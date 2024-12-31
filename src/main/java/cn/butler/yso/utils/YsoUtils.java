package cn.butler.yso.utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.faces.FacesException;
import java.security.SecureRandom;

public class YsoUtils {
    //JSF加密辅助对象,源自com.sun.faces.renderkit.ByteArrayGuard
    private SecretKey facesSecretKey;

    public void setSk(SecretKey sk) {
        this.facesSecretKey = sk;
    }

    /**
     * JSF的AES加密逻辑,源自com.sun.faces.renderkit.ByteArrayGuard
     * @param bytes
     * @return
     */
    public byte[] jsfFacesEncrypt(byte[] bytes) {
        byte[] securedata = null;
        try {
            SecureRandom rand = new SecureRandom();
            byte[] iv = new byte[16];
            rand.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(1, this.facesSecretKey, ivspec);
            Mac encryptMac = Mac.getInstance("HmacSHA256");
            encryptMac.init(this.facesSecretKey);
            encryptMac.update(iv);
            byte[] encdata = encryptCipher.doFinal(bytes);
            byte[] macBytes = encryptMac.doFinal(encdata);
            byte[] tmp = concatBytes(macBytes, iv);
            securedata = concatBytes(tmp, encdata);
            return securedata;
        } catch (Exception var11) {
            return null;
        }
    }

    /**
     * JSF加密逻辑辅助类,源自com.sun.faces.renderkit.ByteArrayGuard
     * @param array1
     * @param array2
     * @return
     */
    private static byte[] concatBytes(byte[] array1, byte[] array2) {
        byte[] cBytes = new byte[array1.length + array2.length];

        try {
            System.arraycopy(array1, 0, cBytes, 0, array1.length);
            System.arraycopy(array2, 0, cBytes, array1.length, array2.length);
            return cBytes;
        } catch (Exception var4) {
            throw new FacesException(var4);
        }
    }
}
