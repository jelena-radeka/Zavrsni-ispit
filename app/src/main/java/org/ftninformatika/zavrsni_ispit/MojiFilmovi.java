package org.ftninformatika.zavrsni_ispit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.zavrsni_ispit.DB.DatabaseHelper;
import org.ftninformatika.zavrsni_ispit.DB.Filmovi;
import org.ftninformatika.zavrsni_ispit.adapteri.AdapterMojiFilmovi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MojiFilmovi extends AppCompatActivity implements AdapterMojiFilmovi.OnItemClickListener {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private AdapterMojiFilmovi adapterLista;
    private List<Filmovi> filmovi;
    private SharedPreferences prefs;
    AdapterMojiFilmovi adapterMojiFilmovi;
    public static String KEY = "KEY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moji_filmovi);
        //  setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        recyclerView = findViewById(R.id.rvLista);
        setupToolbar();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {

            filmovi = getDataBaseHelper().getFilmoviDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        adapterLista = new AdapterMojiFilmovi(this, filmovi, this);
        recyclerView.setAdapter(adapterLista);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mojifilmovi, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarmoji);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_film:
               deleteFilmove();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteFilmove() {
        AlertDialog dialogDelete = new AlertDialog.Builder(this)
                .setTitle("Brisanje nekretnine")
                .setMessage("Da li zelite da obrisete sve filmove?")
                .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            ArrayList<Filmovi> filmoviZaBrisanje = (ArrayList<Filmovi>) getDataBaseHelper().getFilmoviDao().queryForAll();
                            getDataBaseHelper().getFilmoviDao().delete(filmoviZaBrisanje);


                            adapterMojiFilmovi.removeAll();
                            adapterMojiFilmovi.notifyDataSetChanged();



//                        boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
//                        boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);
//
//                        if (toast) {
//                            Toast.makeText(MainActivity.this, "Izbrisani filmovi", Toast.LENGTH_LONG).show();
//
//                        }
//
//                        if (notif) {
//                            showNotification("Izbrisani filmovi");
//
//                        }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        finish();
                    }
                })
                .setNegativeButton("ODUSTANI", null)
                .show();



    }

    @Override
    public void onItemClick(int position) {
        Filmovi film = adapterLista.get(position);

        Intent i = new Intent(MojiFilmovi.this, DetaljiActivity.class);
        i.putExtra(KEY, film.getmImdbId());
        i.putExtra("id", film.getmId());
        startActivity(i);
         //deleteFilmove();

    }

    private void refresh() {

        RecyclerView recyclerView = findViewById(R.id.rvLista);
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            List<Filmovi> film = null;
            try {

                film = getDataBaseHelper().getFilmoviDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            AdapterMojiFilmovi adapter = new AdapterMojiFilmovi(this, film, this);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDataBaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}