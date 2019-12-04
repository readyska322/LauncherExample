package ua.com.zzz.onelenyk.finalfinal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    final int MY_PERMISSIONS_REQUEST_CODE = 1478;
    private PackageManager manager;
    private List<AppDetail> apps;
    private GridView list;
    private Button btnLight;
    private Button btnwifi;

    private ContentResolver cResolver;
    private int brightnessValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        cResolver = getContentResolver();

        btnLight = findViewById(R.id.light);
        btnwifi = findViewById(R.id.wifi);

        loadApps();
        loadListView();


        btnLight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowDialog();
            }
        });

        btnwifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WifiActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Location permissions are required to do the task.");
                builder.setTitle("Please grant this permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        } else {

            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();

        }

        if (!hasWriteSettingsPermission(this)) {
            changeWriteSettingsPermission(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                if (

                        (grantResults.length > 0) &&
                                (grantResults[0]
                                        == PackageManager.PERMISSION_GRANTED
                                )

                ) {

                    Toast.makeText(this, "Permissions granted.", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Permissions denied.", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            if (ri.activityInfo.packageName.equals("com.cyanogenmod.eleven")) {
                app.label = "kek";
            }

            apps.add(app);


        }
    }

    @Override
    public void onBackPressed() {

    }

    private void loadListView() {
        RecyclerView list = findViewById(R.id.apps_list);
        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int t = Integer.parseInt(v.getTag().toString());
                Intent i = manager.getLaunchIntentForPackage(apps.get(t).name.toString());
                MainActivity.this.startActivity(i);
            }
        };

        AdapterList adapter = new AdapterList(this,listener,apps);

        list.setAdapter(adapter);
        list.setLayoutManager(new GridLayoutManager(this, 3));
    }

    public void ShowDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(255);
        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Please Select Rank 1-100 ");
        popDialog.setView(seek);
        try {
            brightnessValue = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        seek.setProgress(brightnessValue);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                setBrightness(progress);

            }

            public void onStartTrackingTouch(SeekBar arg0) {


            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        popDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });


        popDialog.create();
        popDialog.show();
    }

    public void setBrightness(int progress) {

        Context context = getApplicationContext();
        boolean settingsCanWrite = hasWriteSettingsPermission(context);

        if (!settingsCanWrite) {
            changeWriteSettingsPermission(context);
        } else {
            changeScreenBrightness(context, progress);
        }
    }

    private void changeScreenBrightness(Context context, int screenBrightnessValue) {

        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
    }


    private boolean hasWriteSettingsPermission(Context context) {

        boolean ret = Settings.System.canWrite(context);
        return ret;

    }

    private void changeWriteSettingsPermission(Context context) {

        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        context.startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }
}