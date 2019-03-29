package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.BlameData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GithubBlamePullerTest
{
  private final GithubBlamePuller blamePuller = new GithubBlamePuller();

  private final String FIX_URL =
      "https://github.com/wconrad/ftpd/commit/828064f1a0ab69b2642c59cab8292a67bb44182c#diff-727907d957f457b955115a20e6cda186R222";

  @Test
  public void testGetFilesChangedForHash() throws Exception {

    List<String> files = blamePuller.getFilesChangedForHash("wconrad", "ftpd", "828064f1a0ab69b2642c59cab8292a67bb44182c");

    assertEquals(7, files.size());
    assertTrue(files.contains("Changelog.md"));
    assertTrue(files.contains("README.md"));

  }

  @Test
  public void testGetBlameData() throws Exception {

    BlameData blameData = blamePuller.getBlameData(FIX_URL);

    assertEquals("lib/ftpd/disk_file_system.rb", blameData.getFilename());

  }


}
