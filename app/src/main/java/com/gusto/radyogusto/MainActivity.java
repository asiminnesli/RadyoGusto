package com.gusto.radyogusto;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.R.attr.onClick;
import static com.gusto.radyogusto.R.id.isPlaying;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String RADIO_STATION_URL = "http://95.173.184.17:9974/";
    private ImageButton buttonPlay;
    private Button buttonStopPlay;
    private Button buttonNotification;
    private MediaPlayer player;
    private String nowPlaying;
    private TextView isPlayingText;
    private ImageView redorGreen;
    SeekBar volumeBar;
    LinearLayout mainLayout;
    LinearLayout scheduleLayout;
    LinearLayout contactLayout;
    LinearLayout followusLayout;
    TextView nowPlayingTextview;
    int isPlaying;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    AudioManager am ;
    NotificationCompat.Builder mBuilder;
    /*-----------post metodu için---------------*/
    String url = "http://radyogusto.net/mobil_veri/index.php";
    String veri_string;
    PostClass post = new PostClass();  //Post Class dan post ad�nda nesne olusturduk.Post class�n i�indeki methodu kullanabilmek i�in
    private CountDownTimer cdt;
    /** Called when the activity is first created. */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("geldi","intentlemi");

        am= (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
                int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream..
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("artık","sende");
        }


        initializeUIElements();
        tumSayfalar();
        allInvisible();
        initializeMediaPlayer();

        mainLayout.setVisibility(View.VISIBLE);

        /*
        every second Log
        */

        cdt=new CountDownTimer(1000, 100) {
            @Override
            //OnTick metodu geri sayım süresince yapılacak değişiklikler
            public void onTick(long millisUntilFinished) {
            }

            @Override
            //süre bittiğinde yapılacaklar
            public void onFinish() {
                Log.d("onFinish","bbbbbbbbbb");
                controlMusic();
            }

        }.start();
    }

    private void initializeUIElements() {
        buttonPlay= (ImageButton)findViewById(R.id.imageButton);
        buttonPlay.setOnClickListener(this);

        nowPlayingTextview= ( TextView )findViewById(R.id.textView4);
        nowPlayingTextview.setText("Now Playing: \n  ");

        isPlayingText= (TextView)findViewById(R.id.isPlaying);
        redorGreen=(ImageView)findViewById(R.id.redOrGreen);
    }

    public void onClick(View v) {
        if (v == buttonPlay) {
            if(isPlaying==1){
                buttonPlay.setBackgroundResource(R.drawable.play_button);
                stopPlaying();
            }else {
                buttonPlay.setBackgroundResource(R.drawable.stop_button);
                startPlaying();
                notificationYollaCustom2();
            }
        }

    }

    public void startPlaying() {
        isPlaying=1;
        new Post().execute();
        player.prepareAsync();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });

    }

    public void notificationYollaCustom() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.widget);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.not).setContent(
                remoteViews);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, IntentPage.class);
        // The stack builder object will contain an artificial back stack for
        // the
        resultIntent.putExtra("gelen","gelen2");
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(IntentPage.class);
        // Adds the Intent that starts the Activity to the top of the stack

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button3, resultPendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());


    }


    public void notificationYollaCustom2() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.widget);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.not).setContent(
                remoteViews);

        Intent intent = new Intent(getApplicationContext() , MainActivity.class);
        intent.putExtra("geldi","intent");
        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(IntentPage.class);
        // Adds the Intent that starts the Activity to the top of the stack

        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button3, pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());


    }

    public void stopPlaying() {

        isPlayingText.setText("Durduruldu.");
        redorGreen.setBackgroundResource(R.drawable.red_button);
        isPlaying=0;
        player.stop();

    }

    public void initializeMediaPlayer() {
        volumeBar=(SeekBar)findViewById(R.id.volumeSeek);
        volumeBar.setProgress(50);
        /*
        SEEKBAR CALISMASI
         */

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double progressdouble=progress*0.01;
                float progressfloat=(float)progressdouble;
                Log.d("progress ->"+progress+"///progressfloat ->",progressdouble+"");
                player.setVolume(progressfloat,progressfloat);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        player =new MediaPlayer();

        player.setVolume(0.5f,0.5f);

        try {
            player.setDataSource(RADIO_STATION_URL);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }

    public void tumSayfalar() {

        ImageButton schedule=(ImageButton)findViewById(R.id.scheduleButton);
        ImageButton contact=(ImageButton)findViewById(R.id.contentButton);
        ImageButton followus=(ImageButton)findViewById(R.id.followUsButton);
        ImageButton exit=(ImageButton)findViewById(R.id.exitButton);

        scheduleLayout=(LinearLayout)findViewById(R.id.schedule_middle);
        mainLayout=(LinearLayout)findViewById(R.id.main_middle);
        contactLayout=(LinearLayout)findViewById(R.id.middle_contact);
        followusLayout=(LinearLayout)findViewById(R.id.middle_followus);

        /*
        LinearLayout contactLayout=(LinearLayout)findViewById(R.id.schedule_middle);
        LinearLayout followLayout=(LinearLayout)findViewById(R.id.schedule_middle);
        */
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allInvisible();
                scheduleLayout.setVisibility(View.VISIBLE);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allInvisible();
                contactLayout.setVisibility(View.VISIBLE);
            }
        });

        followus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allInvisible();
                followusLayout.setVisibility(View.VISIBLE);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allInvisible();
                mainLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    public void allInvisible(){
        mainLayout.setVisibility(View.INVISIBLE);
        scheduleLayout.setVisibility(View.INVISIBLE);
        followusLayout.setVisibility(View.INVISIBLE);
        contactLayout.setVisibility(View.INVISIBLE);
    }

    class Post extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() { // Post tan �nce yap�lacak i�lemler. Y�kleniyor yaz�s�n�(ProgressDialog) g�sterdik.

        }

        protected Void doInBackground(Void... unused) { // Arka Planda yap�lacaklar. Yani Post i�lemi

            List<NameValuePair> params = new ArrayList<NameValuePair>(); //Post edilecek de�i�kenleri ayarliyoruz.
            //Bu de�i�kenler bu uygulamada hi�bir i�e yaram�yor.Sadece g�stermek ama�l�
            params.add(new BasicNameValuePair("isim", "taha"));
            params.add(new BasicNameValuePair("mail", "taha@xxxx.com"));
            veri_string = post.httpPost(url,"POST",params,20000); //PostClass daki httpPost metodunu �a��rd�k.Gelen string de�erini ald�k

            Log.d("HTTP POST CEVAP:",""+veri_string);// gelen veriyi log tuttuk

            return null;
        }

        protected void onPostExecute(Void unused) { //Posttan sonra
            DonenVeriler(veri_string);/// veri_string dönen degerimiz oluyor.

        }
    }

    public void DonenVeriler(String gelenveri){
        nowPlayingTextview.setText("Now Playnig: \n" +gelenveri);
    }

    public void controlMusic(){

        if(am.isMusicActive() && isPlaying==1){
            isPlayingText.setText("Canlı Dinliyorsunuz...");
            redorGreen.setBackgroundResource(R.drawable.green_button);
        }else if(!am.isMusicActive() && isPlaying==1){
            isPlayingText.setText("Bağlanıyor");
            redorGreen.setBackgroundResource(R.drawable.red_button);
        }else if(am.isMusicActive() && isPlaying==0){
            redorGreen.setBackgroundResource(R.drawable.red_button);
            isPlayingText.setText("Bir hata oldu uygulama tekrar başlatınız.");
        }else if(!am.isMusicActive() && isPlaying==0){
            redorGreen.setBackgroundResource(R.drawable.red_button);
            isPlayingText.setText("Durduruldu.");
        }
        new Post().execute();
        cdt.start();
    }

}


