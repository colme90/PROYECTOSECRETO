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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import colme.testserver.ventana.HeadLayer;
import fi.iki.elonen.NanoHTTPD;

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
    private final Semaphore available = new Semaphore(1, true);

    Context context = null;

    private static final Logger LOG = Logger.getLogger(HelloServer.class.getName());



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
        HelloServer.LOG.info(method + " '" + uri + "' ");
        openSelector(this.getContext(),available);
//        mHeadLayer = new HeadLayer(this.getContext());
        try {
            available.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key),Context.MODE_PRIVATE);

        String tarjeta = sharedPref.getString(context.getString(R.string.tarjeta),"");
        String msg = "var JQUERY4U = JQUERY4U || {};\n" +
                "\n" +
                "JQUERY4U.SETTINGS = \n" + tarjeta;
                ;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.tarjeta),"");
        editor.commit();
        Response res = newFixedLengthResponse(msg);
        res.setMimeType("application/json");
        return newFixedLengthResponse(msg);
    }

    public void openSelector(final Context context,final Semaphore available)
    {
        try {
            available.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        mHeadLayer = new HeadLayer(context,available);

                    }
                }
        );
    }


}

