package com.example.mymusicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements SongCallback {

    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private Song currentSong;
    private CircleImageView songImageImageView;
    private TextView songTitleTextView;
    private TextView songArtistTextView;
    private TextView songAlbumTextView;
    private final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void setup() {
        songList = SongLoader.getSongList(getApplicationContext().getContentResolver());
        setContentView(R.layout.activity_main);
        songImageImageView = findViewById(R.id.song_album_art);
        songTitleTextView = findViewById(R.id.song_title);
        songArtistTextView = findViewById(R.id.song_artist);
        songAlbumTextView = findViewById(R.id.song_album);
        SongAdapter songAdapter = new SongAdapter(this, songList, this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songAdapter);
        if (songList != null && songList.size() > 0) {
            currentSong = songList.get(0);
        }
        updateSongInfo();
    }

    @Override
    public void onSongClicked(Song song) {
        if (currentSong != null) {
            stopPlayer();
        }
        currentSong = song;
        initPlayer();
        startPlayer();
    }

    private void stopPlayer() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
    }

    private void startPlayer() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void initPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getData());
            mediaPlayer.prepare();
            updateSongInfo();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Could not play song", Toast.LENGTH_LONG).show();
        }
    }

    private void updateSongInfo() {
        if (currentSong != null) {
            songTitleTextView.setText(currentSong.getSongTitle());
            songArtistTextView.setText(currentSong.getSongArtist());
            songAlbumTextView.setText(currentSong.getSongAlbum());
            Glide.with(this)
                    .load(currentSong.getAlbumArt())
                    .placeholder(R.drawable.ic_music_note)
                    .into(songImageImageView);
        }
    }

    @Override
    protected void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onStop();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||

                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

            } else {
                setup();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean ReadStoragePermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean WriteStoragePermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;

                if (ReadStoragePermission && WriteStoragePermission) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    setup();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
