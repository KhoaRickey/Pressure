package com.logovisa.pressure;


import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements SensorEventListener{
    public int CurrentPressure;
    public int CurrentCompare = 0;
    public int CalculatedPressure = 0;
    public int id = 0;
    public List idList = new ArrayList();
    public ArrayList<Float> calculateList = new ArrayList<>();
    public SensorManager sensorManager;
    public Sensor pS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Tell user that average pressure is being calculated
        TextView CalculatedResult = (TextView)findViewById(R.id.calculatedResult);
        CalculatedResult.setText("Average Pressure is being calculated...");

        //Get the reference to the sensor manager
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        // Look for barometer sensor
        pS = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pS == null){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Your device doesn't have barometer sensor");
            alert.setCancelable(false);
            alert.setPositiveButton("Exit",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    System.exit(0);
                }
            });
            //alert.create().show();
        }
        sensorManager.registerListener((SensorEventListener) this, pS, sensorManager.SENSOR_DELAY_FASTEST );

        //Add button handling
        Button add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CalculatedPressure == 0) {
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                    alertDlg.setCancelable(true);
                    alertDlg.setMessage("Pressure is being calculated, please keep the phone stable and wait...");
                    alertDlg.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // We do nothing
                        }
                    });
                    alertDlg.create().show();
                }
                else newRow();
            }
        });

        //Compare button handling
        final Button compare = (Button)findViewById(R.id.compare);
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.editText);
                TextView compareValue = (TextView)findViewById(R.id.compareValue);
                CurrentCompare = CalculatedPressure;

                if(CurrentCompare == 0) compareValue.setText("Average Value is being calculated, please wait!");
                else {
                    //If there is a string in input field, it will be updated next to the Compare Value. If there is none, it just update the compare value
                    String inputTxt = input.getText().toString();
                    String compare = compareValue.getText().toString();
                    String[] compareS = compare.split(":");
                    if (inputTxt.matches(""))
                        compareValue.setText(compareS[0] + ": " + CurrentCompare);
                    else
                        compareValue.setText("Compare Value (" + input.getText() + ") : " + CurrentCompare);
                    updateCompare();
                    input.setText("");
                }
            }
        });

        //Save button handling
        final Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTable();
            }
        });

        //Saved Room button handling
        final Button showSave = (Button)findViewById(R.id.saveView);
        showSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent act2 = new Intent(view.getContext(),SavedDataActivity.class);
                startActivity(act2);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        //Convert float value to string with desired format
        //DecimalFormat df = new DecimalFormat("#.####");
        //String dx=df.format(values[0]);
        CurrentPressure = Math.round(values[0] * 100);
        TextView result = (TextView)findViewById(R.id.resultText);
        //Show current pressure value to user
        result.setText("Current atmospheric pressure:   " + CurrentPressure + " Pa");
        calculateList.add(values[0]);
        if(calculateList.size() == 50){
            calculateAverage(calculateList);
            calculateList.clear();
        }
    }

    @Override
    public void onBackPressed() {
        createExitMsgBox();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this, pS, sensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createExitMsgBox()
    {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Oletko varma että haluat sulkea sovelluksen?");
        alertDlg.setCancelable(false); // We avoid that the dialong can be cancelled, forcing the user to choose one of the options
        alertDlg.setPositiveButton("Kyllä", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                }
        );

        alertDlg.setNegativeButton("Ei", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // We do nothing

            }

        });
        alertDlg.create().show();
    }

    //Comparing functon and update the values
    void updateCompare(){
        for(int i = 0; i < idList.size(); i++){
            //TextView comparePressure = (TextView)findViewById(idList.indexOf(i));
            //TextView currentPressure = (TextView)findViewById(idList.indexOf(i) + 100);
            TableRow row = (TableRow)findViewById(idList.indexOf(i));
            TextView comparePressure = (TextView)row.getChildAt(2);
            TextView currentPressure = (TextView)row.getChildAt(1);
            String current = currentPressure.getText().toString();
            int currentP = Integer.parseInt(current);
            int newCompare = CurrentCompare - currentP;
            comparePressure.setText(-newCompare);
        }
    }

    //Calculate average pressure value over a period of time
    void calculateAverage(ArrayList<Float> calculateList){
        float sum = 0;
        Collections.sort(calculateList);
        for(int i=0; i <= 9; i++){
            calculateList.remove(i);
        }
        for(int i=29; 30 == calculateList.size(); i++){
            calculateList.remove(i);
        }
        for(int i = 0; i < calculateList.size(); i++){
            sum += calculateList.get(i);
        }
        int average = Math.round((sum / calculateList.size()) * 100);
        CalculatedPressure = average;
        TextView calculatedResult = (TextView)findViewById(R.id.calculatedResult);
        calculatedResult.setText("Calculated atmospheric pressure:   " + average + " Pa");
    }

    //Create new table's row function
    void newRow(){
        EditText input = (EditText)findViewById(R.id.editText);
        TableLayout table = (TableLayout)findViewById(R.id.tableData);
        TableRow row = new TableRow(getApplicationContext());
        row.setId(id);
        idList.add(id);

        //update room name from input field
        TextView inputRoom = new TextView(getApplicationContext());
        inputRoom.setText(input.getText());
        row.addView(inputRoom);

        //add current pressure of the room above
        TextView currentPressure = new TextView(getApplicationContext());
        currentPressure.setGravity(Gravity.CENTER_HORIZONTAL);
        currentPressure.setText(CalculatedPressure);
        row.addView(currentPressure);

        //compare Compare value with the room's value
        TextView comparePressure = new TextView(getApplicationContext());
        int diff;
        if(CurrentCompare == 0) diff = 0;
        else diff = CurrentCompare - CalculatedPressure;
        comparePressure.setText(-diff);
        row.addView(comparePressure);

        table.addView(row);
        input.setText("");
        id++;
    }

    //Saving function handling
    void saveTable(){
        File savePressure = new File("savePressure.txt");
        StringBuilder data = new StringBuilder();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String currentDay = day + "/" + month + "/" + year;
        for(int i = 0; i < idList.size(); i++) {
            TableRow row = (TableRow)findViewById(idList.indexOf(i));
            TextView room = (TextView)row.getChildAt(0);
            TextView currentP = (TextView)row.getChildAt(1);
            data.append(room.getText() + "\n");
            data.append(currentP.getText() + "\n");
        }
        data.append(currentDay + "\n");
        data.append(" " + "\n");
        try {
                StringBuilder oldData = new StringBuilder();
                try {
                    InputStream inputStream = openFileInput("savePressure.txt");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        oldData.append(receiveString).append("\n");
                    }

                    inputStream.close();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                oldData.append(data);
                OutputStreamWriter outputFile = new OutputStreamWriter(openFileOutput("savePressure.txt", MODE_PRIVATE));
                outputFile.write(oldData.toString());
                outputFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //List device's sensor list function
    void listDeviceSensor(){
        // Get the list of sensor
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

         List<Map<String, String>> sensorData = new ArrayList<Map<String,String>>();

         //Show sensors info to user
         StringBuilder data = new StringBuilder();
         for(Sensor sensor: sensorList){
         data.append(sensor.getName() + "\n");
         data.append(sensor.getVendor() + "\n");
         data.append(sensor.getVersion() + "\n");
         }

        LinearLayout mainView = (LinearLayout)findViewById(R.id.mainView);
        TextView ssData = new TextView(this);
        ssData.setText(data);
        mainView.addView(ssData);
    }
}
