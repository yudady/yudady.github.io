package tk.tommy;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * java生成openssl兼容的rsa 公私钥
 * <p>
 * KeyPairGenerator生成的 私钥是pkcs8格式，需要先转为pkcs1格式，再由pkcs1转为pem格式(openssl生成的pem)
 * KeyPairGenerator生成的 公钥是x509格式,需要替换_为/,替换-为+，格式化后，加-----BEGIN PUBLIC KEY-----头，-----END PUBLIC KEY-----尾，就与openssl生成的公钥一致了
 * <p>
 * 根据私钥生成公钥，公私钥是一对一 openssl rsa -pubout -in rsa_private_key.pem -out rsa_public_key.pem
 */
public class FileToPEM {

    public static void main(String[] args) throws Exception {
        final String privatePath = Paths.get(KeyPairGeneratorTest.class.getClassLoader()
            .getResource("pkcs8.pem").toURI()).toFile().getPath();
        final PrivateKey priKey = FileToPEM.getPemPrivateKey(privatePath, "RSA");
        System.out.println("[LOG] priKey = " + Base64.encodeBase64URLSafeString(priKey.getEncoded()));

        final String publicPath = Paths.get(KeyPairGeneratorTest.class.getClassLoader()
            .getResource("public.pem").toURI()).toFile().getPath();
        final PublicKey pubKey = FileToPEM.getPemPublicKey(publicPath, "RSA");
        System.out.println("[LOG] priKey = " + Base64.encodeBase64URLSafeString(pubKey.getEncoded()));
        final KeyPair pair = new KeyPair(pubKey, priKey);
        System.out.println("[LOG] pair.getPrivate() = " + pair.getPrivate());
        System.out.println("[LOG] pair.getPublic() = " + pair.getPublic());
        System.out.println("-------------------------");
        System.out.println("-------------------------");
        System.out.println("-------------------------");

    }

    public static PrivateKey getPemPrivateKey(String filename, String algorithm) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        String temp = new String(keyBytes);
        String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
        //System.out.println("Private key\n"+privKeyPEM);

        Base64 b64 = new Base64();
        byte[] decoded = b64.decode(privKeyPEM);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPemPublicKey(String filename, String algorithm) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        String temp = new String(keyBytes);
        String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");


        Base64 b64 = new Base64();
        byte[] decoded = b64.decode(publicKeyPEM);

        X509EncodedKeySpec spec =
            new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }


}