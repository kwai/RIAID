package com.kuaishou.riaid.adbrowser.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;

/**
 * 字符串相关的判断和转换
 */
public class ADBrowserStringHelper {
  /**
   * 判断字符串是否为整数
   */
  public static boolean isInteger(@Nullable String input) {
    if (input == null) {
      return false;
    }
    Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
    return mer.find();
  }
}
