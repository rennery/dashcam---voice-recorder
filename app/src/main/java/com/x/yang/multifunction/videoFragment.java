package com.x.yang.multifunction;


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class videoFragment extends Fragment {


    private ImageButton start,end;
    private Button pl,stp;
    private TextView textView;
    private File RecVedioPath;
    private String FileName = null;
    private String FilePath = null;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private boolean started,stoped, hasprv,playing,paused;

    private int time,min,sec,hour;

    public videoFragment() {
        // Required empty public constructor
        time = min = sec = hour = 0;
        //FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        RecVedioPath = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/hfdatabase/voice/temp/");
        if (!RecVedioPath.exists()) {
            RecVedioPath.mkdirs();
        }
        FilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/hfdatabase/voice/temp/";

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        start = (ImageButton) v.findViewById(R.id.start_recorder);
        start.setOnClickListener(new clickImage());
        end = (ImageButton) v.findViewById(R.id.stop_recorder);
        end.setOnClickListener(new clickImage());
        pl = (Button) v.findViewById(R.id.play_last);
        pl.setOnClickListener(new clickImage());
        stp=(Button) v.findViewById(R.id.stop_last);
        stp.setVisibility(View.INVISIBLE);
        stp.setOnClickListener(new clickImage());
        textView = (TextView) v.findViewById(R.id.time_recorder);
        started = false;
        stoped = true;
        hasprv = false;
        playing = false;
        paused = true;
        Timer t = new Timer();
        t.schedule(new timer_sec(),0,1000);
        return v;
    }

    private class clickImage implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.start_recorder){
                if(stoped) {
                    Calendar calendar = Calendar.getInstance();
                    FileName = FilePath + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR) +
                            calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND)+".3gp";
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(FileName);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        if(playing == true){
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;
                            paused = true;
                            playing = false;
                            new playbutton().execute();
                        }
                        mRecorder.prepare();
                        mRecorder.start();
                        time = min = sec = hour = 0;
                        started = true;
                        stoped = false;
                        Toast.makeText(videoFragment.this.getActivity(),"voice recording started",Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        mRecorder.release();
                        mRecorder = null;
                        Log.e("voice", "prepare() failed");
                    }
                }

            }else if(v.getId() == R.id.stop_recorder){
                if(started) {
                    mRecorder.stop();
                    mRecorder.release();
                    Toast.makeText(videoFragment.this.getActivity(),"voice recording stop",Toast.LENGTH_LONG).show();
                    hasprv = true;
                    mRecorder = null;
                    started = false;
                    stoped = true;
                }
            }else if(v.getId() == R.id.play_last){
                if (pl.getText().toString().contains("play")){
                    if(playing){
                        mPlayer.start();
                        paused = false;
                        new playbutton().execute();
                    }else
                    if (hasprv) {
                        if (stoped) {
                            if (mPlayer != null) {
                                mPlayer.release();
                                mPlayer = null;
                            }
                            mPlayer = new MediaPlayer();
                            try {
                                mPlayer.setDataSource(FileName);
                                mPlayer.prepare();
                                mPlayer.start();
                                playing = true;
                                paused = false;
                                new playbutton().execute();
                            } catch (IOException e) {
                                Log.e("voice", "play error");
                            }
                        } else {
                            Toast.makeText(videoFragment.this.getActivity(), "you can not play audio when app is recording", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(videoFragment.this.getActivity(), "you have no audios since app started", Toast.LENGTH_LONG).show();
                    }
            }else{
                    mPlayer.pause();
                    paused = true;
                    new playbutton().execute();
                }
            }else{
                if(playing){
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                    paused = true;
                    playing = false;
                    new playbutton().execute();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRecorder != null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private class timer_sec extends TimerTask{

        @Override
        public void run() {
            new timer().execute();
        }
    }

    private class playbutton extends AsyncTask{
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(!paused){
                pl.setText("PAUSE");
            }else{
                pl.setText("play last one");
            }
            if(playing){
                stp.setVisibility(View.VISIBLE);
            }else{
                stp.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    }
    private class timer extends AsyncTask{
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(started)
                textView.setText(hour+":"+min+":"+sec);
            else
                textView.setText("");
        }

        @Override
        protected Object doInBackground(Object[] params) {
            if(started) {
                time++;
                sec++;
                if (sec == 60) {
                    sec = 0;
                    min++;
                    if (min == 60) {
                        min = 0;
                        hour++;
                    }
                }
            }
            return null;
        }
    }

}
