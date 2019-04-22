package security;

import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertArrayEquals;

public class DecrypterTest {
    private static final String expectedSecretKey = "test8hkwVg2ps2QSEcB8aJIJ2OqDlJKu";
    private Decrypter victim = new Decrypter(expectedSecretKey);

    @Test
    public void decryptShouldDecryptUsingAesAndTheSpecifiedSecretKey() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] bytesToDecrypt = new Encrypter(expectedSecretKey).encrypt(new byte[] {'T', 'E', 'S', 'T'});

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(expectedSecretKey.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] expectedMessageBytes = cipher.doFinal(bytesToDecrypt);

        byte[] actualMessageBytes = victim.decrypt(bytesToDecrypt);

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }
}