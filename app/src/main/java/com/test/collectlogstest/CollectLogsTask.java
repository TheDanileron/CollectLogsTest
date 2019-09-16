package com.test.collectlogstest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Pair;

import static com.test.collectlogstest.AppFilesHelper.APP_INFO_FILE_NAME;

public class CollectLogsTask extends AsyncTask<Void, Integer, Pair<String[], String>> {
    private OnSendLogsDialogListener mListener;
    public static final String LOGS_ZIP_FILE_NAME = "logs.zip";

    public CollectLogsTask(Activity activity, OnSendLogsDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Pair<String[], String> doInBackground(Void... params) {
        String fileData = AppFilesHelper.constructAppInfoFile();
        AppFilesHelper.addAppInfoFile(fileData, APP_INFO_FILE_NAME);
        AppFilesHelper.collectFiles(LOGS_ZIP_FILE_NAME, new LogsFilenameFilter());
        String[] attachments = new String[]{LOGS_ZIP_FILE_NAME};
        publishProgress(100);
        return Pair.create(attachments, fileData);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(Pair<String[], String> stringPair) {
        mListener.onShowSendLogsDialog(stringPair);
    }

    public interface OnSendLogsDialogListener {
        void onShowSendLogsDialog(Pair<String[], String> stringPair);
    }
}