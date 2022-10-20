package tk.tommy;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.util.encoders.Base64;

public class SignTest {
    /**
     * RSA驗簽名檢查
     *
     * @param content        待簽名數據
     * @param sign           簽名值
     * @param ali_public_key 支付寶公鑰
     * @param input_charset  編碼格式
     * @return 布爾值
     */
    public static boolean verify(String content, String sign, String ali_public_key, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(ali_public_key);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature
                .getInstance("RSA");

            signature.initVerify(pubKey);
            signature.update(content.getBytes(input_charset));

            boolean bverify = signature.verify(Base64.decode(sign));
            return bverify;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 校驗數字簽名
     *
     * @param data      加密數據
     * @param publicKey 公鑰
     * @param sign      數字簽名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        //解密公鑰
        byte[] keyBytes = Base64.decode(publicKey.getBytes());
        //構造X509EncodedKeySpec對象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        //取公鑰匙對象
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

        Signature signature = Signature.getInstance("");
        signature.initVerify(publicKey2);
        signature.update(data);
        //驗證簽名是否正常
        return signature.verify(Base64.decode(sign));

    }
}
