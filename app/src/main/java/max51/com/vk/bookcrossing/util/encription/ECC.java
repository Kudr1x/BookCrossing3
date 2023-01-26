package max51.com.vk.bookcrossing.util.encription;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.*;
import java.io.File;
import  java.io.FileInputStream;
import java.lang.reflect.Field;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;

public class ECC{

    private final static int KEY_SIZE=256;

    private final static String SIGNATURE="SHA256withECDSA";

    static{
        Security.addProvider(new BouncyCastleProvider());
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());
    }

    private static void printProvider(){
        Provider provider=new BouncyCastleProvider();
        for(Provider.Service service:provider.getServices()){
            System.out.println(service.getType()+":"+service.getAlgorithm());
        }
    }

    public static KeyPair getKeyPair() throws Exception{
        //BC即BouncyCastle加密包，EC为ECC算法
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("EC","BC");
        keyPairGenerator.initialize(KEY_SIZE,new SecureRandom());
        KeyPair keyPair=keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public static String getPublicKey(KeyPair keyPair){
        ECPublicKey publicKey=(ECPublicKey)keyPair.getPublic();
        byte[] bytes=publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getPrivateKey(KeyPair keyPair){
        ECPrivateKey privateKey=(ECPrivateKey)keyPair.getPrivate();
        byte[] bytes=privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] encrypt(byte[] content,ECPublicKey publicKey) throws Exception{
        Cipher cipher=Cipher.getInstance("ECIES","BC");
        //setFieldValueByFieldName(cipher);
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        return cipher.doFinal(content);
    }

    public static byte[] decrypt(String content,ECPrivateKey privateKey) throws Exception{
        Cipher cipher=Cipher.getInstance("ECIES","BC");
//        setFieldValueByFieldName(cipher);
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(Base64.getDecoder().decode(content));
    }

    private static void setFieldValueByFieldName(Cipher object){
        if(object==null){
            return;
        }
        Class cipher=object.getClass();
        try{
            //获取该类的成员变量CryptoPermission cryptoPerm;
            Field cipherField=cipher.getDeclaredField("cryptoPerm");
            cipherField.setAccessible(true);
            Object cryptoPerm=cipherField.get(object);

            //获取CryptoPermission类的成员变量maxKeySize
            Class c=cryptoPerm.getClass();

            Field[] cs=c.getDeclaredFields();
            Field cryptoPermField=c.getDeclaredField("maxKeySize");
            cryptoPermField.setAccessible(true);
            //设置maxKeySize的值为257，257>256
            cryptoPermField.set(cryptoPerm,257);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] sign(String content,ECPrivateKey privateKey) throws Exception {
        Signature signature=Signature.getInstance(SIGNATURE);
        signature.initSign(privateKey);
        signature.update(content.getBytes());
        return signature.sign();
    }

    public static boolean verify(String content,byte[] sign,ECPublicKey publicKey) throws Exception{
        Signature signature=Signature.getInstance(SIGNATURE);
        signature.initVerify(publicKey);
        signature.update(content.getBytes());
        return signature.verify(sign);
    }

    private static String getSignature(File certFile) throws Exception{
        CertificateFactory certificateFactory=CertificateFactory.getInstance("X.509","BC");
        X509Certificate x509Certificate=(X509Certificate) certificateFactory.generateCertificate(new FileInputStream(certFile));
        return x509Certificate.getSigAlgName();
    }

    public static ECPublicKey string2PublicKey(String pubStr) throws Exception{
        byte[] keyBytes=Base64.getDecoder().decode(pubStr);
        X509EncodedKeySpec keySpec=new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory=KeyFactory.getInstance("EC","BC");
        ECPublicKey publicKey=(ECPublicKey)keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static ECPrivateKey string2PrivateKey(String priStr)throws Exception{
        byte[] keyBytes=Base64.getDecoder().decode(priStr);
        PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory=KeyFactory.getInstance("EC","BC");
        ECPrivateKey privateKey=(ECPrivateKey) keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
}