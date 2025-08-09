package jp.ac.jec.cm0122.android114.Helpers;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;

public class MediaPlayerService {
    static MediaPlayer mediaPlayer;

    public static void playSound(Context context, @NonNull final String url) {
        Uri uri = Uri.parse(url);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(context, uri);
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        mediaPlayer.setOnCompletionListener(mp -> {

        });

    }
}
