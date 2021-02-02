package com.iti41g1.tripreminder.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TripDAO {

    @Insert
    long insert(Trip trip);

    @Delete
    void delete(Trip trip);

    @Query("SELECT * FROM Trip WHERE userID LIKE :userId AND id LIKE :id")
    Trip[] search(String userId, int id);

    @Query("SELECT * FROM Trip")
    List<Trip> getAll();

    @Query("DELETE FROM Trip")
    void clear();

}
