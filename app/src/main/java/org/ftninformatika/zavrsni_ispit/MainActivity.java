package org.ftninformatika.zavrsni_ispit;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.ftninformatika.zavrsni_ispit.DB.DatabaseHelper;
import org.ftninformatika.zavrsni_ispit.DB.Filmovi;

import java.sql.SQLException;
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

        createNotificationChannel();
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
        drawerItems.add("Moji filmovi");
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
                        title = "Moji filmovi";

                        break;
                    case 2:
                        title = "Podesavanja";
                        Intent settings = new Intent(MainActivity.this, Settings.class);
                        startActivity(settings);

                        break;
                    case 3:
                        title = "Obrisi sve";
                        deleteFilmove();
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


    private void deleteFilmove() {
        AlertDialog dialogDelete = new AlertDialog.Builder(this)
                .setTitle("Brisanje nekretnine")
                .setMessage("Da li zelite da obrisete sve filmove?")
                .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            List<Filmovi> filmovi =getDatabaseHelper().getFilmoviDao().queryForAll();

                            getDatabaseHelper().getFilmoviDao().delete(filmovi);
                            for (Filmovi f:filmovi) {
                                getDatabaseHelper().getFilmoviDao().delete(f);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                        finish();
                        boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);
                        boolean notif = prefs.getBoolean(getString(R.string.notif_key), false);

                        if (toast) {
                            Toast.makeText(MainActivity.this, "Izbrisani filmovi", Toast.LENGTH_LONG).show();

                        }

                        if (notif) {
                            showNotification("Izbrisani filmovi");

                        }


                    }
                })
                .setNegativeButton("ODUSTANI", null)
                .show();



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


    public void showNotification(String poruka) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, NOTIF_CHANNEL_ID);
        builder.setSmallIcon(android.R.drawable.ic_input_add);
        builder.setContentTitle("Glumci");
        builder.setContentText(poruka);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_reorder_black_24dp);


        builder.setLargeIcon(bitmap);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void reset() {
        imagePath = "";
        preview = null;
    }
}
