package com.example.matchinggame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    ImageView memImage;
    Button btnA;
    Button btnB;
    Button btnC;
    Button btnD;
    Button endBtn;
    TextView textScore;
    TextView textTime;

    ArrayList<String> names;
    ArrayList<Button> buttons;
    CountDownTimer timer;
    long time;
    Integer score;
    String currentName;
    Random random;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        memImage = findViewById(R.id.memImage);
        btnA = findViewById(R.id.btnA);
        btnB = findViewById(R.id.btnB);
        btnC = findViewById(R.id.btnC);
        btnD = findViewById(R.id.btnD);
        endBtn = findViewById(R.id.endBtn);
        textScore = findViewById(R.id.numberScore);
        textTime = findViewById(R.id.time);


        names = new ArrayList();
        buttons = new ArrayList();
        score = 0;
        random = new Random();

        buttons.add(btnA);
        buttons.add(btnB);
        buttons.add(btnC);
        buttons.add(btnD);

        readFile();
        endButton();
        contactClick();
        nextQuestion();
    }

    public void contactClick() {
        memImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME, currentName);
                timer.cancel();
                startActivityForResult(intent, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            setTimer(time);
        }
    }

    public void readFile() {
        AssetManager assetManager = getResources().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("names.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                for (String token : line.split(",")) {
                    names.add(token);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadImage(String name) {
        String nameImage = name.replaceAll("[^A-Za-z]+", "").toLowerCase();
        Resources resources = getResources();
        final int resourceId = resources.getIdentifier(nameImage, "drawable",
                getPackageName());
        memImage.setImageDrawable(resources.getDrawable(resourceId));
    }

    public String randomName() {
        int randomInt = random.nextInt(names.size());
        String name = names.get(randomInt);
        return name;
    }

    public void nextQuestion() {
        setTimer(6000);
        textTime.setText("5");
        String name = randomName();
        currentName = name;
        loadImage(name);
        int randomInt = random.nextInt(4);
        Button correctBtn = buttons.get(randomInt);
        buttons.remove(correctBtn);
        correctBtn.setText(name);
        correctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                score += 1;
                textScore.setText(score.toString());
                timer.cancel();
                nextQuestion();
            }
        });
        for (int i = 0; i < 3; i++) {
            String wrongName = randomName();
            while (wrongName.equals(name)) {
                wrongName = randomName();
            }
            buttons.get(i).setText(wrongName);
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(GameActivity.this, "Wrong answer! Go study up on the MDB members >:(", Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    nextQuestion();
                }
            });
        }
        buttons.add(correctBtn);
    }

    public void setTimer(long ms) {
        timer = new CountDownTimer(ms, 1000) {
            @Override
            public void onTick(long l) {
                time = l;
                String time = "" + l/1000;
                textTime.setText(time);
            }

            @Override
            public void onFinish() {
                nextQuestion();
            }
        }.start();
    }

    public void endButton() {
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to end this game?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

    }

}
