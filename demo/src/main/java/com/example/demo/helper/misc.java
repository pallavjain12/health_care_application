package com.example.demo.helper;

import com.example.demo.utility.SessionUtil;
import org.apache.tomcat.Jar;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import static com.example.demo.helper.DataEncrypterDecrypter.*;

public class misc {

    public static String getTimeStamp() {
        return ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
    }

    public static UUID getRandomUUID() {
        return java.util.UUID.randomUUID();
    }

    public static String convertDateTOZonedUTC(String date) {
        return ZonedDateTime.of(LocalDateTime.parse(date + "T00:00:00.00000001"), ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static void main(String[] args) {
        HashMap<String, String> keys = receiverKeys();
        String receiverPublicKey = keys.get("publicKey");
        String receiverPrivateKey = keys.get("privateKey");
        String receiverRandom = keys.get("random");

        keys = receiverKeys();
        String senderPublicKey = keys.get("publicKey");
        String senderPrivateKey = keys.get("privateKey");
        String senderRandom = keys.get("random");

        String myData = "Nuclear Launch codes : 3h4b5b6n4j2oe90bn43n2k";

        String encrptedData = encryptFHIRData(receiverPublicKey, receiverRandom, myData, senderPrivateKey, senderRandom);

        System.out.println("encrptedData = " + encrptedData);

        String decryptedData = decrypt(encrptedData, senderPublicKey, senderRandom,receiverPrivateKey, receiverRandom);

        System.out.println(decryptedData);
    }
}
