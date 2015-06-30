package com.logovisa.pressure;

import android.app.Activity;
import android.app.ListActivity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SavedDataActivity extends Activity implements SensorEventListener{
    public int id = 0;
    public List idList = new ArrayList();
    MainActivity main = new MainActivity();
    public ArrayList<Float> calculateList = new ArrayList<>();
    int CalculatedPressure;
    int CurrentCompare = 0;
    public SensorManager sensorManager;
    public Sensor pS;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_data);
        String loadString = readTable();
        //Log.d(TAG, loadString);
        TableLayout table = (TableLayout)findViewById(R.id.tableData);
        if(loadString == ""){
            TableRow row = new TableRow(this);
            TextView announce = new TextView(this);
            announce.setText("Empty");
            row.addView(announce);
            table.addView(row);
        }
        else {
            String[] split = loadString.split("\\n");
            for (int i = 0; i < split.length; i += 2) {
                TableRow row = new TableRow(this);
                TextView room = new TextView(this);
                TextView roomP = new TextView(this);
                roomP.setGravity(Gravity.CENTER_HORIZONTAL);
                TextView compareP = new TextView(this);
                TextView Date = new TextView(this);
                //Skip a row if string is a date
                if(split[i].matches("[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}")){
                    Date.setText(split[i]);
                    row.addView(Date);
                }
                else {
                    room.setText(split[i]);
                    roomP.setText(split[i + 1]);
                    row.addView(room);
                    row.addView(roomP);
                    row.addView(compareP);
                    row.setId(id);
                    idList.add(id);
                    id++;
                }
                table.addView(row);
            }
        }

        //Get the reference to the sensor manager
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        // Look for barometer sensor
        pS = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener((SensorEventListener) this, pS, sensorManager.SENSOR_DELAY_FASTEST );

        //Clear button handling
        final Button clear = (Button)findViewById(R.id.clearsave);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    OutputStreamWriter outputFile = new OutputStreamWriter(openFileOutput("savePressure.txt", MODE_PRIVATE));
                    outputFile.flush();
                    outputFile.close();
                    TableLayout table = (TableLayout)findViewById(R.id.tableData);
                    table.removeAllViews();
                }catch (IOException e) {
                    e.printStackTrace();
                }
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

                if(CurrentCompare == 0) compareValue.setText(R.string.wait);
                else {
                    //If there is a string in input field, it will be updated next to the Compare Value. If there is none, it just update the compare value
                    String inputTxt = input.getText().toString();
                    String compare = compareValue.getText().toString();
                    String[] compareS = compare.split(":");
                    if (inputTxt.matches(""))
                        compareValue.setText(compareS[0] + ": " + CurrentCompare);
                    else
                        compareValue.setText(getText(R.string.comparevalue) + " (" + input.getText() + ") : " + CurrentCompare);
                    updateCompare();
                    input.setText("");
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        calculateList.add(values[0]);
        if(calculateList.size() == 50){
            calculateAverage(calculateList);
            calculateList.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    String readTable(){
        String result = "";
        try {
            InputStream inputStream = openFileInput("savePressure.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            return result;
        } catch (IOException e) {
            return result;
        }
        return result;
    }

    void updateCompare(){
        for(int i = 0; i < idList.size(); i++){
            //TextView comparePressure = (TextView)findViewById(idList.indexOf(i));
            //TextView currentPressure = (TextView)findViewById(idList.indexOf(i) + 100);
            TableRow row = (TableRow)findViewById(idList.indexOf(i));
            TextView comparePressure = (TextView)row.getChildAt(2);
            TextView currentPressure = (TextView)row.getChildAt(1);
            String current = currentPressure.getText().toString();
            if(!current.equals(" ")){
            int currentP = Integer.parseInt(current);
            int newCompare = CurrentCompare - currentP;
            String newCompareS = Integer.toString(-newCompare);
            comparePressure.setText(newCompareS);
            }
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
        Log.d("Main",Integer.toString(average));
    }
}
