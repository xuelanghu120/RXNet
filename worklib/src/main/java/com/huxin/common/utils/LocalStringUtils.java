package com.huxin.common.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalStringUtils {

    /**
     * 将字符串转成URI
     *
     * @param str
     * @return
     */
    public static Uri toUriByStr(String str) {
        if (LocalStringUtils.isEmpty(str)) {
            return null;
        }
        Uri uri = null;
        if (str.indexOf("/storage") > -1) {
            uri = Uri.fromFile(new File(str.substring(str.indexOf("/storage"))));
        } else if (str.indexOf("/sd") > -1) {
            uri = Uri.fromFile(new File(str.substring(str.indexOf("/sd"))));
        } else {
            uri = Uri.parse(str);
        }
        return uri;
    }

    public static String subStringByMaxByteCount(String s, int maxByteCount) {
        if (TextUtils.isEmpty(s))
            return s;
        int count = 0;
        StringBuilder buidler = new StringBuilder();
        int i = 0;
        for (; i < s.length(); i++) {
            if (count < maxByteCount) {
                int ascii = Character.codePointAt(s, i);
                if (ascii >= 0 && ascii <= 255) {
                    count++;
                } else {
                    if ((maxByteCount - count) == 1) {
                        i--;
                        break;
                    } else {
                        count += 2;
                    }
                }
            } else {
                break;
            }
        }

        return buidler.append(s.subSequence(0, i)).toString();
    }

    public static String formatVoiceTime(long audioTime) {
        // 分钟
        long minute = audioTime / 60;
        // 秒
        long second = audioTime % 60;
        // 设置录制时间
        return (minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute)) + ":" + (second < 10 ? "0" + String.valueOf(second) : String.valueOf(second));
    }

    public static String uTF8GetString(byte[] bytearr) {
        return uTF8GetString(bytearr, 0, bytearr.length);
    }

    public static String uTF8GetString(byte[] bytearr, int start, int utflen) {
        char str[] = new char[utflen];
        int c, char2, char3;
        int count = start;
        int strlen = 0;

        while (count < start + utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx */
                    count++;
                    str[strlen++] = (char) c;
                    break;
                case 12: // C 1100
                case 13: // D 1101

					/* 110x xxxx 10xx xxxx */
                    count += 2;
                    if (count > start + utflen) {
                        return new String(bytearr, start, utflen);
                    }
                    char2 = (int) bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80) {
                        return new String(bytearr, start, utflen);
                    }
                    str[strlen++] = (char) (((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14: // E 1110

					/* 1110 xxxx 10xx xxxx 10xx xxxx */
                    count += 3;
                    if (count > start + utflen) {
                        return new String(bytearr, start, utflen);
                    }
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
                        return new String(bytearr, start, utflen);
                    }
                    str[strlen++] = (char) (((c & 0x0F) << 12) | ((char2 & 0x3F) << 6) | ((char3 & 0x3F) << 0));
                    break;
                default:

					/* 10xx xxxx, 1111 xxxx */
                    return new String(bytearr, start, utflen);
            }
        }
        return new String(str, 0, strlen);
    }

    /**
     * @param instring String
     * @return byte[]
     */
    public static byte[] UTF8GetBytes(String instring) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeUTF(instring);
            byte[] jdata = bos.toByteArray();
            bos.close();
            dos.close();
            byte[] buff = new byte[jdata.length - 2];
            System.arraycopy(jdata, 2, buff, 0, buff.length);
            return buff;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获得当前Bitmap数组
     *
     * @param img
     * @return
     */
    // Compress image and then return with byte array
    public static byte[] bitmapToByteArray(Bitmap img) {
        int size = img.getWidth() * img.getHeight() * 4;
        byte[] data;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            img.compress(Bitmap.CompressFormat.JPEG, 100, out);
            data = out.toByteArray();
            out.flush();
            out.close();
        } catch (IOException e) {
            return null;
        }
        return data;
    }

    public static String toHex(byte[] data) {
        StringBuffer strBuffer = new StringBuffer();
        String hexStr = "";

        for (int i = 0; i < data.length; i++) {
            strBuffer.append(byteToHex(data[i]));
        }

        if (strBuffer.length() > 0) {
            hexStr = strBuffer.toString();
        }
        return hexStr;
    }

    public static String byteToHex(byte a) {
        int aaa = (a < 0) ? a + 256 : a;
        StringBuffer sb = new StringBuffer();
        switch (aaa / 16) {
            case 10:
                sb.append("A");
                break;
            case 11:
                sb.append("B");
                break;
            case 12:
                sb.append("C");
                break;
            case 13:
                sb.append("D");
                break;
            case 14:
                sb.append("E");
                break;
            case 15:
                sb.append("F");
                break;
            default:
                sb.append(aaa / 16);
                break;
        }
        switch (aaa % 16) {
            case 10:
                sb.append("A");
                break;
            case 11:
                sb.append("B");
                break;
            case 12:
                sb.append("C");
                break;
            case 13:
                sb.append("D");
                break;
            case 14:
                sb.append("E");
                break;
            case 15:
                sb.append("F");
                break;
            default:
                sb.append(aaa % 16);
                break;
        }
        return sb.toString();
    }

    public static byte[] join(byte[] arrayI, byte[] arrayII) {
        byte[] array = null;

        if (arrayI != null && arrayII != null) {
            array = new byte[arrayI.length + arrayII.length];

            System.arraycopy(arrayI, 0, array, 0, arrayI.length);
            System.arraycopy(arrayII, 0, array, arrayI.length, arrayII.length);
        }
        return array;
    }

    public static byte[] join(byte[] arrayI, byte[] arrayII, byte[] arrayIII) {
        byte[] array = null;

        if (arrayI != null && arrayII != null && arrayIII != null) {
            array = new byte[arrayI.length + arrayII.length + arrayIII.length];

            System.arraycopy(arrayI, 0, array, 0, arrayI.length);
            System.arraycopy(arrayII, 0, array, arrayI.length, arrayII.length);
            System.arraycopy(arrayIII, 0, array, arrayI.length + arrayII.length, arrayIII.length);
        }
        return array;
    }

    public static String byteArray2HEXString(byte[] data) {
        if (null == data || 0 == data.length)
            return "";

        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str);
    }

    public static int getGBKLength(String s) {
        // s.getBytes(Charset.forName("GBK"))
        int length = 0;
        try {
            length = s.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return length;
    }

    /**
     * 去掉字符串开头的标点
     *
     * @param str
     * @return 如果都是标点, 则返回""
     */
    public static String removePunctuate(String str) {
        if (str != null) {
            boolean isMatch = false;
            // Pattern p = Pattern.compile("[\\pP‘’“”]"); // 匹配任意符号
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                String s = String.valueOf(c);
                if (!s.matches("[,\\.\\?!，。？！]")) {// [(.|,|\"|\\?|!|:;')]
                    str = str.substring(i);
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) {
                str = "";
            }
        }
        return str;
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String toDBC(String input) {
        if (!TextUtils.isEmpty(input)) {
            char[] c = input.toCharArray();
            for (int i = 0; i < c.length; i++) {
                if (c[i] == 12288) {
                    c[i] = (char) 32;
                    continue;
                }
                if (c[i] > 65280 && c[i] < 65375)
                    c[i] = (char) (c[i] - 65248);
            }
            return new String(c);
        }
        return input;
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
