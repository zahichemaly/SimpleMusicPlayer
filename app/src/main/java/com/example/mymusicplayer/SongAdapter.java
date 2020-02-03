package com.example.mymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private View view;
    private Context context;
    private List<Song> songList;
    private SongCallback callback;
    private static final String NOW_PLAYING = "NOW_PLAYING_SONG";

    public SongAdapter(Context context, List<Song> songList, SongCallback callback) {
        this.context = context;
        this.songList = songList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(context).inflate(R.layout.item_song, viewGroup, false);
        final SongViewHolder holder = new SongViewHolder(view);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = holder.getAdapterPosition();
                Song songPicked = songList.get(index);
                callback.onSongClicked(songPicked);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        songViewHolder.titleText.setText(songList.get(i).getSongTitle());
        songViewHolder.artistText.setText(songList.get(i).getSongArtist());
        songViewHolder.albumText.setText(songList.get(i).getSongAlbum());
        Glide.with(view)
                .load(songList.get(i).getAlbumArt())
                .placeholder(R.drawable.ic_music_note)
                .into(songViewHolder.albumImage);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    static class SongViewHolder extends RecyclerView.ViewHolder {

        private TextView titleText;
        private TextView artistText;
        private TextView albumText;
        private CircleImageView albumImage;
        private ConstraintLayout container;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.root_layout);
            titleText = itemView.findViewById(R.id.song_title);
            artistText = itemView.findViewById(R.id.song_artist);
            albumText = itemView.findViewById(R.id.song_album);
            albumImage = itemView.findViewById(R.id.song_album_art);
        }
    }
}

