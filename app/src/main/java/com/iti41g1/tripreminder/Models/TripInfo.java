package com.iti41g1.tripreminder.Models;

import java.sql.Time;
import java.util.Date;

public class TripInfo {
  private   int tripImg;
  private String name;
  private String date;
  private  String time;
  private  String source;
  private  String destination;
  private  String [] notes;

    public int getTripImg() {
        return tripImg;
    }

    public void setTripImg(int tripImg) {
        this.tripImg = tripImg;
    }

    public TripInfo(int tripImg,String name, String date, String time, String source, String destination, String[] notees) {
        this.tripImg=tripImg;
        this.name = name;
        this.date = date;
        this.time = time;
        this.source = source;
        this.destination = destination;
        this.notes = notees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String[] getNotees() {
        return notes;
    }
    public void setNotees(String[] notees) {
        this.notes = notees;
    }
   /* public String getFirstNote() {
        return notes[0];
    }
    public void setFirstNote(String firstNote) {
        this.notes[0] = firstNote;
    }*/


}
