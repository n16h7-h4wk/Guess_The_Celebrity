package com.developer.ankit.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.Exception;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView ;
    Button button0, button1,button2,button3 ;
    ArrayList<String> celebURLs = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();
    int index, locationOfCorrectAnswer =0 ;
    String[] answers = new String[4];

    public class DownloadImage extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(in);
                return image;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) throws Exception {

            String result = "";
            URL url;
            HttpURLConnection connection = null ;
                
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    result+=(char)data;
                    data = reader.read();
                }

                return result ;

        }
    }


    public void celebChosen(View view) throws ExecutionException, InterruptedException {
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Wrong! It was "+ celebNames.get(index),Toast.LENGTH_SHORT).show();
        generateQuestion();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button_0);
        button1 = (Button) findViewById(R.id.button_1);
        button2 = (Button) findViewById(R.id.button_2);
        button3 = (Button) findViewById(R.id.button_3);

        DownloadTask task = new DownloadTask();
        try {
            String result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while(m.find()){
                celebURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()){
                celebNames.add(m.group(1));
            }
            generateQuestion();




        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
    public void generateQuestion() throws ExecutionException, InterruptedException {
        Random random = new Random();
        index = random.nextInt(celebURLs.size());

        DownloadImage image = new DownloadImage();
        Bitmap myImage = image.execute(celebURLs.get(index)).get();
        mImageView.setImageBitmap(myImage);
        locationOfCorrectAnswer = random.nextInt(4);
        int incorrectAnswer ;
        for(int i=0;i<4;i++){
            if(i==locationOfCorrectAnswer)
                answers[i] = celebNames.get(index);
            else {
                incorrectAnswer = random.nextInt(celebURLs.size());
                while(incorrectAnswer==index)
                    incorrectAnswer = random.nextInt(celebURLs.size());
                answers[i] = celebNames.get(incorrectAnswer);
            }
        }
        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);
    }
}
