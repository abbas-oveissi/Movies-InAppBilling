package ir.oveissi.moviesinappbilling;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import com.farsitel.bazaar.ILoginCheckService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import ir.oveissi.moviesinappbilling.iabhelpers.IabHelper;
import ir.oveissi.moviesinappbilling.iabhelpers.IabResult;
import ir.oveissi.moviesinappbilling.iabhelpers.Inventory;
import ir.oveissi.moviesinappbilling.iabhelpers.Purchase;

public class DetailActivity extends AppCompatActivity {

    private static final String SKU_FULL_VERSION = "FULLVERSION";
    private static final String SKU_DOWNLOAD = "DOWNLOAD";
    private static final String SKU_STREAMING = "STREAMING";
    private static final String TAG = DetailActivity.class.getSimpleName();

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

    ILoginCheckService service;

    LoginCheckServiceConnection connection;

    String title;
    String description;
    String poster;
    Float rate;
    String screenshot;
    private IabHelper mHelper;
    private boolean isLoggedIn;

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

        initService();
        setupListeners();

    }

    private void setupListeners() {
        playVideoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserBuyStreaming();
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
                isUserBuyFullVersion();
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

    private void isUserBuyFullVersion() {
        if (!isCafeInstalled()) {
            Toast.makeText(this, R.string.cafebazaar_isnot_installed_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isUserLoggedInCafe()) {
            Toast.makeText(this, R.string.cafebazaar_user_isnt_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }

        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    Toast.makeText(DetailActivity.this, R.string.query_inventory_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inventory.hasPurchase(SKU_FULL_VERSION)) {
                    bookmarkMovie();
                } else {
                    buyFullVersion();
                }
            }
        });
    }

    private void buyFullVersion() {
        mHelper.launchPurchaseFlow(this, SKU_FULL_VERSION, 1, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Toast.makeText(DetailActivity.this, R.string.purchasing_error, Toast.LENGTH_SHORT).show();
                    return;
                } else if (info.getSku().equals(SKU_FULL_VERSION)) {
                    bookmarkMovie();
                }
            }
        });
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

    private void isUserBuyStreaming() {
        if (!isCafeInstalled()) {
            Toast.makeText(this, R.string.cafebazaar_isnot_installed_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isUserLoggedInCafe()) {
            Toast.makeText(this, R.string.cafebazaar_user_isnt_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }

        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    Toast.makeText(DetailActivity.this, R.string.query_inventory_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inventory.hasPurchase(SKU_STREAMING)) {
                    openPlayer();
                } else {
                    buyStreaming();
                }
            }
        });
    }

    private void openPlayer() {
        Intent intent = new Intent(DetailActivity.this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.KEY_SCREENSHOT, screenshot);
        startActivity(intent);
    }

    private void buyStreaming() {
        mHelper.launchPurchaseFlow(this, SKU_STREAMING, 1, new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Toast.makeText(DetailActivity.this, R.string.purchasing_error, Toast.LENGTH_SHORT).show();
                    return;
                } else if (info.getSku().equals(SKU_STREAMING)) {
                    openPlayer();
                }
            }
        });
    }

    private boolean isUserLoggedInCafe() {
        return isLoggedIn;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
        releaseService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private void initService() {
        Log.i(TAG, "initService()");
        connection = new LoginCheckServiceConnection();
        Intent i = new Intent(
                "com.farsitel.bazaar.service.LoginCheckService.BIND");
        i.setPackage("com.farsitel.bazaar");
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "initService() bound value: " + ret);
    }

    /** This is our function to un-binds this activity from our service. */
    private void releaseService() {
        unbindService(connection);
        connection = null;
        Log.d(TAG, "releaseService(): unbound.");
    }

    public class LoginCheckServiceConnection implements ServiceConnection {

        private static final String TAG = "LoginCheck";

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = ILoginCheckService.Stub
                    .asInterface((IBinder) boundService);
            try {
                isLoggedIn = service.isLoggedIn();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "onServiceConnected(): Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.e(TAG, "onServiceDisconnected(): Disconnected");
        }
    }
}
