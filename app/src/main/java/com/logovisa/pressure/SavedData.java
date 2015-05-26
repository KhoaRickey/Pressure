package com.logovisa.pressure;

/**
 * Created by Khoa Rickey on 5/26/2015.
 */
public class SavedData {
    private long id;
    private String room;
    private String pressure;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public  String getPressure(){
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return room;
    }
}
