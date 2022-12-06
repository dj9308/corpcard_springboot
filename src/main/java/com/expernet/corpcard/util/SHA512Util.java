package com.expernet.corpcard.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512Util {
    /**
     * SHA-512 암호화
     *
     * @param password : 비밀번호
     * @return Encrypted String
     */
    public static String SHA512Encode(String password) throws NoSuchAlgorithmException {
        String encrypted;

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.reset();
        md.update(password.getBytes(StandardCharsets.UTF_8));

        encrypted = String.format("%0128x", new BigInteger(1, md.digest()));

        return encrypted;
    }

    /**
     * SHA-512 비밀번호 체크
     *
     * @param data
     * @param encoded
     * @param salt
     * @return boolean
     * @throws Exception
     */
    public static boolean checkPassword(String data, String encoded, byte[] salt) throws Exception {
        String hashValue;

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.reset();
        md.update(data.getBytes(StandardCharsets.UTF_8));
        hashValue = String.format("%0128x", new BigInteger(1, md.digest()));

        return MessageDigest.isEqual(hashValue.getBytes(StandardCharsets.UTF_8),
                encoded.getBytes(StandardCharsets.UTF_8));
    }
}
