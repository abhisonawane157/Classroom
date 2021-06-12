package com.sonawane_ad.classroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ViewDocumentActivity extends AppCompatActivity {

    PDFView pdfView;
    private Intent intent = new Intent();
    private SharedPreferences file;
    private AlertDialog.Builder dialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progress);
        dialog = new AlertDialog.Builder(this);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);

//        pdfView.fromStream(file.getString("docurl",""));
        new PDFDownload().execute(file.getString("docurl",""));
        progressBar.setVisibility(View.VISIBLE);

    }
    private class PDFDownload extends AsyncTask<String, Void, InputStream>
    {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if(urlConnection.getResponseCode() == 200)
                {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressBar.setVisibility(View.GONE);
        }
    }
}