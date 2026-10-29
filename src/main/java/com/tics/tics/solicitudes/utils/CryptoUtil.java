package com.tics.tics.solicitudes.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CryptoUtil {
    // Clave secreta de 16 bytes (para AES-128). En producción, esto debe ir en el application.properties
    private static final String SECRET_KEY = "T1CS_Secret_Key_";
    private static final String ALGORITHM = "AES";

    public static String encriptar(String texto) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytesEncriptados = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytesEncriptados);
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar la CURP", e);
        }
    }

    public static String desencriptar(String textoEncriptado) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] bytesDesencriptados = cipher.doFinal(Base64.getDecoder().decode(textoEncriptado));
            return new String(bytesDesencriptados, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Error al desencriptar";
        }
    }
}