package ir.oveissi.moviesinappbilling;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class PlayerActivity extends AppCompatActivity {

    public static final String KEY_SCREENSHOT = "SCREENSHOT";

    ImageView screenshotImg;
    String screenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        screenshotImg = findViewById(R.id.screenshot_img);
        screenshot = getIntent().getExtras().getString(KEY_SCREENSHOT);

        Picasso.get().load(screenshot).into(screenshotImg);
    }
}
