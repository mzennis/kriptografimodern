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

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DecryptFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_decrypt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText textToEncrypt = view.findViewById(R.id.texttobederypted);
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
                    strResult = decryptEcb(strToEncrypt, secretKey);
                } else if (mode.equals("CBC")) {
                    strResult = decryptCbc(strToEncrypt, secretKey);
                }

                result.setText(strResult);
            }
        });
    }

    public static String decryptEcb(String input, String key) {
        byte[] output = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (output != null) {
            return new String(output);
        }
        return "";
    }


    private static byte[] ivBytes = {
            // use your own.
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00
    };

    private static String decryptCbc(String input, String key) {
        try {
            byte[] textBytes = Base64.decode(input, Base64.DEFAULT);

            byte[] keyBytes = key.getBytes();

            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

            byte[] decodedBytes = cipher.doFinal(textBytes);

            return new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return "";
    }


}