package com.songdesy.untils;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * <p>Title:字符编码工具类 </p>
 *
 * @author : songsong.wu
 * @version 1.0
 */
public class CharUtil {

    /**
     * 转换编码 ISO-8859-1到GB2312
     */
    public static String iso2gb(String text) {
        String result;
        try {
            result = new String(text.getBytes("ISO-8859-1"), "GB2312");
        } catch (UnsupportedEncodingException ex) {
            result = ex.toString();
        }
        return result;
    }

    /**
     * 转换编码 GB2312到ISO-8859-1
     */
    public static String gb2iso(String text) {
        String result = "";
        try {
            result = new String(text.getBytes("GB2312"), "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Utf8URL编码
     */
    public static String utf8urlencode(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);
            if (c <= 255) {
                result.append(c);
            } else {

                byte[] b = new byte[0];
                try {
                    b = Character.toString(c).getBytes("UTF-8");
                } catch (Exception ignored) {
                }

                for (byte aB : b) {
                    int k = aB;
                    if (k < 0) {
                        k += 256;
                    }
                    result.append("%").append(Integer.toHexString(k).toUpperCase());
                }

            }
        }
        return result.toString();
    }

    /**
     * Utf8URL解码
     */
    public static String utf8urldecode(String text) {
        String result = "";
        int p;
        if (text != null && text.length() > 0) {
            text = text.toLowerCase();
            p = text.indexOf("%e");
            if (p == -1) {
                return text;
            }
            while (p != -1) {
                result += text.substring(0, p);
                text = text.substring(p, text.length());
                if (Objects.equals(text, "") || text.length() < 9) {
                    return result;
                }
                result += codetoword(text.substring(0, 9));
                text = text.substring(9, text.length());
                p = text.indexOf("%e");
            }
        }
        return result + text;
    }

    /**
     * utf8URL编码转字符
     */
    private static String codetoword(String text) {
        String result;
        if (utf8codecheck(text)) {
            byte[] code = new byte[3];
            code[0] = (byte) (Integer.parseInt(text.substring(1, 3), 16) - 256);
            code[1] = (byte) (Integer.parseInt(text.substring(4, 6), 16) - 256);
            code[2] = (byte) (Integer.parseInt(text.substring(7, 9), 16) - 256);
            try {
                result = new String(code, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                result = null;
            }
        } else {
            result = text;
        }
        return result;
    }

    /**
     * 编码是否有效
     */
    private static boolean utf8codecheck(String text) {
        String sign = "";
        String prefix = "%e";
        if (text.startsWith(prefix)) {
            for (int p = 0; p != -1; ) {
                p = text.indexOf("%", p);
                if (p != -1) {
                    p++;
                }
                sign += p;
            }
        }
        return "147-1".equals(sign);
    }

    /**
     * 判断是否Utf8Url编码
     */
    public static boolean isUtf8Url(String text) {
        text = text.toLowerCase();
        int p = text.indexOf("%");
        int nine = 9;
        if (p != -1 && text.length() - p > nine) {
            text = text.substring(p, p + nine);
        }
        return utf8codecheck(text);
    }

    /**
     * 进行字符规格化（全角转半角，大写转小写处理）
     *
     * @return char
     */
    public static char regularize(char input) {
        if (input == 12288) {
            input = (char) 32;
        } else if (input > 65280 && input < 65375) {
            input = (char) (input - 65248);
        } else {
            char a = 'A';
            char z = 'Z';
            if (input >= a && input <= z) {
                input += 32;
            }
        }
        return input;
    }

}
