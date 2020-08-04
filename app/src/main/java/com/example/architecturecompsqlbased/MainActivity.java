package com.example.architecturecompsqlbased;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

// in the manifest we added -android:parentActivityName=".MainActivity"- to addnoteactivity so the x closeimage goes back to main activity
// also we added -android:launchMode="singleTop"- to main activity so instead of recreating it with the x press it just
//got back to mainactivity
public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST=1;
    public static final int EDIT_NOTE_REQUEST=2;
    private NoteViewModel noteViewModel;
    private SharedPreferences preferences;
    NoteAdapter mAdapter;
    SearchView searchView;
    Spinner spinner,spinner2,spinner3;
    String spinnerText;
    RecyclerView recyclerView;
    int spacing;
    GridSpacingItemDecoration spacingItemDecoration;
    String a;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem deleteAllNotes=menu.findItem(R.id.delete_all_notes);
        deleteAllNotes.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, "all notes deleted", Toast.LENGTH_SHORT).show();
                deleteall();
                return true;
            }
        });
        MenuItem search=menu.findItem(R.id.app_bar_search);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView= (SearchView) search.getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("search notes by title...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                 mAdapter.getFilter().filter(newText);
                 return true;
            }
        });
        return true;
    }
    public void deleteall(){
        noteViewModel.deleteAllNotes();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.edit().putInt("spinner3", spinner.getSelectedItemPosition()).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int position=preferences.getInt("spinner3",-1);
        spinner.setSelection(position);
        Toast.makeText(this, "here "+a, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.my_recycle_view);
       spacingItemDecoration=new GridSpacingItemDecoration(2,40,true);
       preferences=getSharedPreferences("pref",MODE_PRIVATE);


        mAdapter = new NoteAdapter();
        noteViewModel=new ViewModelProvider(this).get(NoteViewModel.class);
        recyclerView.setAdapter(mAdapter);
        noteViewModel.getAllNotes().observe(this, notes -> {
                    //update RecyclerView
                    mAdapter.submitList(notes);
                    mAdapter.setNote(notes);
                });


        initSpinner();


        FloatingActionButton addNewNoteButton=findViewById(R.id.button_add_note);
        addNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we started activity for result so we can get the results back...
                Intent intent=new Intent(MainActivity.this,AddNoteActivity.class);
                //we did not hardcode the requestcode because we can request different results
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }
        });

        //to make the app swipable and deletable...
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
                //for drag and drop..
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(mAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
                new RecyclerViewSwipeDecorator.Builder(MainActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.REDCOLOR))
                        .addActionIcon(R.drawable.ic_delete_black)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerView);

        adapterOnClick();

        mAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Note note,int id,int position,View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddNoteActivity.EXTRA_PRIORITY, note.getPriority());
                Toast.makeText(MainActivity.this, "here "+id+" "+position, Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)


    private void adapterOnClick() {
        mAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Note note, int id, int position, View view) {

            }
        });
    }

    private void initSpinner() {
        spinner =findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.viewtype,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             spinnerText=parent.getItemAtPosition(position).toString();
             switch(spinnerText){
                 case "list":

                     recycleViewList();
                     recyclerView.removeItemDecoration(spacingItemDecoration);

                     break;
                 case "grid":

                     recycleViewGrid();
                     recyclerView.addItemDecoration(spacingItemDecoration);
                     break;
                 default:
                     break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void recycleViewGrid() {
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);



    }

    private void recycleViewList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

    }

    //to receive the info from the addnoteactivity we override another method below..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1);
            Note note = new Note(title, description, priority);
            //to insert to database...
            noteViewModel.insert(note);
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1);
            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        }

        else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
