package org.ftninformatika.zavrsni_ispit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.zavrsni_ispit.DB.DatabaseHelper;
import org.ftninformatika.zavrsni_ispit.DB.Filmovi;
import org.ftninformatika.zavrsni_ispit.adapteri.AdapterMojiFilmovi;

import java.sql.SQLException;
import java.util.List;

public class MojiFilmovi extends AppCompatActivity implements AdapterMojiFilmovi.OnItemClickListener {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private AdapterMojiFilmovi adapterLista;
    private List<Filmovi> filmovi;
    private SharedPreferences prefs;

    public static String KEY = "KEY";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moji_filmovi);
        //  setupToolbar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        recyclerView = findViewById(R.id.rvLista);
        //setupToolbar();

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
    public void onItemClick(int position) {
//        Filmovi film = adapterLista.get(position);
//
//        Intent i = new Intent(OmiljeniActivity.this, DetaljiOmiljeni.class);
//        i.putExtra(KEY, film.getmImdbId());
//        i.putExtra("id", film.getmId());
//        startActivity(i);
        // deleteFilmove();

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