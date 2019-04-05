package com.sonatype.blametest;

public class Config
{

  public static String getGithubApiKey() {
    return System.getenv("GITHUB_API_KEY");
  }
}
