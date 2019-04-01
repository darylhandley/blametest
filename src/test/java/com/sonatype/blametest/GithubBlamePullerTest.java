package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.BlameRange;
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

  private final String MY_HISTORY_FIX_URL =
    "https://github.com/darylhandley/blametest/commit/c6b0687e966ea98b1c8bba50a4e5609d1b83834a#diff-ba21194fc596c80deea196e3d7f5104cR10";

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
        .patch("@@ -1,4 +1,4 @@\n-### dev\n+### 0.2.2\n \n Bug fixes\n \n@@ -8,6 +8,9 @@ Bug fixes\n   PASS\n * Open PASV mode data connection on same local IP as control connection.\n   This is required by RFC 1123.\n+* Disabled globbing in LIST (for now) due to code injection\n+  vulnerability.  This patch also disables globbing in NLST, but NLST\n+  probably shouldn't do globbing.\n \n Enhancements\n ")
        .build();
    assertEquals(expectedFirst, first);
    assertTrue(files.stream().anyMatch(f -> f.getFilename().equals("Changelog.md")));
    

  }

  @Test
  public void testGetBlameRanges() throws Exception {

    List<BlameRange> blameRanges = blamePuller.getBlameRanges(
        "darylhandley",
        "blametest",
        "c6b0687e966ea98b1c8bba50a4e5609d1b83834a",
        "data/myHistoryFIle.txt"
    );

    assertEquals(3, blameRanges.size());
    BlameRange first = blameRanges.get(0);
    assertEquals(new Integer(4), first.getAge());
    assertEquals(new Integer(9), first.getEndingLine());
    assertEquals(new Integer(1), first.getStartingLine());
    assertEquals(new Integer(3), first.getChangedFilesCount());
    assertEquals("2019-03-30T15:45:29Z", first.getCommittedDate());
    assertEquals("Daryl Handley", first.getCommitter());
    assertEquals("5674150d758ef1a11da6bee4608f75accc31255c", first.getCommitHash());


  }

  @Test
  public void testGetLineHistory() throws Exception {

    blamePuller.getLineHistory(FIX_URL);

  }

  @Test
  public void main() throws Exception {

    CommitAndBlameData blameData = blamePuller.getCommitAndBlameData(MY_HISTORY_FIX_URL);

    System.out.println("Executed a blame history");
    System.out.println("Owner        : "   + blameData.getGithubDiffDescriptor().getOwner());
    System.out.println("Repository   : "   + blameData.getGithubDiffDescriptor().getRepoName());
    System.out.println("Filename     : "   + blameData.getCommitFile().getFilename());
    System.out.println("LineNumber   : "   + blameData.getGithubDiffDescriptor().getLineNumber());
    System.out.println("File Changes : "   + blameData.getCommitFile().getChanges());
    System.out.println("Additions    : "   + blameData.getCommitFile().getAdditions());
    System.out.println("Deletions    : "   + blameData.getCommitFile().getDeletions());
    System.out.println("Patch        : \n" + blameData.getCommitFile().getPatch());


    System.out.println("");


  }


}
