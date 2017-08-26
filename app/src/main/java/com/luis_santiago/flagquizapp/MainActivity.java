package com.luis_santiago.flagquizapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.icu.util.*;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import java.util.*;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.luis_santiago.flagquizapp.ui.AboutActivity;
import com.luis_santiago.flagquizapp.ui.SettingsActivity;

import static android.R.string.no;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView mMinutesTextview;
    private TextView mSecondsTextView;
    private Toolbar mToolbar;
    private Button mStart;
    private Button mStop;
    private Button mReset;
    private boolean isStarted = false;
    private int minutesToStart = Keys.MINUTE_TO_START;
    private long currentSecond = 60000;
    private NotificationCompat.Builder notificaionBuilder;
    private Bitmap icon;
    private NotificationManager notificationManager;
    private int currentNotificationID = 0;


    private CountDownTimer countDownTimer2 = new CountDownTimer(currentSecond, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            currentSecond -=1000;
            Log.e("MAIN ACTIVITY", ""+currentSecond);
            mSecondsTextView.setText(String.valueOf(currentSecond / 1000));
            if(minutesToStart == 0 && currentSecond ==0){
                setUpNotification();
                countDownTimer2.cancel();
                resetMinutes();
            }
        }

        @Override
        public void onFinish() {
            Log.e("", "minutesToStart"+minutesToStart+" currentSecond"+currentSecond);
            minutesToStart -=1;
            mSecondsTextView.setText(String.valueOf(currentSecond));
            mMinutesTextview.setText(String.valueOf(minutesToStart));
            start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mReset.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:{
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            }
            case R.id.settings:{
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            }
        }
        return true;
    }

    private void initComponents(){
        mMinutesTextview = (TextView) findViewById(R.id.minutes);
        mSecondsTextView = (TextView) findViewById(R.id.seconds);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mStart = (Button) findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
        mReset = (Button) findViewById(R.id.reset);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:{
                // call the start method
                if(!isStarted){
                    startTiming();
                }
                break;
            }
            case R.id.stop:{
                //call the stop method
                countDownTimer2.cancel();
                isStarted = false;
                break;
            }

            case R.id.reset:{
                //call the reset method
                resetMinutes();
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        minutesToStart = Keys.MINUTE_TO_START;
        mMinutesTextview.setText(String.valueOf(minutesToStart));
    }

    private void resetMinutes() {
        currentSecond = 60000;
        minutesToStart = Keys.MINUTE_TO_START;
        mSecondsTextView.setText("00");
        mMinutesTextview.setText(String.valueOf(minutesToStart));
        isStarted = false;
    }

    private void setUpNotification(){
        notificaionBuilder = new NotificationCompat.Builder(MainActivity.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle("Time to relax!")
                .setContentText("You've been working very hard, it's time for a break!");
        sendNotification();
    }

    private void sendNotification(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notificaionBuilder.setContentIntent(contentIntent);
        Notification notification = notificaionBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        // In order to send notification we have to create a unique id for each one
        currentNotificationID++;

        int notificationId = currentNotificationID;
        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;
        // setting our notification to just be send!
        notificationManager.notify(notificationId, notification);
    }

    private void startTiming(){
        if(minutesToStart==1){
            mMinutesTextview.setText("0");

        }
        isStarted = true;
        countDownTimer2.start();
    }
}