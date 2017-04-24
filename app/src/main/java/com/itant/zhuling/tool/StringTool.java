package com.itant.zhuling.tool;

import java.net.URLDecoder;

/**
 * Created by iTant on 2016/11/16.
 */
public class StringTool {

    public static String getBASE64(String s) {
        if (s == null) {
            return null;
        }

        return null;
    }

    /**
     * 获取下载地址
     * @param raw
     * @return
     */
    public static String getXiaMp3Url(String raw) {
        String url = "";
        try {
            int num = Integer.parseInt(raw.substring(0, 1));
            String str = raw.substring(1);
            int num2 = str.length() % num;
            int length = (int) Math.ceil(((double) str.length()) / ((double) num));
            String[] strArray = new String[num];

            int startIndex = 0;
            for (int i = 0; i < num; i++) {
                if (i < num2) {
                    strArray[i] = str.substring(startIndex, startIndex+length);
                    startIndex = startIndex+length;
                } else if (num2 == 0) {
                    strArray[i] = str.substring(startIndex, startIndex+length);
                    startIndex = startIndex+length;
                } else {
                    strArray[i] = str.substring(startIndex, startIndex+length-1);
                    startIndex = startIndex+length-1;
                }
            }

            StringBuilder builder = new StringBuilder();
            if (num2 == 0) {
                for (int j = 0; j < length; j++) {
                    for (int k = 0; k < num; k++) {
                        builder.append(strArray[k].substring(j, j+1));
                    }
                }
            } else {
                for (int m = 0; m < length; m++) {
                    if (m == (length - 1)) {
                        for (int n = 0; n < num2; n++) {
                            builder.append(strArray[n].substring(m, m+1));
                        }
                    } else {
                        for (int num10 = 0; num10 < num; num10++) {
                            builder.append(strArray[num10].substring(m, m+1));
                        }
                    }
                }
            }

            String input = URLDecoder.decode(builder.toString());
            if (input != null) {
                String one = input.replaceAll("\\^", "0");
                String two = one.replaceAll("\\+", " ");
                return two.replaceAll("\\.mp$", ".mp3");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
