package com.sonatype.blametest.util;

import com.sonatype.blametest.models.GithubDiffDescriptor;

import org.junit.Test;

import static com.sonatype.blametest.util.GithubUtil.parseDiffDescriptor;
import static org.junit.Assert.assertEquals;

public class GithubUtilTest {

  @Test
  public void testParseDiffDescriptor() {

    assertEquals(
        parseDiffDescriptor("https://github.com/wconrad/ftpd/commit/828064f1a0ab69b2642c59cab8292a67bb44182c#diff-727907d957f457b955115a20e6cda186R222"),
        GithubDiffDescriptor.builder()
            .owner("wconrad")
            .repoName("ftpd")
            .commitHash("828064f1a0ab69b2642c59cab8292a67bb44182c")
            .filenameHash("727907d957f457b955115a20e6cda186")
            .lineNumber(222)
            .build()
    );

  }
}
