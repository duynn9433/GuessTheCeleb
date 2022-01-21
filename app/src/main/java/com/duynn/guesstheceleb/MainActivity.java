package com.duynn.guesstheceleb;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private List<String> avatarUrls;
    private List<String> names;
    private int answerIndex;
    Button btn0;
    Button btn1;
    Button btn2;
    Button btn3;
    ImageView imageView;

    public void chooseAnswer(View view){
        Button btn = (Button) view;
        if(btn.getText().toString().equals(names.get(answerIndex))){
            Toast.makeText(this, "Correct", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Wrong :( It is "+names.get(answerIndex), Toast.LENGTH_LONG).show();
        }

        createNextQues();
    }
    public void createNextQues(){
        answerIndex = (int)(Math.random()*avatarUrls.size());
        //picture
        DownLoadImageTask task = new DownLoadImageTask();
        try {
            Bitmap bitmap = task.execute(avatarUrls.get(answerIndex)).get();
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //button
        List<String> listAnswer = new ArrayList<>();
        listAnswer.add(names.get(answerIndex));
        for(int i =0;i<3;i++){
            int temp = 0;
            do{
                temp = (int)(Math.random()*names.size());
            }while(temp == answerIndex);

            listAnswer.add(names.get(temp));
        }
        Collections.shuffle(listAnswer);

        btn0.setText(listAnswer.get(0));
        btn1.setText(listAnswer.get(1));
        btn2.setText(listAnswer.get(2));
        btn3.setText(listAnswer.get(3));
    }

    public class DownLoadImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                Bitmap bitmap = BitmapFactory.decodeStream(httpURLConnection.getInputStream());
                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    /**
     * Download html file have avatar link + name
     * */
    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                Log.i("Info","start");
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    stringBuilder.append((char) data) ;
                    data = reader.read();
                    Log.i("Info","loop");
                }
                Log.i("Info","end");
                return stringBuilder.toString();


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avatarUrls = new ArrayList<>();
        names = new ArrayList<>();
        DownloadTask task = new DownloadTask();
        try {
            String result = task.execute("https://www.imdb.com/list/ls052283250/").get();
            Log.i("Html",result);

            //avatar links
            Pattern p = Pattern.compile("src=\"(.*?).jpg\"");
            Matcher m = p.matcher(result);
            while(m.find()){
                avatarUrls.add(m.group(1)+".jpg");
            }
            //names
            p = Pattern.compile("<img alt=\"(.*?)\"");
            m = p.matcher(result);
            while(m.find()){
                names.add(m.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        btn0 = findViewById(R.id.button0);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        imageView = findViewById(R.id.imageView);

        createNextQues();
    }
}