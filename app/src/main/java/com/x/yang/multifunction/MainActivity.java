package com.x.yang.multifunction;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SearchView sv;
    private float videoSize, photoSize, audioSize, apkSize;
    private ViewFlipper mViewFlipper;
    private PieChart pchart;
    private SettingProfile sp;
    private String settings = null;
    private static String t = "This app can be a dashcam and voice recorder\n" +"" +
            "A. dashcam introduction\n"+
            "1. click the button with a triangle sign to start\n" +
            "2. click the button with a square sign to start\n" +
            "3. right-top sign indicates the status of app\n" +
            "4. red dot means recording black double bar means pause\n" +
            "5. you can change the G sensor level and max number of videos\n" +
            "6. when G sensor detects higher value, this video will be stored permanently\n" +
            "7. other normal videos will be remove when reach the max number\n" +
            "\n" +
            "B. voice recorder\n" +
            "1.you can replay the previous one";
    private Fragment f_cam,f_voice;
    private final String filename = "config.txt";
    private Path Path1;
    private FragmentManager fm;
    private static String privous_fra = "";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp= SettingProfile.getSp();
        f_cam = new camFragment();
        f_voice = new videoFragment();


        if (savedInstanceState != null) {
            privous_fra = savedInstanceState.getString("temp");
        }
        setContentView(R.layout.activity_main);

        FileInputStream in = null;
        ByteArrayOutputStream bout = null;
        byte[]buf = new byte[1024];
        bout = new ByteArrayOutputStream();
        int length = 0;
        try {
            in = this.openFileInput(filename);
            while((length=in.read(buf))!=-1){
                    bout.write(buf,0,length);
                }
            byte[] content = bout.toByteArray();

            settings = new String(content,"UTF-8");

            in.close();
            bout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(settings !=null){
            String g = settings.substring(0,2);
            int k = Integer.parseInt(g);
            sp.setG_level(k);
            String num= settings.substring(2);
            k = Integer.parseInt(num);
            sp.setMaxNumberRecords(k);
        }

        fm = getSupportFragmentManager();
        //fm.beginTransaction().add(R.id.fra_cam,f_cam).commit();
        //fm.beginTransaction().add(R.id.fra_cam,f_voice).commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mViewFlipper =(ViewFlipper)findViewById(R.id.viewFlipper);
        mViewFlipper.setFlipInterval(2000);
        mViewFlipper.setAnimation(AnimationUtils.loadAnimation(this,R.anim.aa));
        mViewFlipper.setAutoStart(true);
        //mViewFlipper.startFlipping();

        //showChart(pchart, mPieData);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("temp", privous_fra);
    }

    private class Caculation extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mViewFlipper.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mViewFlipper.setVisibility(View.INVISIBLE);
        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            removeFra();
            privous_fra = "camera";
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Fragment fragment = fm.findFragmentById(R.id.fra_cam);
            if(fragment == null){
                fragment = new camFragment();
                fm.beginTransaction().add(R.id.fra_cam,fragment).commit();
            }

        } else if (id == R.id.nav_audio) {
            removeFra();
            privous_fra="audio";
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Fragment fragment = fm.findFragmentById(R.id.fra_cam);
            if(fragment == null){
                fragment = new videoFragment();
                fm.beginTransaction().add(R.id.fra_cam,fragment).commit();
            }

        }  else if (id == R.id.nav_help) {
            removeFra();
            privous_fra="search";
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            TextView tv = (TextView)findViewById(R.id.helptext);
            if(tv.getText().length() == 0)
            tv.setText(t);
            else
                tv.setText("");

        }else if(id == R.id.nav_set){
            removeFra();
            privous_fra="setting";
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Fragment fragment = fm.findFragmentById(R.id.fra_cam);
            if(fragment == null){
                fragment = new settingFragment();
                fm.beginTransaction().add(R.id.fra_cam,fragment).commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileOutputStream out;

        String setting =Integer.toString(sp.getG_level())+sp.getMaxNumberRecords();

        try {
            out = this.openFileOutput(filename, Context.MODE_WORLD_READABLE);
            out.write(setting.getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeFra(){
        Fragment fragment;
        switch (privous_fra){
            case "camera":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                fm.executePendingTransactions();
                break;
            case "ruler":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                fm.executePendingTransactions();
                break;
            case "audio":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                fm.executePendingTransactions();
                break;
            case "apks":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                fm.executePendingTransactions();
                break;

            case "search":      //search is help

                break;
            case "setting":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                fm.executePendingTransactions();
                break;
            default:
                ;
                break;
        }

    }
}
