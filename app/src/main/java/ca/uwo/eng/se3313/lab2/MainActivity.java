package ca.uwo.eng.se3313.lab2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ca.uwo.eng.se3313.lab2.R.drawable.cat_error;

public class MainActivity extends AppCompatActivity {

    /**
     * View that showcases the image
     */
    private ImageView ivDisplay;

    /**
     * Skip button
     */
    private ImageButton skipBtn;

    /**
     * Progress bar showing how many seconds left (percentage).
     */
    private ProgressBar pbTimeLeft;

    /**
     * Label showing the seconds left.
     */
    private TextView tvTimeLeft;

    /**
     * Control to change the interval between switching images.
     */
    private SeekBar sbWaitTime;

    /**
     * Editable text to change the interval with {@link #sbWaitTime}.
     */
    private EditText etWaitTime;


    /**
     * Used to download images from the {@link #urlList}.
     */
    private IImageDownloader imgDownloader;

    /**
     * List of image URLs of cute animals that will be displayed.
     */
    private static final List<String> urlList = new ArrayList<String>() {{
        add("http://i.imgur.com/CPqbVW8.jpg");
        add("http://i.imgur.com/Ckf5OeO.jpg");
        add("http://i.imgur.com/3jq1bv7.jpg");
        add("http://i.imgur.com/8bSITuc.jpg");
        add("http://i.imgur.com/JfKH8wd.jpg");
        add("http://i.imgur.com/KDfJruL.jpg");
        add("http://i.imgur.com/o6c6dVb.jpg");
        add("http://i.imgur.com/B1bUG2K.jpg");
        add("http://i.imgur.com/AfxvVuq.jpg");
        add("http://i.imgur.com/DSDtm.jpg");
        add("http://i.imgur.com/SAVYw7S.jpg");
        add("http://i.imgur.com/4HznKil.jpg");
        add("http://i.imgur.com/meeB00V.jpg");
        add("http://i.imgur.com/CPh0SRT.jpg");
        add("http://i.imgur.com/8niPBvE.jpg");
        add("http://i.imgur.com/dci41f3.jpg");
    }};

    // declaring my components
    ImageView viewPic;
    downloader dl;

    int i =1;
    // my image downloader class
    public class downloader extends AsyncTask<Void, Void, Bitmap> implements IImageDownloader
    {
        @Override
        protected Bitmap doInBackground(Void... params)
        {
            Bitmap image = null;
            i=1;
            try
            {
                InputStream is = new URL(urlList.get(new Random().nextInt(15)+1)).openStream();
                image = BitmapFactory.decodeStream(is);

                return image;
            }catch(Exception e)
            {
            }
            // if the image isn't found null is returned and i is set to 0
            if (image==null)
            {
                i = 0;
                return null;
            }
            return null;
        }
        protected void onPostExecute(Bitmap imageResult)
        {
            super.onPostExecute(imageResult);
            if (i==1)
            {
                // the regular downloaded image is displayed
                viewPic.setImageBitmap(imageResult);
            }
            // if the image is set to null is part will happen at the cat image will be displayed
            else if (i==0)
            {
                //sets viewPic to the cat error image
                viewPic.setImageResource(R.drawable.cat_error);
            }
        }
        // on loading failure option
        protected void onLoadingFailure()
        {
            viewPic.setImageResource(R.drawable.cat_error);
        }
        @Override
        // the download function called in the timer and button function
        public void download(@NonNull String imageUrl, @NonNull SuccessHandler handler)
        {
            this.execute();
        }
    };

    // declaring the components
    TextView tv;
    ProgressBar pb;
    SeekBar sb;
    CountDownTimer cdt;
    ImageButton bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Insert your code here (and within the class!)

        dl = new downloader();
        pb = (ProgressBar) findViewById(R.id.pbTimeLeft);
        sb = (SeekBar) findViewById(R.id.sbWaitTime);
        EditText et = (EditText) findViewById(R.id.etWaitTime);
        tv = (TextView) findViewById(R.id.tvTimeLeft);
        bt = (ImageButton) findViewById(R.id.btnSkip);
        viewPic = (ImageView) findViewById(R.id.ivDisplay);

        // starts the initial download, and changes the range of the seekbar to 5-60
        dl.download(null,null);
        pb.setMax((sb.getProgress()+ 5));
        //starts the counter
        ct();

        // skip button
        bt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            // the function of the button when pressed
            public void onClick(View v)
            {
                // cancels the timer, downloads the next images, and restarts the counter
                cdt.cancel();
                new downloader().download(null,null);
                ct();
            }
        });

        // Seek bar
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            // when the value of the seekbar is changed
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // changes the edit text value to the seekbar value, sets a new max for the progress bar, and starts the counter with the new value
                et.setText(String.valueOf(progress + 5));
                cdt.cancel();
                pb.setMax((sb.getProgress()+ 5));
                ct();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        // the edit text
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // checks the value when a key and the enter button are pressed
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String tm = et.getText().toString();
                    int k = Integer.parseInt(tm);
                    // checks if the value is in the correct range and returns an error if it's not
                    if (k < 5 || k > 60) {
                        et.requestFocus();
                        et.setError("Enter a number between 5 and 60");
                        return false;
                    } else if (k >= 5 && k <= 60) {
                        sb.setProgress(k - 5);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    // counter function
    public void ct() {
        // aligns the counter with the seekbar
        cdt = new CountDownTimer((sb.getProgress() + 6) * 1000, 1) {
            public void onTick(long millisUntilFinished) {
                // displays the remaining time on the text view
                tv.setText(millisUntilFinished / 1000 + "s");
                // updates the progress bar with the counter
                pb.setProgress((int) ((sb.getProgress() + 6) * 1000 - millisUntilFinished) / 1000);
                //here you can have your logic to set text to edittext
            }
            // what happends to the counter on finish
            public void onFinish()
            {
                // downloads a new picture and restarts the counter when the timer ends
                new downloader().download(null,null);
                ct();
            }
        }.start();
    }

}
