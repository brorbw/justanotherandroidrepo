package com.larusaarhus.weatheraarhus7;


import java.sql.Timestamp;

/**
 * Created by brorbw on 04/05/16.
 */
public class Model {
    private long id;
    private Timestamp timestamp;
    private double temp;
    private String description;

    public Model(long id, Timestamp timestamp, double temp, String description) {
        this.id = id;
        this.timestamp = timestamp;
        this.temp = temp;
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String toString(){
        return "ID: " + id + ", Time: " + timestamp.toString() + ", Temp: " + temp + ", Description: " + description;
    }
    public String bigPrint(){
        return description + "\n" + temp;
    }
}
