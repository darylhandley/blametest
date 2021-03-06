package com.sonatype.blametest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sonatype.blametest.models.CommitFile;
import com.sonatype.blametest.models.FileCommitHistoryItem;
import com.sonatype.blametest.models.GraphQLRequest;
import com.sonatype.blametest.models.githubgraphql.Commit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.SneakyThrows;

public class GithubService
{

  private static ObjectMapper objectMapper = new ObjectMapper();

  private GithubBlamePuller gbp = new GithubBlamePuller();


  static {
    Unirest.setObjectMapper(new JacksonObjectMapper());
  }

  @SneakyThrows
  public List<FileCommitHistoryItem> getFileCommitHistory(String owner, String repo, String filename, String startingHash)  {
    List<Commit> commits = getCommitsForFile(owner, repo, filename, startingHash);

    List<FileCommitHistoryItem> fileCommitHistory = commits.stream().map(commit-> {
      CommitFile commitFile = getCommitFileForCommit(owner, repo, filename, commit.getOid());
      String contents = getRawFile(commitFile.getRawUrl());
      return FileCommitHistoryItem.builder()
          .commit(commit)
          .commitFile(commitFile)
          .rawFileContents(contents)
          .build();
    }).collect(Collectors.toList());

    return fileCommitHistory;


  }

  public List<Commit> getCommitsForFile(String owner, String repo, String filename, String startingHash)
    throws Exception {

    String query = loadFile("fileCommitHistory.gql")
        .replaceAll("__owner__", owner)
        .replaceAll("__repoName__", repo)
        .replaceAll("__filename__", filename)
        .replaceAll("__commitHash__", startingHash)
        .replaceAll("", "");

    HttpResponse<String> result = executeGraphQlQuery(query);
    System.out.println(result.getBody());


    JsonNode rootNode = objectMapper.readTree(result.getBody());

    JsonNode commitNodes = rootNode.get("data").get("repository").get("object").get("history").get("nodes");

    List<Commit> commits = new ArrayList<>();
    for (JsonNode commitNode: commitNodes) {
      Commit commit = objectMapper.readValue(commitNode.toString(), Commit.class);
      System.out.println(commit);
      commits.add(commit);
    }

    return commits;

  }

  @SneakyThrows
  public CommitFile getCommitFileForCommit(String owner, String repo, String filename, String hash) {
    List<CommitFile> commitFiles = gbp.getFilesChangedForHash(owner, repo, hash);
    Optional<CommitFile> commitFile = commitFiles
        .stream()
        .filter(cf -> cf.getFilename().equals(filename))
        .findFirst();

    return commitFile.orElseThrow(() -> new RuntimeException("Commit file not found"));

  }

  @SneakyThrows
  public String getRawFile(String rawUrl)  {

    HttpResponse<String> response = Unirest.get(rawUrl).asString();
    if (response.getStatus() != 200) {
      throw new RuntimeException("Unexpected response code " + response.getStatus());
    }
    return response.getBody();

  }



  private HttpResponse<String> executeGraphQlQuery(String query)  {
    GraphQLRequest graphQLRequest = new GraphQLRequest();
    graphQLRequest.setQuery(query);

    try {
      HttpResponse<String> response = Unirest.post("https://api.github.com/graphql")
          .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
          .body(graphQLRequest)
          .asString();

      if (response.getStatus() != 200) {
        throw new RuntimeException("Unexpected response code " + response.getStatus() + " content is " + response.getBody());
      }

      return response;

    } catch (UnirestException excp) {
      throw new RuntimeException(excp);
    }

  }


  private String loadFile(String filename) {
    URL url = Resources.getResource("graphqlqueries/" + filename);
    try {
      return Resources.toString(url, Charsets.UTF_8);
    } catch (IOException ioExcp) {
      throw new RuntimeException(ioExcp);
    }

  }

}
