package security;

import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class EncrypterTest {
    private static final String expectedSecretKey = "test8hkwVg2ps2QSEcB8aJIJ2OqDlJKu";
    private Encrypter victim = new Encrypter(expectedSecretKey);

    @Test
    public void decryptShouldDecryptUsingAesAndTheSpecifiedSecretKey() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] bytesToEncrypt = new byte[] {'T', 'E', 'S', 'T'};

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(expectedSecretKey.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, k);
        byte[] expectedMessageBytes = cipher.doFinal(bytesToEncrypt);

        byte[] actualMessageBytes = victim.encrypt(bytesToEncrypt);

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }
}