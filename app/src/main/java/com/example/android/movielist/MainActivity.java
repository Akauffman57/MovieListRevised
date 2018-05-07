package com.example.android.movielist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String[] movies = {"JAWS", "Airplane!", "Raiders of the Lost Ark", "Ghostbusters", "Groundhog Day", "Dumb and Dumber"};
    public static final String[] IMBDcode = {"tt0073195", "tt0080339", "tt0082971", "tt0087332", "tt0107048", "tt0109686"};
    public static ListView listView;
    public static ArrayList<String> movieTitles, movieIMBD;
    public static final String preurl = "https://www.imdb.com/title/";
    public static String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.movielist.R.layout.activity_main);
        listView = findViewById(com.example.android.movielist.R.id.list);
        movieTitles = new ArrayList<String>();
        movieIMBD = new ArrayList<String>();

        load(this);

        for (int i = 0; i < movies.length; i++) {
            movieTitles.add(movies[i]);
            movieIMBD.add(IMBDcode[i]);
        }

        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, com.example.android.movielist.R.layout.list_item_view, movieTitles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        url = preurl + movieIMBD.get(i);
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(in);
                    }
                }
        );

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Remove this movie from list?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int j) {
                        movieTitles.remove(pos);
                        movieIMBD.remove(pos);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Movie Deleted", Toast.LENGTH_SHORT).show();
                        listView.setAdapter(adapter);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

    }

    public void add(View view) {
        Intent intent = new Intent(this, additem.class);
        startActivityForResult(intent, 1);
    }

@Override
    public void onActivityResult(int requestcode, int resultcode,Intent sendback) {
        if (resultcode == RESULT_OK) {
                movieTitles.add(sendback.getStringExtra("Title"));
                movieIMBD.add(sendback.getStringExtra("Code"));
                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item_view, movieTitles);
                listView.setAdapter(adapter);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = p.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(movieTitles);
        set.addAll(movieIMBD);
        editor.putInt("Size",movieTitles.size());

        for (int i = 0; i<movieTitles.size();i++){
            editor.remove("Title"+i);
            editor.remove("Code"+i);
            editor.putString("Title"+i+"", movieTitles.get(i));
            editor.putString("Code"+i+"",movieIMBD.get(i));
        }
        editor.commit();
    }

    public void load(Context context){
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        movieTitles.clear();
        movieIMBD.clear();
        int size = p.getInt("Size", 0);
        for(int i = 0; i<size; i++){
            movieTitles.add(p.getString("Title" + i, null));
            movieIMBD.add(p.getString("Code" + i, null));
        }
    }
}





