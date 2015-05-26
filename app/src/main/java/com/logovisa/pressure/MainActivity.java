package com.logovisa.pressure;


import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements SensorEventListener{
    public float CurrentPressure;
    public float CurrentCompare = 0;
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
            alert.create().show();
        }
        sensorManager.registerListener((SensorEventListener) this, pS, sensorManager.SENSOR_DELAY_FASTEST );

        //Add button handling
        Button add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRow();
            }
        });

        //Compare button handling
        final Button compare = (Button)findViewById(R.id.compare);
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.editText);
                TextView compareValue = (TextView)findViewById(R.id.compareValue);
                CurrentCompare = CurrentPressure;

                //If there is a string in input field, it will be updated next to the Compare Value. If there is none, it just update the compare value
                String inputTxt = input.getText().toString();
                String compare = compareValue.getText().toString();
                String[] compareS = compare.split(":");
                if(inputTxt.matches("")) compareValue.setText(compareS[0] + ": " + CurrentCompare);
                else compareValue.setText("Compare Value (" + input.getText() + ") : " + CurrentCompare);
                updateCompare();
                input.setText("");
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
        CurrentPressure = values[0];
        TextView result = (TextView)findViewById(R.id.resultText);
        //Show current pressure value to user
        result.setText("Current atmospheric pressure:   " + values[0] + " hPa");
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
            TextView comparePressure = (TextView)findViewById(idList.indexOf(i));
            TextView currentPressure = (TextView)findViewById(idList.indexOf(i) + 100);
            String current = currentPressure.getText().toString();
            float currentP = Float.parseFloat(current);
            float newCompare = CurrentPressure - currentP;
            String newCompareTxt = Float.toString(-newCompare);
            comparePressure.setText(newCompareTxt);
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
        float average = sum / calculateList.size();
        TextView calculatedResult = (TextView)findViewById(R.id.calculatedResult);
        calculatedResult.setText("Calculated atmospheric pressure:   " + average + " hPa");
    }

    //Create new table's row function
    void newRow(){
        EditText input = (EditText)findViewById(R.id.editText);
        TableLayout table = (TableLayout)findViewById(R.id.tableData);
        TableRow row = new TableRow(getApplicationContext());

        //update room name from input field
        TextView inputRoom = new TextView(getApplicationContext());
        inputRoom.setText(input.getText());
        row.addView(inputRoom);

        //add current pressure of the room above
        TextView currentPressure = new TextView(getApplicationContext());
        currentPressure.setGravity(Gravity.CENTER_HORIZONTAL);
        currentPressure.setId(id + 100);
        String cP = Float.toString(CurrentPressure);
        currentPressure.setText(cP);
        row.addView(currentPressure);

        //compare Compare value with the room's value
        TextView comparePressure = new TextView(getApplicationContext());
        idList.add(id);
        comparePressure.setId(id);
        float diff;
        if(CurrentCompare == 0) diff = 0;
        else diff = CurrentCompare - CurrentPressure;
        String diffString = Float.toString(-diff);
        comparePressure.setText(diffString);
        row.addView(comparePressure);

        table.addView(row);
        input.setText("");
        id++;
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
