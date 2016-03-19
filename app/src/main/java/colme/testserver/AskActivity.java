package colme.testserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import colme.testserver.fingerprint.FingerprintAuthenticationDialog;

public class AskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ask);
        FingerprintAuthenticationDialog fingerPrint = new FingerprintAuthenticationDialog(this);
        View v = fingerPrint.createView();

        FrameLayout layout = new FrameLayout(this);

        setContentView(v);
        fingerPrint.startListen();
    }
}
