package ir.oveissi.moviesinappbilling;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import ir.oveissi.moviesinappbilling.iabhelpers.IabHelper;
import ir.oveissi.moviesinappbilling.iabhelpers.IabResult;
import ir.oveissi.moviesinappbilling.iabhelpers.Purchase;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final String SKU_DOWNLOAD = "DOWNLOAD";

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
    private IabHelper mHelper;


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

        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwD4iLpCmSQxHsuw+uT5smFvP2qj5zZHMUXptVcsDKQ5LCuy98s3Gx/u6fUfLo4tuWtF8Yr1xxJJ4IOa7wu7tV19DJ24FBH+fXdb/LU8ymBQLQEQImZ5ygc/66v38djRel7hdAqBx0zWsPHJJ62vedMC2jKTCBLtmwJUc2h7kJVJzF+8meI7puzZrdKodz285E1HXFsS0VDNFiGDNzBE/Y1wYPiBIv6IozdcHifgRdsCAwEAAQ==";
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Toast.makeText(DetailActivity.this, R.string.setting_up_inapp_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                buyDownload();
            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmarkMovie();
            }
        });
    }

    private void buyDownload() {
        if (!isCafeInstalled()) {
            Toast.makeText(this, R.string.cafebazaar_isnot_installed_error, Toast.LENGTH_SHORT).show();
            return;
        }

        mHelper.launchPurchaseFlow(this, SKU_DOWNLOAD, 1, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Log.d(TAG, getResources().getString(R.string.purchasing_error) + result);
                    return;
                } else if (info.getSku().equals(SKU_DOWNLOAD)) {
                    consumeDownloadPurchase(info);
                }
            }
        });
    }

    private void consumeDownloadPurchase(Purchase purchase) {
        mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                if (result.isFailure()) {
                    Toast.makeText(DetailActivity.this, R.string.purchasing_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                startDownloading();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.handleActivityResult(requestCode, resultCode, data);
    }


    private boolean isCafeInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.farsitel.bazaar", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
