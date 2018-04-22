package com.example.root.guesstheanimal;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
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

    ArrayList<String> animalURLs = new ArrayList<>();
    ArrayList<String> animalNames = new ArrayList<>();
    int chosenAnimal = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void selectAnimal(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Wrong. It was " + animalNames.get(chosenAnimal), Toast.LENGTH_LONG).show();
        }
        createNewQuestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // https://www.activewild.com/african-animals-list/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView2);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        try {
            @SuppressLint("StaticFieldLeak") DownloadTask task = new DownloadTask() {
                protected void onPostExecute(String result) {
                    String newString = null;

                    if (result != null) {
                        Pattern p = Pattern.compile("</header><!-- .entry-header -->(.*?)</div><!-- .entry-content -->");
                        Matcher m = p.matcher(result);

                        while (m.find()) {
                            newString = m.group(1);
                        }

                        Pattern p1 = Pattern.compile("\" src=\"(.*?)\" alt");
                        Matcher m1 = p1.matcher(newString);

                        while (m1.find()) {
                            animalURLs.add(m1.group(1));
                        }

                        Pattern p2 = Pattern.compile("alt=\"(.*?)\" ");
                        Matcher m2 = p2.matcher(newString);

                        while (m2.find()) {
                            animalNames.add(m2.group(1));
                        }
                    }
                }
            };

            task.execute("https://www.activewild.com/african-animals-list/");

            createNewQuestion();
        }
        catch (Exception e) {

            e.printStackTrace();
        }

/*
        DownloadTask task = new DownloadTask();
        String result = null;
        String newString = null;

        try {
            result = task.execute("https://www.activewild.com/african-animals-list/").get();

            Pattern p = Pattern.compile("</header><!-- .entry-header -->(.*?)</div><!-- .entry-content -->");
            Matcher m = p.matcher(result);

            while (m.find()) {
                newString = m.group(1);
            }

            Pattern p1 = Pattern.compile("\" src=\"(.*?)\" alt");
            Matcher m1 = p1.matcher(newString);

            while (m1.find()) {
                animalURLs.add(m1.group(1));
            }

            Pattern p2 = Pattern.compile("alt=\"(.*?)\" ");
            Matcher m2 = p2.matcher(newString);

            while (m2.find()) {
                animalNames.add(m2.group(1));
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

//        System.out.println(animalURLs);

*/

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
/*
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
*/


                BufferedReader br = new BufferedReader(reader);
                String inputLine;
                StringBuilder builder = new StringBuilder();

                while ((inputLine = br.readLine()) != null) {
                    builder.append(inputLine);
                }
                result = builder.toString();

/*
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String data = reader.readLine();

                while (data != null) {

                    result += data;
                    data = reader.readLine();
                }
*/

                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

    }

    public void createNewQuestion() {

        Random random = new Random();
        chosenAnimal = random.nextInt(animalURLs.size());

        int incorrectAnswerLocation;

        try {
            @SuppressLint("StaticFieldLeak") ImageDownloader imageTask = new ImageDownloader() {
                protected void onPostExecute(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            };

            imageTask.execute(animalURLs.get(chosenAnimal));

            locationOfCorrectAnswer = random.nextInt(4);
            for (int i = 0; i < 4; i++) {

                if (i == locationOfCorrectAnswer) {

                    answers[i] = animalNames.get(chosenAnimal);

                } else {

                    incorrectAnswerLocation = random.nextInt(animalNames.size());

                    while (incorrectAnswerLocation == chosenAnimal) {

                        incorrectAnswerLocation = random.nextInt(animalNames.size());

                    }

                    answers[i] = animalNames.get(incorrectAnswerLocation);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}

