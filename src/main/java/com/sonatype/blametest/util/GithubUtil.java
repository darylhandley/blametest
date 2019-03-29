package com.sonatype.blametest.util;

import com.sonatype.blametest.models.GithubDiffDescriptor;

public class GithubUtil
{

  public static GithubDiffDescriptor parseDiffDescriptor(String diffUrl) {
    // "https://github.com/wconrad/ftpd/commit/828064f1a0ab69b2642c59cab8292a67bb44182c#diff-727907d957f457b955115a20e6cda186R222"
    if (!diffUrl.startsWith("https://github.com")) {
      throw new RuntimeException("Invalid git url");
    }

    diffUrl = diffUrl.replaceAll("https://github.com/", "");

    String [] splits = diffUrl.split("/");
    String owner = splits[0];
    String repoName = splits[1];
    String hashFileAndLineNum = splits[3];
    String commitHash = hashFileAndLineNum.substring(0, 40);
    String filenameHash = hashFileAndLineNum.substring(46, 46 + 32);
    String linenumberStr = hashFileAndLineNum.substring(
        "828064f1a0ab69b2642c59cab8292a67bb44182c#diff-727907d957f457b955115a20e6cda186R".length(),
        hashFileAndLineNum.length()
    );

    return GithubDiffDescriptor.builder()
        .owner(owner)
        .repoName(repoName)
        .commitHash(commitHash)
        .filenameHash(filenameHash)
        .lineNumber(Integer.parseInt(linenumberStr))
        .build();

  }
}
