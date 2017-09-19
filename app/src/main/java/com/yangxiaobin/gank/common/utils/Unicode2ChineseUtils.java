package com.yangxiaobin.gank.common.utils;

/**
 * Created by handsomeyang on 2017/6/26.
 */

public class Unicode2ChineseUtils {
  /**
   * 传入汉字的Unicode编码字符串，返回相应的汉字字符串
   */
  public static String decode(String unicodeStr) {
    if (unicodeStr == null) {
      return null;
    }
    StringBuffer retBuf = new StringBuffer();
    int maxLoop = unicodeStr.length();
    for (int i = 0; i < maxLoop; i++) {
      if (unicodeStr.charAt(i) == '\\') {
        if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1)
            == 'U'))) {
          try {
            retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
            i += 5;
          } catch (NumberFormatException localNumberFormatException) {
            retBuf.append(unicodeStr.charAt(i));
          }
        } else {
          retBuf.append(unicodeStr.charAt(i));
        }
      } else {
        retBuf.append(unicodeStr.charAt(i));
      }
    }
    return retBuf.toString();
  }
}
