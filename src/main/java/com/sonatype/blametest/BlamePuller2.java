package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.githubgraphql.Commit;

import com.google.common.collect.ImmutableList;

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


  public List<FileCommitHistory> loadCommitHistory() throws Exception {
    // get all the commits for the file
    List<Commit> commits = githubService.getCommitsForFile(repo, owner, filename, startingCommit);


    // fill in the data from the v2 api

    return ImmutableList.of();
  }


}
