package id.meyta;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_encrypt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText textToEncrypt = view.findViewById(R.id.texttobeencrypted);
        final Spinner spinnerMode = view.findViewById(R.id.spinnermode);
        final Spinner spinnerBits = view.findViewById(R.id.spinnerbits);

        final EditText textSecretKey = view.findViewById(R.id.textsecretkey);
        Button btn = view.findViewById(R.id.btn);

        final EditText result = view.findViewById(R.id.result);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strToEncrypt = textToEncrypt.getText().toString();
                if (strToEncrypt.isEmpty()) {
                    Toast.makeText(getContext(), "Text tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                String secretKey = textSecretKey.getText().toString();
                if (secretKey.isEmpty()) {
                    Toast.makeText(getContext(), "Secret Key tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                String bits = (String) spinnerBits.getSelectedItem();

                int bitsize = 0;
                switch (bits) {
                    case "128":
                        bitsize = 16;
                        break;
                    case "192":
                        bitsize = 24;
                        break;
                    case "256":
                        bitsize = 32;
                        break;
                }

                if (secretKey.length() != bitsize) {
                    Toast.makeText(getContext(), "Secret Key harus memiliki panjang " + bitsize + " atau " + bits + " bits", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mode = (String) spinnerMode.getSelectedItem();

                String strResult = "";
                if (mode.equals("ECB")) {
                    strResult = encryptEcb(strToEncrypt, secretKey);
                } else if (mode.equals("CBC")) {
                    strResult = encryptCbc(strToEncrypt, secretKey);
                }

                result.setText(strResult);
            }
        });
    }

    private static String encryptEcb(String input, String key) {
        byte[] crypted = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return Base64.encodeToString(crypted, Base64.DEFAULT);
    }

    private static byte[] ivBytes = {
            // use your own.
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00
    };

    private static String encryptCbc(String text, String key) {
        byte[] keyBytes = key.getBytes();

        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher;
        byte[] encryptedBytes = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
            encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (encryptedBytes != null)
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        return "";
    }
}
