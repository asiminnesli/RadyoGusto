package com.gusto.radyogusto;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by asim on 26.01.2017.
 */
    public class IntentPage extends AppCompatActivity {

    AudioManager.OnAudioFocusChangeListener afChangeListener;
    AudioManager am;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Bundle extras = getIntent().getExtras();
        String userName;

        if (extras != null) {
            userName = extras.getString("gelen");
            Log.d("-->", userName);
        }
        // Request audio focus for playback

        am= (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
