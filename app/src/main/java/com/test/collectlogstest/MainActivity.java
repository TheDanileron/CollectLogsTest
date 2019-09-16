package com.test.collectlogstest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CollectLogsTask.OnSendLogsDialogListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button makeLogBtn;
    private Button startCollectingBtn;
    private LogcatHelper logcatHelper;
    private PreferenceHelper preferenceHelper;
    // just generate a random number and log it
    private View.OnClickListener logClickListener = (v) -> {
        Random random = new Random();
        int randomInt = random.nextInt();
        Log.i(TAG, "Random int: " + randomInt);
    };
    // start collecting logs
    private View.OnClickListener startCollectingListener = (v) -> {
        preferenceHelper.setCollectingLogs(!preferenceHelper.isCollectingLogs());
        if (preferenceHelper.isCollectingLogs()) {
            ((Button)v).setText("Stop collecting");
            logcatHelper.prepareNewLogFile();
            logcatHelper.start(null);
        } else {
            ((Button)v).setText("Collect logs");
            new CollectLogsTask(this, this).execute();
            logcatHelper.stop();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceHelper = new PreferenceHelper();
        makeLogBtn = findViewById(R.id.logBtn);
        makeLogBtn.setOnClickListener(logClickListener);
        startCollectingBtn = findViewById(R.id.startCollectingBtn);
        startCollectingBtn.setOnClickListener(startCollectingListener);
        logcatHelper = new LogcatHelper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preferenceHelper.isCollectingLogs()) {
            startCollectingBtn.setText("Stop collecting");
        } else {
            startCollectingBtn.setText("Collect logs");
        }
    }

    @Override
    public void onShowSendLogsDialog(Pair<String[], String> stringPair) {
        String emailTo = "your_email@gmail.com";
        String emailSubj = "Subject";
        String chooserTitle = "Title";
        ArrayList fileNames = new ArrayList<String>(Arrays.asList(stringPair.first));
        sendEmail(this, fileNames, emailTo, emailSubj, chooserTitle, stringPair.second);
    }

    private void sendEmail(Context context, List<String> fileNames,
                           String emailTo, String emailSubj, String chooserTitle,
                           String msg) {
        ArrayList attachments = new ArrayList<Uri>();
        if (fileNames != null) {
            for (String fileName : fileNames) {
                Uri uri = Uri.parse(this.getString(R.string.uri_content_cache, fileName));
                attachments.add(uri);
            }
        }
        try {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{emailTo});
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubj);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            if (attachments != null && attachments.size() != 0) {
                Log.i("send email", "add attachment $attachments");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooserIntent = null;
            if (chooserTitle == null) {
                chooserIntent = intent;
            } else {
                chooserIntent = Intent.createChooser(intent, chooserTitle);
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivity(chooserIntent);
        } catch (Exception e) {
        }
    }
}
