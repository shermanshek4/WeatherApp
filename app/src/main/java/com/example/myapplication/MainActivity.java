package com.example.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String City = "Japan";

    String Key = "9251a1201edd950ccea542a7a0f7acdd";

    String url1 = "https://api.openweathermap.org/data/2.5/weather?q=" + City + "&appid=" + Key;

    TextView txtCity,txtValue,txtValueFeelLike,txtValueHumidity,txtValueVision;
    String nameIcon = "10d";
    EditText editText;
    Button loading,back;
    ImageView imgIcon;
    RelativeLayout relativeLayoutMain;
    RelativeLayout relativeLayout;
    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imgIcon = findViewById(R.id.imgIcon);
        txtValue = findViewById(R.id.Value);
        editText = findViewById(R.id.input);
        txtCity = findViewById(R.id.City);
        txtValueFeelLike = findViewById(R.id.TitleFeelLike);
        txtValueHumidity = findViewById(R.id.ValueHumidity);
        txtValueVision = findViewById(R.id.ValueVision);
        relativeLayout = findViewById(R.id.rlWeather);
        relativeLayoutMain = findViewById(R.id.rlMain_Ac);
        loading = findViewById(R.id.loading);
        back = findViewById(R.id.back);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        imgIcon.setAnimation(topAnim);
        txtValue.setAnimation(bottomAnim);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            URL url;
            HttpURLConnection httpURLConnection;
            InputStream inputStream;

            try {
                Log.i("LINK",strings[0]);
                url = new URL(strings[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void , String> {
        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;
            InputStream inputStream;
            InputStreamReader inputStreamReader;

            try {

                url = new URL(strings[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data != -1) {

                    result += (char) data;
                    data = inputStreamReader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public void loading(View view) {

        editText.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        relativeLayoutMain.setBackgroundColor(Color.parseColor("#E6E6E6"));

        City = editText.getText().toString();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + City +"&units=metric&appid=" + Key;

        DownloadTask downloadTask = new DownloadTask();

        try {
            String result = "abc";
            result = downloadTask.execute(url).get();
            Log.i("Result:",result);
            JSONObject jsonObject = new JSONObject(result);
            JSONObject main = jsonObject.getJSONObject("main");
            String temp = main.getString("temp");
            String humidity = main.getString("humidity");
            String feel_like = main.getString("feels_like");
            String visibility = jsonObject.getString("visibility");
            nameIcon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
            Log.i("Name Icon",nameIcon);
            Long time = jsonObject.getLong("dt");
            String Time = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.ENGLISH)
                    .format(new Date(time * 1000));

            txtCity.setText(City);
            txtValue.setText(temp + "°");
            txtValueVision.setText(visibility);
            txtValueHumidity.setText(humidity);
            txtValueFeelLike.setText(feel_like);

            DownloadImage downloadImage = new DownloadImage();
            String urlIcon = " https://openweathermap.org/img/wn/"+ nameIcon +"@2x.png";
            Bitmap bitmap = downloadImage.execute(urlIcon).get();
            imgIcon.setImageBitmap(bitmap);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}