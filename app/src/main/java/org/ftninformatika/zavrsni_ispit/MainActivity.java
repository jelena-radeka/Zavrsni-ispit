package org.ftninformatika.zavrsni_ispit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import org.ftninformatika.zavrsni_ispit.DB.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int SELECT_PICTURE = 1;
    private static final String TAG = "PERMISSIONS";
    private String imagePath = null;
    private ImageView preview;
    List<String> drawerItems;
    private int position;
    DrawerLayout drawerLayout;
    ListView drawerList;
    private RelativeLayout drawerPane;
    private ActionBarDrawerToggle drawerToggle;

    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    public static String GLUMAC_KEY = "GLUMAC_KEY";

    private Toolbar toolbar;

    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillData();
        setupToolbar();
        setupDrawer();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


        }

        return super.onOptionsItemSelected(item);
    }


    public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_reorder_black_24dp);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();

        }
    }

    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add("Pretraga filmova");
        drawerItems.add("Pregled svih filmova");
        drawerItems.add("Podesavanja");
        drawerItems.add("Obrisi sve");

    }

    private void setupDrawer() {
        drawerList = findViewById(R.id.drawerList);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerPane = findViewById(R.id.drawerPane);

        // drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems));


        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = "Unknown";
                switch (position) {
                    case 0:
                        title = "Pretraga filmova";
                        pretragaFilmova();
                        break;
                    case 1:
                        title = "Pregled svih filmova";
                        //pretragaFilmova();
                        break;
                    case 2:
                        title = "Podesavanja";
                        break;
                    case 3:
                        title = "Obrisi sve";
                        break;
                    default:
                        break;
                }
                drawerList.setItemChecked(position, true);
                setTitle(title);
                drawerLayout.closeDrawer(drawerPane);
            }
        });
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems));

        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

        drawerToggle.onConfigurationChanged(configuration);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", position);
    }


    private void pretragaFilmova() {
        Intent intent=new Intent(MainActivity.this,PretragaActivity.class);
        intent.putExtra("position","movie");
        startActivity(intent);

    }

}
