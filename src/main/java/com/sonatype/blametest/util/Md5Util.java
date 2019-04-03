package com.sonatype.blametest.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public class Md5Util
{


  public static String md5(String input) {
    String hash = Hashing.md5().hashString(input, Charsets.UTF_8).toString();
    return hash;
  }
}
