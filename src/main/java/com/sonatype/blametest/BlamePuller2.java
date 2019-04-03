package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.FileCommitHistoryItem;
import com.sonatype.blametest.models.githubgraphql.Commit;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;

public class BlamePuller2 {

  private String repo;
  private String owner;
  private String filename;
  private String startingCommit;
  private Integer lineNumber;

  private GithubService githubService = new GithubService();


  public BlamePuller2(String owner, String repo, String filename, String startingCommit,
                      Integer lineNumber) {
    this.owner = owner;
    this.repo = repo;
    this.filename = filename;
    this.startingCommit = startingCommit;
    this.lineNumber = lineNumber;
  }


  @SneakyThrows
  public List<FileCommitHistoryItem> loadFileCommitHistory() {
    List<FileCommitHistoryItem> fileCommitHistory =
        githubService.getFileCommitHistory(owner, repo, filename, startingCommit);
    fileCommitHistory.forEach(fileCommitHistoryItem -> {
      fileCommitHistoryItem.setRepo(repo);
      fileCommitHistoryItem.setOwner(owner);
      fileCommitHistoryItem.setFilename(filename);
      fileCommitHistoryItem.setLineNumber(lineNumber);
      fileCommitHistoryItem.init();
    });

    return  fileCommitHistory;
  }


}
