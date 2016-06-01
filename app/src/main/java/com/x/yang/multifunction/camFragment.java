package com.x.yang.multifunction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class camFragment extends Fragment implements SensorEventListener{


    private static final int REQUEST_CODE_TAKE_VIDEO = 2;

    private int minute = 0;
    private int second = 0;
    private SurfaceView Surfaceview;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder surfaceHolder;
    private TextView textView;
    private ImageButton start,stop;
    private File RecVedioPath;
    private Bitmap bt;
    private File RecAudioFile;
    private ImageView imageView1, imageView2;
    private Camera camera;
    private int filenumbers = 0;
    private String str="";
    private boolean isPreview = true;
    private boolean isRecording = false;
    SensorManager sensorManager;
    private final Timer timer = new Timer();
    private SettingProfile sp = SettingProfile.getSp();
    private String previous_name = "";
    private boolean stopable = false;
    private int emergency = 0;
    private int invis,vis;



    public camFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager)this.getActivity(). getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        timer.schedule(new Looptask(),10000,10000);
        invis = View.INVISIBLE;
        vis = View.VISIBLE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cam, container, false);
        start = (ImageButton)v.findViewById(R.id.cam_start);
        stop = (ImageButton)v.findViewById(R.id.cam_end);
        start.setOnClickListener(new clickImage());
        stop.setOnClickListener(new clickImage());
        Surfaceview = (SurfaceView)v.findViewById(R.id.arc_hf_video_view);
        textView = (TextView)v.findViewById(R.id.cam_time);
        textView.setText(str);
        imageView1 = (ImageView)v.findViewById(R.id.state1);
        imageView1.setVisibility(vis);
        imageView2 = (ImageView)v.findViewById(R.id.state2);
        imageView2.setVisibility(invis);
        RecVedioPath = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/hfdatabase/video/temp/");
        if (!RecVedioPath.exists()) {
            RecVedioPath.mkdirs();
        }
        SurfaceHolder lsurfaceHolder = Surfaceview.getHolder();
        lsurfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(camera!=null){
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(camera != null){
                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                surfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                if(camera == null)
                    camera = Camera.open(0);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setRotation(0);
                //parameters.setPreviewSize(width,height);parameters.setPreviewFrameRate(5);
                camera.setParameters(parameters);
                try{
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                    isPreview = true;
                }catch (Exception e){
                    camera.release();
                }


            }
        });
        lsurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return v;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        Log.i("sensor value", ""+values[0]);
        Log.i("sensor value", ""+values[1]);
        Log.i("sensor value", ""+values[2]);
        float level = (float) sp.getG_level();
        if(values[0]>level|| values[1]>level || values[2]>level ){
            emergency++;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private class clickImage implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            camFragment.this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if(id == R.id.cam_start){
                if(!isRecording){
                    if (isPreview) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    isPreview = false;
                }
                    if (mediaRecorder == null)
                        mediaRecorder = new MediaRecorder();
                    else
                        mediaRecorder.reset();
                    mediaRecorder.setPreviewDisplay(surfaceHolder
                            .getSurface());
                    mediaRecorder
                            .setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    mediaRecorder
                            .setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder
                            .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder
                            .setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    mediaRecorder.setVideoEncodingBitRate(5*1024*1024);
                    mediaRecorder
                            .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.setVideoSize(1280,720);
                    mediaRecorder.setVideoFrameRate(15);
                    try {
                        RecAudioFile = File.createTempFile("Vedio", ".3gp",
                                RecVedioPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaRecorder.setOutputFile(RecAudioFile
                            .getAbsolutePath());
                    try {
                        mediaRecorder.prepare();
                        Toast.makeText(camFragment.this.getActivity(),"Start recording.....",Toast.LENGTH_LONG).show();

                        mediaRecorder.start();
                        str = "recording....";
                        new statechage().execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isRecording = true;
                    stopable = true;}

            }

            if(id == R.id.cam_end){
                if(isRecording){
                    if(stopable){
                        try {
                            isRecording = false;
                            mediaRecorder.stop();
                            camFragment.this.getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            mediaRecorder.release();
                            mediaRecorder = null;
                            String path = Environment.getExternalStorageDirectory()
                                    .getAbsolutePath()
                                    + "/hfdatabase/video/";
                            String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                                    .format(new Date()) + ".3gp";
                            File out = new File(path);
                            if (!out.exists()) {
                                out.mkdirs();
                            }
                            Toast.makeText(camFragment.this.getActivity(),"end recording,file saved!",Toast.LENGTH_LONG).show();
                            out = new File(path, fileName);
                            if (RecAudioFile.exists())
                                RecAudioFile.renameTo(out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(camera == null)
                            camera = Camera.open(0);
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setRotation(0);
                        //parameters.setPreviewSize(width,height);parameters.setPreviewFrameRate(5);
                        camera.setParameters(parameters);
                        try{
                            camera.setPreviewDisplay(surfaceHolder);
                            camera.startPreview();
                            isPreview = true;
                            str = "recording stop";
                            isRecording=false;
                            new statechage().execute();
                        }catch (Exception e){
                            camera.release();
                        }
                    }
                    else {
                        Toast.makeText(camFragment.this.getActivity(),"can not stop now!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        //camera.open(0);

    }

    private class statechage extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
           if(invis== View.INVISIBLE){
               invis= View.VISIBLE;
           }else{
               invis= View.INVISIBLE;
           }
            if(vis== View.INVISIBLE){
                vis= View.VISIBLE;
            }else{
                vis= View.INVISIBLE;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            super.onPostExecute(o);
            imageView1.setVisibility(vis);
            imageView2.setVisibility(invis);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
        if(camera !=null){
            camera.release();
            camera = null;
        }
        if (mediaRecorder != null){
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        sensorManager.unregisterListener(this);
        if(camera !=null){
            camera.release();
            camera = null;
        }
        if (mediaRecorder != null){
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
    private class Looptask extends TimerTask {

        @Override
        public void run() {
            if(isRecording){
                if(filenumbers>sp.getMaxNumberRecords()){
                    filenumbers = 0;
                }
                File recVedioPath = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/hfdatabase/video/temp/"+emergency+"/"+filenumbers+"/");
                if (!recVedioPath.exists()) {
                    recVedioPath.mkdirs();
                }else{
                    File[] files = recVedioPath.listFiles();
                    if(files.length>0)
                        files[0].delete();

                }

                String fileName = new SimpleDateFormat("yyyyMMdd")
                        .format(new Date()) + "-"+filenumbers;
                filenumbers++;

                previous_name = fileName;
                stopable = false;
                mediaRecorder.reset();
                mediaRecorder.setPreviewDisplay(surfaceHolder
                        .getSurface());
                mediaRecorder
                        .setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediaRecorder
                        .setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder
                        .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder
                        .setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mediaRecorder.setVideoEncodingBitRate(5*1024*1024);
                mediaRecorder
                        .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setVideoSize(1280,720);
                mediaRecorder.setVideoFrameRate(15);
                try {
                    RecAudioFile = File.createTempFile(fileName, ".3gp",
                            recVedioPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaRecorder.setOutputFile(RecAudioFile
                        .getAbsolutePath());
                try {
                    mediaRecorder.prepare();


                    mediaRecorder.start();
                    stopable = true;
                }catch (Exception e){

                }
            }

        }
    }

    private int findFrontFacingCamera() {
    int cameraId = 0;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
