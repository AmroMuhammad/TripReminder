package com.iti41g1.tripreminder.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "Trip")
public class Trip  implements Serializable {

    @NonNull
    private String userID;

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String tripName;
    @NonNull
    private  String startPoint;
    @NonNull
    private  String endPoint;
    @NonNull
    private double endPointLat;
    @NonNull
    private double endPointLong;
    @NonNull
    private String date;
    @NonNull
    private  String time;
    @NonNull
    private  int tripImg;
    @NonNull
    private String tripStatus;
    @TypeConverters(DataConverter.class)
    private List<String> notes;

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public Trip(@NonNull String userID, @NonNull String tripName, @NonNull String startPoint,
                @NonNull String endPoint, @NonNull double endPointLat, @NonNull double endPointLong,
                @NonNull String date, @NonNull String time, @NonNull int tripImg, String tripStatus) {
        this.userID = userID;
        this.id = id;
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.endPointLat = endPointLat;
        this.endPointLong = endPointLong;
        this.date = date;
        this.time = time;
        this.tripImg = tripImg;
       // this.notes = notes;
        this.tripStatus = tripStatus;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripImg() {
        return tripImg;
    }

    public void setTripImg(int tripImg) {
        this.tripImg = tripImg;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getEndPointLat() {
        return endPointLat;
    }

    public void setEndPointLat(double endPointLat) {
        this.endPointLat = endPointLat;
    }

    public double getEndPointLong() {
        return endPointLong;
    }

    public void setEndPointLong(double endPointLong) {
        this.endPointLong = endPointLong;
    }

//    public List<String> getNotes() {
//        return notes;
//    }
//
//    public void setNotes(List<String> notes) {
//        this.notes = notes;
//    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
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

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

   /* public String getFirstNote() {
        return notes[0];
    }
    public void setFirstNote(String firstNote) {
        this.notes[0] = firstNote;
    }*/


}
