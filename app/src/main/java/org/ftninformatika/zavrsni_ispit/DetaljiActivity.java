package org.ftninformatika.zavrsni_ispit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import org.ftninformatika.zavrsni_ispit.DB.DatabaseHelper;
import org.ftninformatika.zavrsni_ispit.DB.Filmovi;
import org.ftninformatika.zavrsni_ispit.Model.Detalji;
import org.ftninformatika.zavrsni_ispit.Servisi.MyService;

import java.sql.SQLException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetaljiActivity extends AppCompatActivity {


    private Detalji detalji;
    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji);



        prefs = PreferenceManager.getDefaultSharedPreferences(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalji_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_detalji);
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
                addFilm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addFilm() {

        Filmovi film = new Filmovi();
        film.setmNaziv(detalji.getTitle());
        film.setmGodina(detalji.getYear());
        film.setmImage(detalji.getPoster());
        film.setmImdbId(detalji.getImdbID());

        try {
            getDataBaseHelper().getFilmoviDao().create(film);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String tekstNotifikacije = film.getmNaziv() + " je uspesno dodat u omiljene!";

        Toast.makeText(DetaljiActivity.this, tekstNotifikacije, Toast.LENGTH_LONG).show();


    }


    private void getDetail(String imdbKey) {
        HashMap<String, String> queryParams = new HashMap<>();
        //TODO unesi api key
        queryParams.put("apikey", "b0189569");
        queryParams.put("i", imdbKey);


        Call<Detalji> call = MyService.apiInterface().getMovieData(queryParams);
        call.enqueue(new Callback<Detalji>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Detalji> call, Response<Detalji> response) {
                if (response.code() == 200) {
                    Log.d("REZ", "200");

                    detalji = response.body();
                    if (detalji != null) {


                        ImageView image = DetaljiActivity.this.findViewById(R.id.detalji_slika);

                        Picasso.with(DetaljiActivity.this).load(detalji.getPoster()).into(image);


                        TextView title = DetaljiActivity.this.findViewById(R.id.detalji_naziv);
                        title.setText(detalji.getTitle());

                        TextView year = DetaljiActivity.this.findViewById(R.id.detalji_godina);
                        year.setText("(" + detalji.getYear() + ")");

                        TextView runtime = DetaljiActivity.this.findViewById(R.id.detalji_runtime);
                        runtime.setText(detalji.getRuntime());

                        TextView genre = DetaljiActivity.this.findViewById(R.id.detalji_zanr);
                        genre.setText(detalji.getGenre());

                        TextView language = DetaljiActivity.this.findViewById(R.id.detalji_jezik);
                        language.setText(detalji.getLanguage());

                        TextView plot = DetaljiActivity.this.findViewById(R.id.detalji_plot);
                        plot.setText(detalji.getPlot());

                        TextView awards=DetaljiActivity.this.findViewById(R.id.detalji_nagrade);
                        awards.setText(detalji.getAwards());

                    }
                }
            }

            @Override
            public void onFailure(Call<Detalji> call, Throwable t) {
                Toast.makeText(DetaljiActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String imdbKey = getIntent().getStringExtra(PretragaActivity.KEY);
        getDetail(imdbKey);
    }



//    public void addFilm() {
//
//        Filmovi film = new Filmovi();
//        film.setmNaziv(detalji.getTitle());
//        film.setmGodina(detalji.getYear());
//        film.setmImage(detalji.getPoster());
//        film.setmImdbId(detalji.getImdbID());
//
//        try {
//            getDataBaseHelper().getFilmoviDao().create(film);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        String tekstNotifikacije = film.getmNaziv() + " je uspesno dodat u omiljene!";
//
//        Toast.makeText(DetaljiActivity.this, tekstNotifikacije, Toast.LENGTH_LONG).show();
//
//
//    }

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
}




