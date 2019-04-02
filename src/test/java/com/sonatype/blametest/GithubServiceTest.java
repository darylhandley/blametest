package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.CommitAndBlameData;
import com.sonatype.blametest.models.CommitFile;
import com.sonatype.blametest.models.FileCommitHistoryItem;
import com.sonatype.blametest.models.githubgraphql.Author;
import com.sonatype.blametest.models.githubgraphql.Blame;
import com.sonatype.blametest.models.githubgraphql.BlameRange;
import com.sonatype.blametest.models.githubgraphql.Commit;
import com.sonatype.blametest.models.githubgraphql.User;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GithubServiceTest
{
  private final GithubService githubService = new GithubService();


  @Test
  public void testGetCommitsForFile() throws Exception {

    List<Commit> commits = githubService.getCommitsForFile("darylhandley", "blametest", "data/myHistoryFIle.txt",
        "c6b0687e966ea98b1c8bba50a4e5609d1b83834a");

    assertEquals(4, commits.size());
    Commit first = commits.get(0);
    Commit expected = Commit.builder()
        .oid("c6b0687e966ea98b1c8bba50a4e5609d1b83834a")
        .changedFiles(1)
        .message("changes")
        .author(Author.builder()
            .email("dhandley@sonatype.com")
            .user(User.builder()
                .name("Daryl Handley")
                .login("darylhandley")
                .build())
            .build()
        )
        .blame(Blame.builder()
            .ranges(
                ImmutableList.of(
                    BlameRange.builder().age(5).endingLine(9).startingLine(1).build(),
                    BlameRange.builder().age(5).endingLine(10).startingLine(10).build(),
                    BlameRange.builder().age(5).endingLine(20).startingLine(11).build()
                )
            )
            .build()
        )
        .build();

    assertEquals(expected, first);

  }

  @Test
  public void testGetCommitFileForCommit() throws Exception {
    CommitFile commitFile = githubService.getCommitFileForCommit("darylhandley", "blametest", "data/myHistoryFIle.txt",
        "c6b0687e966ea98b1c8bba50a4e5609d1b83834a");

    assertNotNull(commitFile);
    assertEquals(1, commitFile.getAdditions());
    assertEquals(1, commitFile.getDeletions());
    assertEquals(2, commitFile.getChanges());
    assertEquals("data/myHistoryFIle.txt", commitFile.getFilename());
    assertEquals("https://github.com/darylhandley/blametest/raw/c6b0687e966ea98b1c8bba50a4e5609d1b83834a/data/myHistoryFIle.txt",
        commitFile.getRawUrl());
    assertEquals("@@ -7,7 +7,7 @@ line 6\n line 7\n line 8\n line 9\n-The quick brown fox jumps right over the lazy sheep.\n+The quick brown fox bounds right over the lazy sheep.\n line 11\n line 12\n line 13",
        commitFile.getPatch());
    assertEquals("modified", commitFile.getStatus());

    /*
    "sha": "fb26873c21ed94598c3dd5a91a43fea8b56d4051",
"filename": "data/myHistoryFIle.txt",
"status": "modified",
"additions": 1,
"deletions": 1,
"changes": 2,
"blob_url": "https://github.com/darylhandley/blametest/blob/c6b0687e966ea98b1c8bba50a4e5609d1b83834a/data/myHistoryFIle.txt",
"raw_url": "https://github.com/darylhandley/blametest/raw/c6b0687e966ea98b1c8bba50a4e5609d1b83834a/data/myHistoryFIle.txt",
"contents_url": "https://api.github.com/repos/darylhandley/blametest/contents/data/myHistoryFIle.txt?ref=c6b0687e966ea98b1c8bba50a4e5609d1b83834a",
"patch": "@@ -7,7 +7,7 @@ line 6\n line 7\n line 8\n line 9\n-The quick brown fox jumps right over the lazy sheep.\n+The quick brown fox bounds right over the lazy sheep.\n line 11\n line 12\n line 13"
}
]
     */
  }


  @Test
  public void testGetRawFile() throws Exception {
    String contents = githubService.getRawFile("https://github.com/darylhandley/blametest/raw/c6b0687e966ea98b1c8bba50a4e5609d1b83834a/data/myHistoryFIle.txt");

    assertNotNull(contents);
    assertEquals(
        "line 1\n" +
        "line 2\n" +
        "line 3\n" +
        "line 4\n" +
        "line 5\n" +
        "line 6\n" +
        "line 7\n" +
        "line 8\n" +
        "line 9\n" +
        "The quick brown fox bounds right over the lazy sheep.\n" +
        "line 11\n" +
        "line 12\n" +
        "line 13\n" +
        "line 14\n" +
        "line 15\n" +
        "line 16\n" +
        "line 17\n" +
        "line 18\n" +
        "line 19\n" +
        "line 20\n",
        contents);
  }

  @Test
  public void testGetFileCommitHistory() {
    List<FileCommitHistoryItem> fileCommitHistory = githubService.getFileCommitHistory(
        "darylhandley",
        "blametest",
        "data/myHistoryFIle.txt",
        "c6b0687e966ea98b1c8bba50a4e5609d1b83834a"
    );

    String expectedFileContents =
        "line 1\n" +
            "line 2\n" +
            "line 3\n" +
            "line 4\n" +
            "line 5\n" +
            "line 6\n" +
            "line 7\n" +
            "line 8\n" +
            "line 9\n" +
            "The quick brown fox bounds right over the lazy sheep.\n" +
            "line 11\n" +
            "line 12\n" +
            "line 13\n" +
            "line 14\n" +
            "line 15\n" +
            "line 16\n" +
            "line 17\n" +
            "line 18\n" +
            "line 19\n" +
            "line 20\n";

    assertEquals(4, fileCommitHistory.size());
    assertEquals(expectedFileContents, fileCommitHistory.get(0).getRawFileContents());


  }




}
