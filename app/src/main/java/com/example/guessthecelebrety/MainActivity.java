package com.example.guessthecelebrety;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    class ContentDownloader extends AsyncTask<String, Void, String> {

        //pass the url of the website
        @Override
        protected String doInBackground(String... params) {

            try {
                String content = "";
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                StringBuilder sb = new StringBuilder();


                while (data != -1) {

                    char c = (char) data;
                    sb.append(c);

                    data = reader.read();
                }
                content = String.valueOf(sb);

                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        //pass the url of the image from aquired from the website.
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    ArrayList<String> imageUrls;
    ArrayList<Celebrety> celebreties;
    ImageView imageView;
    TextView rank;
    TextView name;
    TextView description;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        imageUrls = new ArrayList<>();
        names = new ArrayList<>();
        descriptions = new ArrayList<>();
        celebreties = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentDownloader contentDownloader = new ContentDownloader();

        String webContents;

        try {
            webContents = contentDownloader.execute("http://www.imdb.com/list/ls052283250/").get();

            String[] firstSplit = webContents.split("<div class=\"list detail\">");


            String[] secondSplit = firstSplit[1].split("<iframe id");

            findImages(secondSplit[0]);
            findNames(secondSplit[0]);
            findDescription(secondSplit[0]);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < names.size(); i++) {

            celebreties.add(new Celebrety(names.get(i), imageUrls.get(i), descriptions.get(i)));

        }


        imageView = (ImageView) findViewById(R.id.imageView);
        rank = (TextView) findViewById(R.id.rankTextView);
        name = (TextView) findViewById(R.id.nameTextView);
        description = (TextView) findViewById(R.id.descriptionTextView);
        Button button = (Button) findViewById(R.id.button);
        showCelebretyInfo(button);

    }

    int current = 0;

    public void showCelebretyInfo(View view) {

        if (view.getTag().equals(String.valueOf(0))) {
            if(current<100)
            current++;
        } else {
            if(current>1)
            current--;
        }

        int call;
        if(current>0) {
            call = current - 1;
        }else{
            call = current;
        }

        Celebrety celeb = celebreties.get(call);

        ImageDownloader downloader = new ImageDownloader();
        Bitmap image = null;
        try {
            image = downloader.execute(celeb.getImageUrl()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(image);
        rank.setText(String.valueOf(current));
        name.setText(celeb.getName());
        description.setText(celeb.getDescription());
    }

    ArrayList<String> descriptions;


    private void findDescription(String input) {

        Pattern p = Pattern.compile("<div class=\"item_description\">(.*?)</div>");
        Matcher m = p.matcher(input);

        String description;
        while (m.find()) {
            description = m.group(1);
            String[] splits = description.split("<.*>");

            descriptions.add(Arrays.toString(splits).replaceAll("\\[|\\]|,|", ""));
        }

    }

    ArrayList<String> names;

    private void findNames(String input) {

        Pattern p = Pattern.compile("<b>.*>(.*?)</a>");
        Matcher m = p.matcher(input);

        while (m.find()) {
            names.add(m.group(1));
        }

    }

    private void findImages(String input) {
        Pattern p = Pattern.compile("img src=\"(.*?)\"");
        Matcher m = p.matcher(input);

        while (m.find()) {
            imageUrls.add(m.group(1));
        }

        p = Pattern.compile("loadlate=\"(.*?)\"");
        m = p.matcher(input);

        while (m.find()) {
            imageUrls.add(m.group(1));
        }
    }


}
