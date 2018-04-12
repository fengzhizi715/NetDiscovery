package com.cv4j.netdiscovery.admin.common;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
public class CommonUtil {
    public static HttpEntity getHttpEntityForRequest(JsonObject jsonObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObject.toString(), headers);

        return requestEntity;
    }

    public static boolean isValidQueryParam(String val) {
        if(val == null || "".equals(val) || "all".equalsIgnoreCase(val)) {
            return false;
        }
        return true;
    }

    //编码
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        System.out.println("unicodeBytes is: " + unicodeBytes);
        return unicodeBytes;
    }

    //解码
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }


    public static String getString(String str) {
        if(str == null || "".equals(str)) {
            return "";
        } else {
            return str;
        }
    }

    public static boolean isNull(String str) {
        if(str == null || "".equals(str)) {
            return true;
        }

        return false;
    }

    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".")-1, filePath.length());
    }

    public static String getErrorMsg(String text, String msg) {
        if(isNull(msg)) {
            return "";
        } else {
            return text + "：" + msg + "</br>";
        }
    }

    public static int getIntString(String str) {
        if(isNull(str)) return 0;

        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }

        return result;
    }

    public static float getFloatString(String str) {
        if(isNull(str)) return 0.0f;

        float result = 0.0f;
        try {
            result = Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return 0.0f;
        }

        return result;
    }

    /**
 　　* 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
 　　* @param sourceDate
 　　* @param formatLength
 　　* @return 重组后的数据
 　　*/
    public static String fillFrontWithZero(int str, int len) {
        return String.format("%0"+len+"d", str);
    }


}