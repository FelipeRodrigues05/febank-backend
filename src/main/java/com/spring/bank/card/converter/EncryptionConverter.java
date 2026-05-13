package com.spring.bank.card.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionConverter {

    private static final int KEY_LENGTH = 32;
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private final SecretKeySpec secretKey;

    public EncryptionConverter(@Value("${app.security.encryption-key:Nexwallet2025DevKeyABC!!!!!!!!}") String key) {
        byte[] keyBytes = key.substring(0, KEY_LENGTH).getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception error) {
            throw new RuntimeException("Encryption failed", error);
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)), StandardCharsets.UTF_8);
        } catch (Exception error) {
            throw new RuntimeException("Decryption failed", error);
        }
    }
}
