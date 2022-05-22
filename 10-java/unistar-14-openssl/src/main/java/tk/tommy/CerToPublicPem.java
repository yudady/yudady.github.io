package tk.tommy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.apache.commons.codec.binary.Base64;

/**
 * BEGIN CERTIFICATE 转成 BEGIN PUBLIC KEY：
 * 如果对方给的cer格式的证书,需要转换之成java可以使用的PKCS#8格式密钥,具体如下
 */
public class CerToPublicPem {
    /**
     * BEGIN CERTIFICATE格式解析密钥
     *
     * @Return: java.security.PublicKey
     */
    public static String getCerToPublicKey() throws FileNotFoundException, CertificateException {
        FileInputStream file = new FileInputStream("D://publicKey.cer");

        CertificateFactory ft = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) ft.generateCertificate(file);
        PublicKey publicKey = certificate.getPublicKey();

        String strKey = "-----BEGIN PUBLIC KEY-----\n"
            + Base64.encodeBase64String(publicKey.getEncoded())
            + "\n-----END PUBLIC KEY-----";
        System.out.println(strKey);
        return strKey;
    }
}
