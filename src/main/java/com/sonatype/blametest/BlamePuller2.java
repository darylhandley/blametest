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

  private GithubService githubService = new GithubService();


  public BlamePuller2(String repo, String owner, String filename, String startingCommit) {
    this.repo = repo;
    this.owner = owner;
    this.filename = filename;
    this.startingCommit = startingCommit;
  }


  @SneakyThrows
  public List<FileCommitHistoryItem> loadFileCommitHistory() {
    return githubService.getFileCommitHistory(repo, owner, filename, startingCommit);
  }


}
