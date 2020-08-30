package com.example.voicemusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SmartPlayerActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView playPauseButton, playNextButton, playPreviousButton, songImage;
    private TextView songNameTxt;
    private Button voiceModeButton;
    private String mode = "ON";

    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);

        checkAudioPermission();

        playPauseButton = findViewById(R.id.play_pause_song_image_button);
        playNextButton = findViewById(R.id.play_next_song_image_button);
        playPreviousButton = findViewById(R.id.play_previous_song_image_button);
        songImage = findViewById(R.id.song_logo_image_view);
        songNameTxt = findViewById(R.id.song_name_text_view);
        voiceModeButton = findViewById(R.id.voice_mode_button);

        ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceivedValuesAndStartPlaying();

        voiceModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mode.equals("ON")){
                    mode = "OFF";

                    voiceModeButton.setText("Voice control: OFF");
                    playNextButton.setVisibility(View.VISIBLE);
                    playPauseButton.setVisibility(View.VISIBLE);
                    playPreviousButton.setVisibility(View.VISIBLE);
                }else {
                    mode = "ON";
                    voiceModeButton.setText("Voice control: ON");
                    playNextButton.setVisibility(View.INVISIBLE);
                    playPauseButton.setVisibility(View.INVISIBLE);
                    playPreviousButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }
                return false;
            }
        });

        playNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.getCurrentPosition() > 0){

                    playNextSong();
                }
            }
        });

        playPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer.getCurrentPosition() > 0){

                    playPreviousSong();
                }
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null) {

                    keeper = matchesFound.get(0);
                    Toast.makeText(SmartPlayerActivity.this, "Input Result: " + keeper, Toast.LENGTH_SHORT).show();
                    if (keeper.equalsIgnoreCase("Pause the Song")){

                        playPauseSong();
                        Toast.makeText(SmartPlayerActivity.this, "COMMAND: " + keeper, Toast.LENGTH_SHORT).show();
                    }else if (keeper.equalsIgnoreCase("Play the Song")){

                        playPauseSong();
                        Toast.makeText(SmartPlayerActivity.this, "COMMAND: " + keeper, Toast.LENGTH_SHORT).show();
                    }else if (keeper.equalsIgnoreCase("Play the next Song")){

                        playNextSong();
                        Toast.makeText(SmartPlayerActivity.this, "COMMAND: " + keeper, Toast.LENGTH_SHORT).show();
                    }else if (keeper.equalsIgnoreCase("Play the previous Song")){

                        playPreviousSong();
                        Toast.makeText(SmartPlayerActivity.this, "COMMAND: " + keeper, Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playPauseSong();
            }
        });

    }


    private void validateReceivedValuesAndStartPlaying(){

        if (mediaPlayer != null){

            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) Objects.requireNonNull(bundle).getParcelableArrayList("song");
        mSongName = Objects.requireNonNull(mySongs).get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();

    }

    private void checkAudioPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                int REQUEST_MICROPHONE = 1;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE);

            }
        }
    }

    private void playPauseSong(){

        songImage.setImageResource(R.drawable.music_logo_2);

        if (mediaPlayer.isPlaying()){

            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.ic_action_play);
        }else {

            mediaPlayer.start();
            songImage.setImageResource(R.drawable.music_logo_3);
            playPauseButton.setImageResource(R.drawable.ic_action_pause);
        }

    }

    private void playNextSong(){

        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position + 1) % mySongs.size());
        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(this, uri);
        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        mediaPlayer.start();

        songImage.setImageResource(R.drawable.music_logo_1);

        if (mediaPlayer.isPlaying()){

            playPauseButton.setImageResource(R.drawable.ic_action_pause);
        }else {

            playPauseButton.setImageResource(R.drawable.ic_action_play);
            songImage.setImageResource(R.drawable.music_logo_2);
        }
    }

    private void playPreviousSong(){

        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position - 1) < 0 ? (mySongs.size() - 1) : (position - 1));
        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(this, uri);
        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        mediaPlayer.start();

        songImage.setImageResource(R.drawable.music_logo_3);

        if (mediaPlayer.isPlaying()){

            playPauseButton.setImageResource(R.drawable.ic_action_pause);
        }else {

            playPauseButton.setImageResource(R.drawable.ic_action_play);
            songImage.setImageResource(R.drawable.music_logo_1);
        }
    }
}
