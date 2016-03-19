package colme.testserver;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.util.logging.Logger;

import colme.testserver.ventana.HeadLayer;
import colme.testserver.ventana.SettingsFragment;

public class MainActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(HelloServer.class.getName());
    private HeadLayer mHeadLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HelloServer helloServer = new HelloServer();
        helloServer.setContext(this.getApplicationContext());
        try {
            LOG.info("Iniciando Server");
            helloServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.info(e.getMessage());
        }

        SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }


}
