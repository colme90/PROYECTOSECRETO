package colme.testserver;

/*
 * #%L
 * NanoHttpd-Samples
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;

import colme.testserver.Util.SemaforoUtil;
import colme.testserver.fingerprint.FingerprintAuthenticationDialog;
import colme.testserver.ventana.HeadLayer;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class HelloServer extends NanoCustom {
    public Context getContext() {
        return context;
    }

    private HeadLayer mHeadLayer;

    public void setContext(Context context) {
        this.context = context;
    }

    Context context = null;

    private static final Logger LOG = Logger.getLogger(HelloServer.class.getName());
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    /** Alias for our key in the Android Key Store */
    private static final String KEY_NAME = "my_key";

    FingerprintAuthenticationDialog mFragment;


    public HelloServer() {
        super(9443);
    }

    @Override
    public void start() throws IOException {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("BKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        InputStream stream = this.context.getResources().openRawResource(R.raw.redsys);
        try {
            keyStore.load(stream, "redsys".toCharArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        KeyManagerFactory keyManagerFactory = null;
        try {
            keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            keyManagerFactory.init(keyStore, "redsys".toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        super.setServerSocketFactory(new SecureServerSocketFactory(NanoCustom.makeSSLSocketFactory(keyStore, keyManagerFactory.getKeyManagers()), null));
        super.start();


    }


    @Override
    public Response serve(IHTTPSession session) {

        Method method = session.getMethod();
        String uri = session.getUri();
        String msg = "";
        HelloServer.LOG.info(method + " '" + uri + "' ");
        if (uri.equals("/")) {
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            openFingerprint(this.getContext());
            try {
                SemaforoUtil.LOCK.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(sharedPref.getBoolean(context.getString(R.string.fingerprint_success),false)) {
                openSelector(this.getContext());

                try {
                    SemaforoUtil.LOCK.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String tarjeta = sharedPref.getString(context.getString(R.string.tarjeta), "");
                msg = "var JQUERY4U = JQUERY4U || {};\n" +
                        "\n" +
                        "JQUERY4U.SETTINGS = \n" + tarjeta;
                ;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(context.getString(R.string.tarjeta), "");
                editor.putBoolean(context.getString(R.string.fingerprint_success),false);
                editor.apply();
                Response res = newFixedLengthResponse(msg);
                res.setMimeType("application/json");
                SemaforoUtil.LOCK.release();
            }
        }
        return newFixedLengthResponse(msg);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void openFingerprint(final Context context) {
            LOG.info("empezando autenticación");
            try {
                SemaforoUtil.LOCK.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
//                            mFragment = new FingerprintAuthenticationDialog(context);
                            Intent intent = new Intent (context, AskActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
            );

    }


    public void openSelector(final Context context) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        mHeadLayer = new HeadLayer(context);

                    }
                }
        );
    }





}

