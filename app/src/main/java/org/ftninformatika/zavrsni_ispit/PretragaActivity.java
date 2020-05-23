package org.ftninformatika.zavrsni_ispit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ftninformatika.zavrsni_ispit.DB.DatabaseHelper;
import org.ftninformatika.zavrsni_ispit.Model.Example;
import org.ftninformatika.zavrsni_ispit.Model.Search;
import org.ftninformatika.zavrsni_ispit.Servisi.MyService;
import org.ftninformatika.zavrsni_ispit.adapteri.MyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PretragaActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Button btnSearch;
    EditText movieName;
    int position = 0;
    private DatabaseHelper databaseHelper;
    public static String KEY = "KEY";
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretraga);






        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int position = getIntent().getExtras().getInt("position", 0);

        btnSearch = findViewById(R.id.btn_search);
        movieName = findViewById(R.id.ime_filma);
        recyclerView = findViewById(R.id.rvLista);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovieByName(movieName.getText().toString());
            }
        });
    }



    private void getMovieByName(String name) {
        Map<String, String> query = new HashMap<>();
        query.put("apikey", "b0189569");
        query.put("s", name.trim());

        Call<Example> call = MyService.apiInterface().getMovieByName(query);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                if (response.code() == 200) {
                    try {
                        Example searches = response.body();

                        ArrayList<Search> search = new ArrayList<>();

                        for (Search e : searches.getSearch()) {

                            if (e.getType().equals("movie") || e.getType().equals("series")) {
                                search.add(e);
                            }
                        }

                        layoutManager = new LinearLayoutManager(PretragaActivity.this);
                        recyclerView.setLayoutManager(layoutManager);

                        adapter = new MyAdapter(PretragaActivity.this, search, PretragaActivity.this);
                        recyclerView.setAdapter(adapter);
                        btnSearch.setVisibility(View.GONE);

                        Toast.makeText(PretragaActivity.this, "Prikaz filmova/serija.", Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        Toast.makeText(PretragaActivity.this, "Ne postoji film/serija sa tim nazivom", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(PretragaActivity.this, "Greska sa serverom", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(PretragaActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        Search filmovi = adapter.get(position);
        Intent i = new Intent(PretragaActivity.this, DetaljiActivity.class);
        i.putExtra(KEY, filmovi.getImdbID());
        i.putExtra("id", filmovi.getTitle());
        startActivity(i);
    }

}





