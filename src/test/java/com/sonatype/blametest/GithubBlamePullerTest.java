package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.CommitAndBlameData;
import com.sonatype.blametest.models.CommitFile;

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

    List<CommitFile> files = blamePuller.getFilesChangedForHash("wconrad", "ftpd", "828064f1a0ab69b2642c59cab8292a67bb44182c");

    assertEquals(7, files.size());
    CommitFile first = files.get(0);
    CommitFile expectedFirst = CommitFile.builder().
        filename("Changelog.md")
        .status("modified")
        .additions(4)
        .deletions(1)
        .changes(5)
        .rawUrl("https://github.com/wconrad/ftpd/raw/828064f1a0ab69b2642c59cab8292a67bb44182c/Changelog.md")
        .patch("@@ -1,4 +1,4 @@\\n-### dev\\n+### 0.2.2\\n \\n Bug fixes\\n \\n@@ -8,6 +8,9 @@ Bug fixes\\n   PASS\\n * Open PASV mode data connection on same local IP as control connection.\\n   This is required by RFC 1123.\\n+* Disabled globbing in LIST (for now) due to code injection\\n+  vulnerability.  This patch also disables globbing in NLST, but NLST\\n+  probably shouldn't do globbing.\\n \\n Enhancements\\n ")
        .build();
    assertEquals(expectedFirst, first);
    assertTrue(files.stream().anyMatch(f -> f.getFilename().equals("Changelog.md")));
    assertTrue(files.contains("README.md"));

  }

  @Test
  public void testGetBlameData() throws Exception {

    CommitAndBlameData blameData = blamePuller.getCommitAndBlameData(FIX_URL);


    CommitFile expectedCommitFile = CommitFile.builder()
        .filename("lib/ftpd/disk_file_system.rb")
        .status("modified")
        .additions(4)
        .deletions(3)
        .changes(7)
        .rawUrl("https://github.com/wconrad/ftpd/raw/828064f1a0ab69b2642c59cab8292a67bb44182c/lib/ftpd/disk_file_system.rb")
        .patch("@@ -206,6 +206,8 @@ class DiskFileSystem\n \n     module Ls\n \n+      include Shellwords\n+\n       def ls(ftp_path, option)\n         path = expand_ftp_path(ftp_path)\n         dirname = File.dirname(path)\n@@ -214,11 +216,10 @@ def ls(ftp_path, option)\n           'ls',\n           option,\n           filename,\n-          '2>&1',\n-        ].compact.join(' ')\n+        ].compact\n         if File.exists?(dirname)\n           list = Dir.chdir(dirname) do\n-            `#{command}`\n+            `#{shelljoin(command)} 2>&1`\n           end\n         else\n           list = ''")
        .build();


    assertEquals(expectedCommitFile, blameData.getCommitFile());

    //CommitFile file = blameData.getCommitFile();
    //assertEquals(expectedCommitFile.getFilename(), file.getFilename());
    //assertEquals(expectedCommitFile.getStatus(), file.getStatus());
    //assertEquals(expectedCommitFile.getAdditions(), file.getAdditions());
    //assertEquals(expectedCommitFile.getDeletions(), file.getDeletions());
    //assertEquals(expectedCommitFile.getChanges(), file.getChanges());
    //assertEquals(expectedCommitFile.getRawUrl(), file.getRawUrl());
    //assertEquals(expectedCommitFile.getPatch(), file.getPatch());

  }

  @Test
  public void testCommandLine() throws Exception {

    CommitAndBlameData blameData = blamePuller.getCommitAndBlameData(FIX_URL);

    System.out.println("");


  }


}
