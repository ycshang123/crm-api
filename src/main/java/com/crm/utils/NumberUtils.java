package com.crm.utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-10-25 10:19
 **/
public class NumberUtils {
    public static String generateContractNumber() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timePart = LocalDateTime.now().format(formatter);
        String randomPart = getRandomString(4);
        return "HT" + timePart + randomPart;
    }

    /**
     * 生成指定长度的随机字符串（大写字母 + 数字）
     */
    public static String getRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
