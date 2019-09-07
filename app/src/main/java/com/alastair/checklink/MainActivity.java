package com.alastair.checklink;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SDCARD = 0;


    @Override
    protected void onStart() {
        //System.out.println("On Start called");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
            boolean noPermission = true;
            while (noPermission) {
                if (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //System.out.println("Still running without permission ");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    sleep(10000);
                } else {
                    noPermission = false;


                    setContentView(R.layout.activity_main);

                    setTitle("CheckLink - Android Background Service - ASM");

                    Button startBackService = findViewById(R.id.start_background_service_button);
                    startBackService.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Start android service.
                            //System.out.println("Starting Service ");
                            Intent theIntent = new Intent(MainActivity.this, CheckLinkService.class);
                            startForegroundService(theIntent);
                            show_Notification();
                        }
                    });


                    Button stopBackService = findViewById(R.id.stop_background_service_button);
                    stopBackService.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Stop android service.
                            //System.out.println("Stopping Service ");
                            Intent stopServiceIntent = new Intent(MainActivity.this, CheckLinkService.class);
                            stopService(stopServiceIntent);
                        }
                    });

                }
            }
        } catch (InterruptedException ie) {
            //System.out.println("InterruptedException :"+ie.getMessage() );
        }

    }
    public void show_Notification(){

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText("CheckLink")
                .setContentTitle("asm")
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.sym_action_chat,"Title",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
    }

    public void notifyThis() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(),
                (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(getApplicationInfo().icon)
                        .setContentTitle("CheckLink")
                        .setContentText("asm")
                        .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

}
