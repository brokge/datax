package com.alibaba.datax.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
/**
 * @author chenlw
 * @date 2024/6/4 14:50
 */
public class PinyinUtil {

     public static String toPinyin(String input, String caseType, String separate, String sorted) {
        if (input.matches(".*[\\u4E00-\\u9FA5].*")) {
            return getCompareNameByCN(input, caseType, separate, "1".equals(sorted));
        } else {
            return getCompareStrByEN(input, caseType, separate, "1".equals(sorted));
        }
    }


    private static String getCompareStrByEN(String input, String caseType, String separate, Boolean sorted) {
        String[] textArr = null;
        if (HanyuPinyinCaseType.UPPERCASE.getName().equals(caseType)) {
            textArr = input.toUpperCase().split("[^A-Za-z\\u4E00-\\u9FA5]");
        } else {
            textArr = input.toLowerCase().split("[^A-Za-z\\u4E00-\\u9FA5]");
        }
        if (sorted) {
            return Arrays.stream(textArr).sorted().collect(Collectors.joining(separate));
        } else {
            return String.join(separate, textArr);
        }
    }

    private static String getCompareNameByCN(String input, String caseType, String separate, Boolean sorted) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        if (HanyuPinyinCaseType.UPPERCASE.getName().equals(caseType)) {
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//设置为小写
        }
        //if (HanyuPinyinToneType.WITHOUT_TONE.getName().equals(toneType)) {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //}
        String result = "";
        try {
            if (sorted) {
                char[] charArr = input.toCharArray();
                List<String> list = new ArrayList<>(charArr.length);
                for (int i = 0; i < charArr.length; i++) {
                    //判断是否为汉字字符
                    if (isCNChar(charArr[i])) {//"[\\u4E00-\\u9FA5]+"
                        //同字多音取第一个
                        String[] pyArr = PinyinHelper.toHanyuPinyinStringArray(charArr[i], format);
                        if (pyArr != null && pyArr.length > 0) {
                            list.add(pyArr[0]);
                        }
                        // throw new IllegalArgumentException("拼音格式化转换异常=》"+charArr[i]+"找不到对应的拼音");
                    } else {
                        if (charArr[i] > 'a' && charArr[i] < 'z') {
                            list.add(String.valueOf((char) (charArr[i] - 32)));
                        } else if (charArr[i] > 'A' && charArr[i] < 'Z') {
                            list.add(String.valueOf(charArr[i]));
                        }
                    }
                }
                result = list.stream().sorted().collect(Collectors.joining(separate));
            } else {
                result = PinyinHelper.toHanYuPinyinString(input, format, separate, true);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            //e.printStackTrace();
            result = "Error";
        }
        return result;

    }

    private static boolean isCNChar(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;//[\u4E00-\u9FA5]
    }
}
