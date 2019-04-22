package security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Decrypter extends SymmetricKeyLoader {
    private static final Logger LOGGER = LogManager.getFormatterLogger(Decrypter.class.getName());

    public Decrypter() {

    }

    Decrypter(String secretKey) {
        super(secretKey);
    }

    public byte[] decrypt(byte[] bytesToDecrypt) {
        byte[] decryptedBytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedBytes = cipher.doFinal(bytesToDecrypt);
        } catch (Exception e){
            LOGGER.error("Unable to decrypt due to missing algorithm.");
        }
        return decryptedBytes;
    }
}
