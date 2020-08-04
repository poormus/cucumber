package com.example.architecturecompsqlbased;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import java.util.List;

//dao accesses to the database
// create one dao per entity
// data access object
@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);
    @Update
    void update(Note note);
    @Delete
    void delete(Note note);
    //some methods such as delete all are not ready made so we query them instead...
    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table ORDER BY priority DESC")
    //live data is observable means app will be notified...
    LiveData<List<Note>> getAllNotes();






}
