package com.iti41g1.tripreminder.Models;

import java.util.ArrayList;

public class TripModel {
    private  String tripName;
    private  String tripType;
    private  Location location;
    private  Date   date;
    private  Date   date2;
    private  Time   time;
    private Time    time2;

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public Time getTime2() {
        return time2;
    }

    public void setTime2(Time time2) {
        this.time2 = time2;
    }

    private ArrayList<NoteModel> notes;

    public TripModel (){
        notes=new ArrayList<>();
    }

    public TripModel(String tripName, String tripType, Location location, Date date, Time time, ArrayList<NoteModel> notes,Date date2,Time time2) {
        this.tripName = tripName;
        this.tripType = tripType;
        this.location = location;
        this.date = date;
        this.date2=date2;
        this.time = time;
        this.time2=time2;
        this.notes = notes;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() {
        return location;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public ArrayList<NoteModel> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteModel> notes) {
        this.notes = notes;
    }


    public static class Time{
        private int hour;
        private int minute;

        public Time(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }
        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }
        public void setMinute(int minute) {
            this.minute = minute;
        }
    }
    static public class Date{
        private int day;
        private  int month;
        private  int year;

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public Date(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }
    }
    public static class Location{
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
