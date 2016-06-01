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
import android.widget.SearchView;
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
            String g = settings.substring(0,1);
            int k = Integer.parseInt(g);
            sp.setG_level(k);
            String num= settings.substring(1);
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
        sv = (SearchView)findViewById(R.id.searchView);
        sv.setVisibility(View.INVISIBLE);
        mViewFlipper =(ViewFlipper)findViewById(R.id.viewFlipper);

        //mViewFlipper.startFlipping();
        pchart = (PieChart) findViewById(R.id.spread_pie_chart);
        PieData mPieData = getPieData(4, 100);
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

    private void showChart(PieChart pieChart, PieData pieData) {
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(64f);
        pieChart.setDescription("");
        pieChart.setDrawCenterText(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90);
        pieChart.setRotationEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("usage of memory space");
        pieChart.setData(pieData);

        Legend mLegend = pieChart.getLegend();
        mLegend.setEnabled(false);
       // mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        //mLegend.setXEntrySpace(7f);
       // mLegend.setYEntrySpace(5f);

        pieChart.animateXY(1000, 1000);

    }

    private PieData getPieData(int count, float range) {

        long blockSize=0;
        long blockCount=0;
        long availCount=0;
        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("video");
        xValues.add("photo");
        xValues.add("audio");
        xValues.add("apks");
        xValues.add("other");
        xValues.add("free");

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            blockSize = sf.getBlockSize();
            blockCount = sf.getBlockCount();
            availCount = sf.getAvailableBlocks();
            Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
            Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");
        }

        ArrayList<Entry> yValues = new ArrayList<Entry>();

        float quarterly1 = 14;
        float quarterly2 = 14;
        float quarterly3 = 34;
        float quarterly4 = 12;
        float quarterly5 = 12;
        float quarterly6 = (float) (availCount*100/blockCount);

        yValues.add(new Entry(quarterly1, 0));
        yValues.add(new Entry(quarterly2, 1));
        yValues.add(new Entry(quarterly3, 2));
        yValues.add(new Entry(quarterly4, 3));
        yValues.add(new Entry(quarterly5, 4));
        yValues.add(new Entry(quarterly6, 5));

        PieDataSet pieDataSet = new PieDataSet(yValues, "total memory space:"+(float)(blockSize*blockCount/1024/1024/1024)+"GB");
        pieDataSet.setSliceSpace(0f);

        ArrayList<Integer> colors = new ArrayList<Integer>();


        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(114, 188, 223));
        colors.add(Color.rgb(255, 123, 124));
        colors.add(Color.rgb(57, 135, 200));

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(16f);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px);

        PieData pieData = new PieData(xValues, pieDataSet);

        return pieData;
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

        }else if(id == R.id.nav_set){
            removeFra();
            privous_fra="setting";
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            out = this.openFileOutput(filename, Context.MODE_PRIVATE);
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
                break;
            case "ruler":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                break;
            case "audio":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                break;
            case "apks":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();

            case "search":      //search is help
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                break;
            case "setting":
                fragment = fm.findFragmentById(R.id.fra_cam);
                fm.beginTransaction().remove(fragment).commit();
                break;
            default:
                ;
                break;
        }
        fm.executePendingTransactions();
    }
}
