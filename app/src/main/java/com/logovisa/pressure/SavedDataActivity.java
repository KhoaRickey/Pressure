package com.logovisa.pressure;

import android.app.Activity;
import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class SavedDataActivity extends Activity {
    public int id = 0;
    String TAG = "Main";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_data);
        String loadString = readTable();
        //Log.d(TAG, loadString);
        String[] split = loadString.split("\\n");
        TableLayout table = (TableLayout)findViewById(R.id.tableData);
        for(int i = 0; i < split.length; i += 2){
            Log.d(TAG, split[i]);
            TableRow row = new TableRow(this);
            TextView room = new TextView(this);
            TextView roomP = new TextView(this);
            TextView compareP = new TextView(this);
            room.setText(split[i]);
            roomP.setText(split[i+1]);
            row.addView(room);
            row.addView(roomP);
            row.addView(compareP);
            row.setId(id);
            id++;
            table.addView(row);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private String readTable(){
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
