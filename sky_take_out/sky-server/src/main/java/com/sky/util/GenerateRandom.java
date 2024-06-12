package com.sky.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateRandom {
    private static final String BASE_PREFIX = "xxxxxxxx-zxxx-qxxx-yxxx-xxxxxxxxxxxx"; // BASE64编码的32位UUID，y位替换
    private static final Random RANDOM = new Random();

    public static String randomId(String prefix) {
        long timestamp = System.nanoTime();  // 使用纳秒级时间戳
        int randomNum = ThreadLocalRandom.current().nextInt(10000);  // 使用线程安全的随机数生成器
        String id = prefix + ":" + timestamp + ":" + randomNum;

        // 使用BASE64编码将ID长度控制为32-50位
        String encodedID = java.util.Base64.getEncoder().encodeToString(id.getBytes());
        if (encodedID.length() > 50) {
            encodedID = encodedID.substring(0, 50);
        }
        return encodedID;
    }

    public static String randomPass(){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<8;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
