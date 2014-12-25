package woodward.joshua.ribbit.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import woodward.joshua.ribbit.R;

public class ViewImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        Uri imageUri=getIntent().getData();

        Picasso.with(this).load(imageUri.toString()).into(imageView);

        //create a Timer object
        Timer timer=new Timer();
        //10 seconds * 1000 ms / s
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //after the delay, this code will be run
                //finish the current activity
                finish();
            }
        },10*1000);
    }


}
