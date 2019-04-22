package security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter extends SymmetricKeyLoader {
    private static final Logger LOGGER = LogManager.getFormatterLogger(Encrypter.class.getName());

    public Encrypter() {

    }

    Encrypter(String secretKey) {
        super(secretKey);
    }

    public byte[] encrypt(byte[] bytesToEncrypt) {
        byte[] encryptedBytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedBytes = cipher.doFinal(bytesToEncrypt);
        } catch (Exception e){
            LOGGER.error("Unable to encrypt due to missing algorithm.");
        }
        return encryptedBytes;
    }
}
