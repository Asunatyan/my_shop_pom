package com.tamakiakoo.utils;

import java.util.Base64;

public class Base64Util {

    public static String encodingBase64(String content) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(content.getBytes());
    }

    public static String decodingBase64(String content) {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(content.getBytes()));
    }


    public static void main(String[] args) {
        /*String hello_word = encodingBase64("hello word");

        System.out.println(hello_word);

        String s = decodingBase64(hello_word);
        System.out.println(s);*/
    }
}
