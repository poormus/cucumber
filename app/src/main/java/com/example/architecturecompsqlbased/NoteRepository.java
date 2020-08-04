package com.example.architecturecompsqlbased;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
// establishes connnection between database and view
public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    private String [] allTitles;

    // application is a subclass of context..
    public NoteRepository(Application application){
      NoteDatabase noteDatabase=NoteDatabase.getInstance(application);
      noteDao=noteDatabase.noteDao();
      allNotes=noteDao.getAllNotes();




    }

    public void insert(Note note){
      new InsertNoteAsyncTask(noteDao).execute(note);
    }
    public void update(Note note){
     new UpdateNoteAsyncTask(noteDao).execute(note);
    }
    public void delete(Note note){
     new DeleteNoteAsyncTask(noteDao).execute(note);
    }
    public void deleteAllNotes(){
    new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
    public String [] getAllTitles(){
        return allTitles;
    }






    //////////Room wont allow database ??? so we need to create these static classes...
    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;
        private InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }


    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;
        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }
    //our view model will call these methods but wont care about what they do...
    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;
        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;
        private DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}
