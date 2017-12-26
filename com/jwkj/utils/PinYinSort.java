package com.jwkj.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinYinSort {
    private HashList<String, String> hashList = new HashList(new C11301());

    class C11301 implements KeySort<String, String> {
        C11301() {
        }

        public String getKey(String value) {
            return PinYinSort.this.getFirstChar(value);
        }
    }

    public String getFirstChar(String value) {
        String first;
        char firstChar = value.charAt(0);
        String[] print = PinyinHelper.toHanyuPinyinStringArray(firstChar);
        if (print == null) {
            if (firstChar >= 'a' && firstChar <= 'z') {
                firstChar = (char) (firstChar - 32);
            }
            if (firstChar < 'A' || firstChar > 'Z') {
                first = "#";
            } else {
                first = String.valueOf(firstChar);
            }
        } else {
            first = String.valueOf((char) (print[0].charAt(0) - 32));
        }
        if (first == null) {
            return "?";
        }
        return first;
    }

    public HashList<String, String> getHashList() {
        return this.hashList;
    }
}
