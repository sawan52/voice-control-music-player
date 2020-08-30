package com.example.voicemusicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsList = findViewById(R.id.songsList);
        checkReadWritePermission();

    }

    private void checkReadWritePermission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        displayAudioSongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private ArrayList<File> readAudioSongs(File file) {

        ArrayList<File> arrayList = new ArrayList<>();
        File[] allFiles = file.listFiles();

        for (File i : Objects.requireNonNull(allFiles)) {
            if (i.isDirectory() && !i.isHidden()) {
                arrayList.addAll(readAudioSongs(i));
            } else {
                if (i.getName().endsWith(".mp3") || i.getName().endsWith(".wav") || i.getName().endsWith(".aac") || i.getName().endsWith(".wma")) {
                    arrayList.add(i);
                }
            }
        }

        return arrayList;
    }

    private void displayAudioSongs() {

        final ArrayList<File> audioSongs = readAudioSongs(Environment.getExternalStorageDirectory());
        String[] itemsAll = new String[audioSongs.size()];

        for (int j = 0; j < audioSongs.size(); j++) {

            itemsAll[j] = audioSongs.get(j).getName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsAll);
        songsList.setAdapter(arrayAdapter);

        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String songName = songsList.getItemAtPosition(i).toString();
                Intent intent = new Intent(MainActivity.this, SmartPlayerActivity.class);

                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);
                startActivity(intent);

            }
        });

    }

}
