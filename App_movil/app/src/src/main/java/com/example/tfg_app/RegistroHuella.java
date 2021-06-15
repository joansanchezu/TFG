package com.example.tfg_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroHuella extends AppCompatActivity {
    private static final int REGISTRO_HUELLA = 1;
    public static String valido;
    private String dataEncoded;
    private PublicKey pub;
    private PrivateKey priv;
    private Signature sign;
    private byte[] signature;
    private String signEncoded;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_huella);

        try {
            keysGenerator(MainActivity.username.getText().toString());
            sendPK();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    private void keysGenerator(String username) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(username, (KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY))
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setUserAuthenticationRequired(false)
                    .build()
        );
        keyPairGenerator.generateKeyPair();
    }

    private void sendPK() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        pub = keyStore.getCertificate(MainActivity.username.getText().toString()).getPublicKey();
        priv = (PrivateKey) keyStore.getKey(MainActivity.username.getText().toString(), null);

        byte[] data = pub.getEncoded();
        dataEncoded = "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.encode(data, Base64.DEFAULT)) + "-----END PUBLIC KEY-----";

        sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(priv);
        sign.update("test".getBytes());

        Intent recon = new Intent(this, ReconocimientoHuella.class);
        startActivityForResult(recon, REGISTRO_HUELLA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTRO_HUELLA) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    signature = sign.sign();
                } catch (SignatureException e) {
                    e.printStackTrace();
                }
                signEncoded = new String(Base64.encode(signature, Base64.DEFAULT));

                Map<String, String> parametros = new LinkedHashMap<>();
                HttpConn http = new HttpConn();

                parametros.put("url", "moodle/mod/exam/android/registro.php");
                parametros.put("activity", "Registro");
                parametros.put("pub", dataEncoded);
                parametros.put("signature", signEncoded);
                parametros.put("data", "test");
                try {
                    parametros.put("userid", MainActivity.user.getString("id"));
                    http.conn(parametros);
                } catch (InterruptedException | JSONException e) {
                    e.printStackTrace();
                }
                Intent course = new Intent(RegistroHuella.this, Course.class);
                startActivity(course);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //valido = data.getStringExtra("valido");
                System.out.println("ERROR!!");
            }
        }
    }
}
