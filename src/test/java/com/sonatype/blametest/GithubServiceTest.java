package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.CommitAndBlameData;
import com.sonatype.blametest.models.CommitFile;
import com.sonatype.blametest.models.githubgraphql.Author;
import com.sonatype.blametest.models.githubgraphql.Blame;
import com.sonatype.blametest.models.githubgraphql.BlameRange;
import com.sonatype.blametest.models.githubgraphql.Commit;
import com.sonatype.blametest.models.githubgraphql.User;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GithubServiceTest
{
  private final GithubService githubService = new GithubService();


  @Test
  public void testGetFileCommitHistory() throws Exception {

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


}
