package com.example.samsung.sustainableaquaculture;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import helpers.MqttHelper;
import helpers.ChartHelper;

public class MainActivity extends AppCompatActivity {
    MqttHelper mqttHelper;
    TextView dataReceived;
    ChartHelper mChart;
    LineChart chart;
    TextView condition;
    private static final int REQUEST_CODE_CHECK_GOOGLE_PLAY_SERVICES = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checkGooglePlayServices();

        dataReceived = (TextView) findViewById(R.id.dataReceived);
        chart = (LineChart) findViewById(R.id.chart);
        mChart = new ChartHelper(chart);
        condition = (TextView) findViewById(R.id.condition);
        startMqtt();
        Button historyBtn = (Button) findViewById(R.id.historybtn);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adviceRequest = new Intent(MainActivity.this, notify.class);
                Bundle bundle = new Bundle();
                bundle.putString("con", condition.getText().toString());
                adviceRequest.putExtras(bundle);
                startActivity(adviceRequest);
            }
        });
        Button map = (Button) findViewById(R.id.mapbtn);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapRequest = new Intent(MainActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("con", condition.getText().toString());
                mapRequest.putExtras(bundle);
                startActivity(mapRequest);
            }
        });
        //createNotificationChannel();
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CODE_CHECK_GOOGLE_PLAY_SERVICES:
                if(resultCode== RESULT_CANCELED)
                    showGPSFailExitApp();  //使用者取消處理google play service問題->結束app
                break;
        }
    }

    private void showGPSFailExitApp() {
        AlertDialog.Builder altDlg = new AlertDialog.Builder(this);
        altDlg.setTitle("錯誤");
        altDlg.setMessage("找不到 Google Play Service，無法開啟 GoogleMap 設定多重魚塭");
        altDlg.setIcon(android.R.drawable.ic_dialog_info);

        altDlg.show();

    }

    private void checkGooglePlayServices(){
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if(resultCode == ConnectionResult.SUCCESS) return;

        if(googleApi.isUserResolvableError(resultCode)){
            googleApi.showErrorDialogFragment(this, resultCode, REQUEST_CODE_CHECK_GOOGLE_PLAY_SERVICES);
            return;
        }
        showGPSFailExitApp();
    }
    */
    public void startMqtt(){

        mqttHelper = new MqttHelper(getApplicationContext());

        mqttHelper.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                dataReceived.setText(mqttMessage.toString());
                mChart.addEntry(Float.valueOf(mqttMessage.toString()));
                float CFU = Float.valueOf(mqttMessage.toString());
                if(CFU<=1000){
                    condition.setText("安全");
                }
                else if(CFU>1000 && CFU<=100000){
                    condition.setText("中度危險");
                    showNotification();
                }
                else if(CFU>100000){
                    condition.setText("極度危險");
                    showNotification();
                }
                else condition.setText("輸入值有誤");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    public void showNotification(){
        System.out.println("SHOW NOTIFY");
        Intent intent = new Intent(getApplicationContext(), notify.class);
        //if user click on noti-> start targeted activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent penIt = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("警告")
                .setContentText("您的魚塭弧菌濃度已超過安全範圍，請盡速處理")
                .setContentIntent(penIt)
                .build();
        notiMgr.notify(1,noti);
    }

   /* private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }*/


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
