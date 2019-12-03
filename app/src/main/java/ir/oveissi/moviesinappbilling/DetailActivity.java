package ir.oveissi.moviesinappbilling;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_DESCRIPTION = "DESCRIPTION";
    public static final String KEY_POSTER = "POSTER";
    public static final String KEY_RATE = "RATE";
    public static final String KEY_SCREENSHOT = "SCREENSHOT";

    FloatingActionButton playVideoFab;
    Button bookmarkBtn, downloadBtn;
    TextView descriptionTv, titleTv, rateTv;
    RatingBar ratingRb;
    ImageView posterImg;

    String title;
    String description;
    String poster;
    Float rate;
    String screenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        playVideoFab = findViewById(R.id.play_fab);
        downloadBtn = findViewById(R.id.download_btn);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        descriptionTv = findViewById(R.id.description_tv);
        titleTv = findViewById(R.id.title_tv);
        rateTv = findViewById(R.id.rate_tv);
        ratingRb = findViewById(R.id.rate_rb);
        posterImg = findViewById(R.id.poster_img);

        title = getIntent().getExtras().getString(KEY_TITLE);
        description = getIntent().getExtras().getString(KEY_DESCRIPTION);
        poster = getIntent().getExtras().getString(KEY_POSTER);
        rate = getIntent().getExtras().getFloat(KEY_RATE);
        screenshot = getIntent().getExtras().getString(KEY_SCREENSHOT);

        titleTv.setText(title);
        descriptionTv.setText(description);
        rateTv.setText(String.valueOf(rate));
        ratingRb.setRating(rate);
        Picasso.get().load(poster).into(posterImg);

        setupListeners();

    }

    private void setupListeners() {
        playVideoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPlayer();
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloading();
            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmarkMovie();
            }
        });
    }

    private void startDownloading() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.download_dialog_title));
        progressDialog.setMessage(getResources().getString(R.string.download_dialog_message));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);
    }


    private void bookmarkMovie() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.bookmark_dialog_title))
                .setMessage(getResources().getString(R.string.bookmark_dialog_message))
                .setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void openPlayer() {
        Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.KEY_SCREENSHOT, screenshot);
        startActivity(intent);
    }

}
