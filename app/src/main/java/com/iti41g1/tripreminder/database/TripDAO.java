package com.iti41g1.tripreminder.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TripDAO {

    @Insert
    long insert(Trip trip);

    @Delete
    void delete(Trip trip);

    @Query("DELETE FROM Trip where id=:id AND userID= :userId ")
    void deleteById(String userId, int id);

    @Query("SELECT * FROM Trip WHERE userID LIKE :userId AND id LIKE :id")
    List<Trip> search(String userId, int id);

    @Query("SELECT * FROM Trip WHERE id = :id ")
    Trip selectById(int id);

    @Query("SELECT * FROM Trip WHERE userId  = :userId And(tripStatus LIKE :cancleStatus Or tripStatus LIKE :finishedStatus) ")
    List<Trip> selectHistoryTrip(String userId, String cancleStatus, String finishedStatus);

    @Query("SELECT * FROM Trip WHERE userId  = :userId And tripStatus LIKE :status ")
    List<Trip> selectUpcomingTrip(String userId, String status);


    @Query("UPDATE Trip SET tripStatus = :tripStatus WHERE id = :id And userID= :userId")
    int updateTrip(String userId, int id, String tripStatus);

    @Query("UPDATE Trip SET tripName = :tripName , startPoint =:startPoint , endPoint =:endPoint , endPointLat=:endPointLat, endPointLong=:endPointLong, date =:date , time=:time WHERE id = :id")
    int EditTrip(int id, String tripName,String startPoint,String endPoint,double endPointLat,double endPointLong,String date,String time);

    @Query("UPDATE Trip SET notes = :notes WHERE id = :id")
    int EditNotes(int id, String notes);
}
