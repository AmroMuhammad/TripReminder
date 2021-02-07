package com.iti41g1.tripreminder.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataConverter{
    @TypeConverter
    public String fromNotesList(ArrayList<String> notes){
        if(notes==null){
            return (null);
        }
        Gson gson=new Gson();
        Type type= new TypeToken<ArrayList<String>>(){}.getType();
        String json=gson.toJson(notes,type);
        return json;
    }
    @TypeConverter
    public ArrayList<String> toNotesList(String notesString){
        if(notesString==null)
            return (null);
        Gson gson=new Gson();
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        return gson.fromJson(notesString,type);
    }

}
