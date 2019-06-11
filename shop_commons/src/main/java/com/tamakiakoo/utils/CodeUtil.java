package com.tamakiakoo.utils;

import java.util.Random;

public class CodeUtil {

    //1000-9999
    public static int getCode(){
        return (int)(Math.random() * 9000 + 1000);
    }

}
