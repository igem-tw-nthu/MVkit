package com.example.samsung.sustainableaquaculture;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class notify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        NotificationManager notiMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent getadvice = getIntent();
        Bundle bundle = getadvice.getExtras();
        String condition = bundle.getString("con");
        TextView advice = (TextView) findViewById(R.id.advice);

        switch (condition){
            case("安全"):
                advice.setText(R.string.safeAdvice); break;
            case("中度危險"):
                advice.setText(R.string.warnAdvice); break;
            case("極度危險"):
                advice.setText(R.string.warnAdvice); break;
        }
        Button conditionBtn = (Button) findViewById(R.id.conditionBtn);
        conditionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == android.R.id.home){
            // 點擊 ActionBar 返回按鈕時 結束目前的 Activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
