package com.example.demo.helper;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
//import java.security.interfaces.ECPrivateKey;
//import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;

    /*
 This sample code illustrates below mention feature
    1) Diffie Hillman key exchange
    2) HKDF Aes encryption and decryption
 Tech used:
 1) BouncyCastle: (
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.66</version>
        </dependency>
        )
 2) JAVA

 NOTE: ---->
 Please use last to methods of this file if you are using PROJECTEKA-HIU For decryption.
 If you are making both the system by your self use this sample implementation
*/

import com.example.demo.helper.Service.VisitServiceHelper;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;

public class DataEncrypterDecrypter {
    static Logger logger = LoggerFactory.getLogger(DataEncrypterDecrypter.class);
    public static final String ALGORITHM = "ECDH";
    public static final String CURVE = "curve25519";
    public static final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
//
//        // Generate the DH keys for sender and receiver
//        KeyPair receiverKeyPair = generateKeyPair();
//        String receiverPrivateKey = getBase64String(getEncodedPrivateKey(receiverKeyPair.getPrivate()));
//        String receiverPublicKey = getBase64String(getEncodedPublicKeyFor(receiverKeyPair.getPublic()));
//
//        KeyPair senderKeyPair = generateKeyPair();
//        String senderPrivateKey = getBase64String(getEncodedPrivateKey(senderKeyPair.getPrivate()));
//        String senderPublicKey = getBase64String(getEncodedPublicKeyFor(senderKeyPair.getPublic()));
//
        String randomSender = "yIWKPmcq4BAxcmRjRLaFtAKFSXcBXdR1128iAd7KWqE=";
//        String randomReceiver = generateRandomKey();
//        System.out.println("sender Private key: " + senderPrivateKey);
//        System.out.println("sender public key: " + senderPublicKey);
//        System.out.println("sender nonce: " + randomSender);
//        System.out.println("receiver private key: "+ receiverPrivateKey);
//        System.out.println("receiver public key: " + receiverPublicKey);
//        System.out.println("receiver nonce: " + randomReceiver);
//
//        byte[] xorOfRandom = xorOfRandom(randomSender, randomReceiver);
        String encryptedData = "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";
//
//        String decryptedData = decrypt(xorOfRandom, receiverPrivateKey, senderPublicKey, encryptedData);
//        System.out.println("decryptedData: "+decryptedData);

    }
    public static HashMap<String, String> receiverKeys() {
        logger.info("Entering receive keys with no data");
        Security.addProvider(new BouncyCastleProvider());
        String privateKey = "";
        String publicKey = "";
        String random = "";

        // Generate the DH keys for sender and receiver
        try {
            KeyPair receiverKeyPair = generateKeyPair();
            privateKey = getBase64String(getEncodedPrivateKey(receiverKeyPair.getPrivate()));
            publicKey = getBase64String(getEncodedPublicKeyFor(receiverKeyPair.getPublic()));
            random = generateRandomKey();
            logger.info("privateKey = " + privateKey);
            logger.info("public key = " + publicKey);
            logger.info("random = " + random);
        }
        catch (Exception e) {
            logger.error("unable to generate keys : " + e);
            return null;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("privateKey", privateKey);
        map.put("publicKey", publicKey);
        map.put("random", random);
        logger.info("Exiting receiverKeys with data: " + map);
        return map;
    }
    public static String encryptFHIRData(String receiverPublicKey, String randomReceiver, String data, String senderPrivateKey, String randomSender) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            byte[] xorOfRandom = xorOfRandom(randomSender, randomReceiver);
            return encrypt(xorOfRandom, senderPrivateKey, receiverPublicKey, data);
        }
        catch (Exception e) {
            logger.error("Unable  to encrypt data: " + e);
            return null;
        }
    }
    public static String decrypt(String encryptedData, String senderPublicKey, String randomSender, String receiverPrivateKey, String randomReceiver) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            byte[] xorOfRandom = xorOfRandom(randomSender, randomReceiver);
            String decryptedData = decrypt(xorOfRandom, receiverPrivateKey, senderPublicKey, encryptedData);
            return decryptedData;
        }
        catch (Exception e) {
            logger.error("Unable to decrypt data");
            return null;
        }

    }

    // Method for encryption
    public static String encrypt(byte[] xorOfRandom, String senderPrivateKey, String receiverPublicKey, String stringToEncrypt) throws Exception {
        System.out.println("<------------------- ENCRYPTION -------------------->");
        // Generating shared secret
        String sharedKey = doECDH(getBytesForBase64String(senderPrivateKey), getBytesForBase64String(receiverPublicKey));
        System.out.println("Shared key: " + sharedKey);

        // Generating iv and HKDF-AES key
        byte[] iv = Arrays.copyOfRange(xorOfRandom, xorOfRandom.length - 12, xorOfRandom.length);
        byte[] aesKey = generateAesKey(xorOfRandom, sharedKey);
        System.out.println("HKDF AES key: " + getBase64String(aesKey));

        // Perform Encryption
        String encryptedData = "";
        try {
            byte[] stringBytes = stringToEncrypt.getBytes();

            GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            AEADParameters parameters =
                    new AEADParameters(new KeyParameter(aesKey), 128, iv, null);

            cipher.init(true, parameters);
            byte[] plainBytes = new byte[cipher.getOutputSize(stringBytes.length)];
            int retLen = cipher.processBytes
                    (stringBytes, 0, stringBytes.length, plainBytes, 0);
            cipher.doFinal(plainBytes, retLen);

            encryptedData = getBase64String(plainBytes);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println("EncryptedData: " + encryptedData);
        System.out.println("<---------------- Done ------------------->");
        return encryptedData;
    }

    // Method for decryption
    public static String decrypt(byte[] xorOfRandom, String receiverPrivateKey, String senderPublicKey, String stringToDecrypt) throws Exception {
        System.out.println("<------------------- DECRYPTION -------------------->");
        // Generating shared secret
        String sharedKey = doECDH(getBytesForBase64String(receiverPrivateKey),getBytesForBase64String(senderPublicKey));
        System.out.println("Shared key: " + sharedKey);

        // Generating iv and HKDF-AES key
        byte[] iv = Arrays.copyOfRange(xorOfRandom, xorOfRandom.length - 12, xorOfRandom.length);
        byte[] aesKey = generateAesKey(xorOfRandom, sharedKey);
        System.out.println("HKDF AES key: " + getBase64String(aesKey));

        // Perform Decryption
        String decryptedData = "";
        try {
            byte[] encryptedBytes = getBytesForBase64String(stringToDecrypt);

            GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
            AEADParameters parameters =
                    new AEADParameters(new KeyParameter(aesKey), 128, iv, null);

            cipher.init(false, parameters);
            byte[] plainBytes = new byte[cipher.getOutputSize(encryptedBytes.length)];
            int retLen = cipher.processBytes
                    (encryptedBytes, 0, encryptedBytes.length, plainBytes, 0);
            cipher.doFinal(plainBytes, retLen);

            decryptedData = new String(plainBytes);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        System.out.println("DecryptedData: " + decryptedData);
        System.out.println("<---------------- Done ------------------->");
        return decryptedData;
    }

    // Method for generating random string
    public static String generateRandomKey() {
        byte[] salt = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return getBase64String(salt);
    }

    // Method for generating DH Keys
    public static KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        X9ECParameters ecParameters = CustomNamedCurves.getByName(CURVE);
        ECParameterSpec ecSpec=new ECParameterSpec(ecParameters.getCurve(), ecParameters.getG(),
                ecParameters.getN(), ecParameters.getH(), ecParameters.getSeed());

        keyPairGenerator.initialize(ecSpec, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    private static PrivateKey loadPrivateKey (byte [] data) throws Exception
    {
        X9ECParameters ecP = CustomNamedCurves.getByName(CURVE);
        ECParameterSpec params=new ECParameterSpec(ecP.getCurve(), ecP.getG(),
                ecP.getN(), ecP.getH(), ecP.getSeed());
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return kf.generatePrivate(privateKeySpec);
    }

    private static PublicKey loadPublicKey (byte [] data) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());
        X9ECParameters ecP = CustomNamedCurves.getByName(CURVE);
        ECParameterSpec ecNamedCurveParameterSpec = new ECParameterSpec(ecP.getCurve(), ecP.getG(),
                ecP.getN(), ecP.getH(), ecP.getSeed());

        return KeyFactory.getInstance(ALGORITHM, PROVIDER)
                .generatePublic(new ECPublicKeySpec(ecNamedCurveParameterSpec.getCurve().decodePoint(data),
                        ecNamedCurveParameterSpec));
    }

    // Method for generating shared secret
    private static String doECDH (byte[] dataPrv, byte[] dataPub) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
        ka.init(loadPrivateKey(dataPrv));
        ka.doPhase(loadPublicKeyForProjectEKAHIU(dataPub), true);
        byte [] secret = ka.generateSecret();
        return getBase64String(secret);
    }

    // method to perform Xor of random keys
    private static byte [] xorOfRandom(String randomKeySender, String randomKeyReceiver)  {
        byte[] randomSender = getBytesForBase64String(randomKeySender);
        byte[] randomReceiver = getBytesForBase64String(randomKeyReceiver);

        byte[] out = new byte[randomSender.length];
        for (int i = 0; i < randomSender.length; i++) {
            out[i] = (byte) (randomSender[i] ^ randomReceiver[i%randomReceiver.length]);
        }
        return out;
    }

    // Method for generating HKDF AES key
    private static byte [] generateAesKey(byte[] xorOfRandoms, String sharedKey ){
        byte[] salt = Arrays.copyOfRange(xorOfRandoms, 0, 20);
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
        HKDFParameters hkdfParameters = new HKDFParameters(getBytesForBase64String(sharedKey), salt, null);
        hkdfBytesGenerator.init(hkdfParameters);
        byte[] aesKey = new byte[32];
        hkdfBytesGenerator.generateBytes(aesKey, 0, 32);
        return aesKey;
    }

    public static String getBase64String(byte[] value){

        return new String(org.bouncycastle.util.encoders.Base64.encode(value));
    }

    public static byte[] getBytesForBase64String(String value){
        return org.bouncycastle.util.encoders.Base64.decode(value);
    }

    public static byte [] getEncodedPublicKey(PublicKey key) throws Exception  {
        ECPublicKey ecKey = (ECPublicKey)key;
        return ecKey.getQ().getEncoded(true);
    }

    public static byte [] getEncodedPrivateKey(PrivateKey key) throws Exception  {
        ECPrivateKey ecKey = (ECPrivateKey)key;
        return ecKey.getD().toByteArray();
    }

    /*
     If using ProjectEka HIU for the decryption then Please use below methods for converting public keys
    * */
    // Replacement for ------> getEncodedPublicKey
    public static byte[] getEncodedPublicKeyFor (PublicKey key){
        ECPublicKey ecKey = (ECPublicKey)key;
        return ecKey.getEncoded();
    }

    // Replacement for ------> loadPublicKey
    private static PublicKey loadPublicKeyForProjectEKAHIU (byte [] data) throws Exception {
        KeyFactory ecKeyFac = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(data);
        PublicKey publicKey = ecKeyFac.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }
}
