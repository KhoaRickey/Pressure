package com.logovisa.pressure;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements SensorEventListener{
    public String CurrentPressure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get the reference to the sensor manager
        SensorManager sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        // Get the list of sensor
        /**List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        List<Map<String, String>> sensorData = new ArrayList<Map<String,String>>();

        //Show sensors info to user
        StringBuilder data = new StringBuilder();
        for(Sensor sensor: sensorList){
            data.append(sensor.getName() + "\n");
            data.append(sensor.getVendor() + "\n");
            data.append(sensor.getVersion() + "\n");
        }

        TextView ssData = (TextView)findViewById(R.id.ssData);
        ssData.setText(data);**/
        // Look for barometer sensor
        Sensor pS = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener((SensorEventListener) this, pS,SensorManager.SENSOR_DELAY_NORMAL);

        //Add button handling
        Button add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.editText);
                //StringBuilder userInput = new StringBuilder();
                //userInput.append(input.getText() + ": " + CurrentPressure + "\n");
                TextView userInputView = (TextView)findViewById(R.id.userInput);
                userInputView.append(input.getText() + ": " + CurrentPressure + " hPa" + "\n");
                TextView resultText1 = (TextView)findViewById(R.id.resultText1);
                resultText1.setVisibility(View.VISIBLE);
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
        DecimalFormat df = new DecimalFormat("#.####");
        String dx=df.format(values[0]);
        CurrentPressure = dx;
        TextView result = (TextView)findViewById(R.id.resultText);
        //Show current pressure value to user
        result.setText("Current atmospheric pressure:   " + dx + " hPa");
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
}
