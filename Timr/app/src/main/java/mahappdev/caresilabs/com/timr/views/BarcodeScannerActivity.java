package mahappdev.caresilabs.com.timr.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.zxing.Result;

import mahappdev.caresilabs.com.timr.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        this.setContentView(R.layout.activity_barcode_scanner);
        this.scannerView = (ZXingScannerView) findViewById(R.id.scannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        scannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Send back model
        Intent data = new Intent();
        data.putExtra("barcodeData", rawResult.getText());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
