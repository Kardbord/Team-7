package security;

import java.util.ResourceBundle;

public abstract class SymmetricKeyLoader {
    private static final String defaultSecretKey = "MNZezoPMc2FEBYEY8TB7BtxUmkdZxXlO"; // Note that this default key only exists for grading purposes
    protected String secretKey;

    public SymmetricKeyLoader() {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("config.properties");
            secretKey = resourceBundle.getString("secretKey");
        } catch (Exception e) {
            secretKey = defaultSecretKey;
        }
    }

    SymmetricKeyLoader(String secretKey) {
        this.secretKey = secretKey;
    }
}
