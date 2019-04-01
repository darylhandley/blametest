package com.sonatype.blametest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sonatype.blametest.models.BlameRange;
import com.sonatype.blametest.models.CommitAndBlameData;
import com.sonatype.blametest.models.CommitFile;
import com.sonatype.blametest.models.GithubDiffDescriptor;
import com.sonatype.blametest.models.GraphQLRequest;
import com.sonatype.blametest.util.GithubUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.val;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class GithubBlamePuller {

  private static ObjectMapper objectMapper = new ObjectMapper();


  static {
    Unirest.setObjectMapper(new JacksonObjectMapper());
  }

  public List<CommitFile> getFilesChangedForHash(String owner, String repo, String commitHash) throws Exception {

    String url = format("https://api.github.com/repos/%s/%s/commits/%s",
        owner,
        repo,
        commitHash
    );

    HttpResponse<String> result = Unirest.get(url).asString();

    String json = result.getBody();

    // result.getBody().getArray("files").
    List<CommitFile> files = new ArrayList<CommitFile>();

    com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(json);
    for (com.fasterxml.jackson.databind.JsonNode fileNode : rootNode.get("files")) {
      files.add(
          CommitFile.builder()
              .filename(fileNode.get("filename").textValue())
              .patch(fileNode.get("patch").textValue())
              .rawUrl(fileNode.get("raw_url").textValue())
              .status(fileNode.get("status").textValue())
              .additions(fileNode.get("additions").intValue())
              .deletions(fileNode.get("deletions").intValue())
              .changes(fileNode.get("changes").intValue())
              .build()
      );

    }

    return files;

  }

  public CommitAndBlameData getCommitAndBlameData(String diffUrl) throws Exception {

    GithubDiffDescriptor githubDiffDescriptor = GithubUtil.parseDiffDescriptor(diffUrl);
    List<CommitFile> files = getFilesChangedForHash(
        githubDiffDescriptor.getOwner(),
        githubDiffDescriptor.getRepoName(),
        githubDiffDescriptor.getCommitHash()
    );

    // iterate over the files and find the first one that matches on hash
    CommitFile commitFile = files.stream()
        .filter(file -> md5(file.getFilename()).equals(githubDiffDescriptor.getFilenameHash()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("unable to find file for hash"));

    // get the blame data
    List<BlameRange> blameRanges = getBlameRanges(
        githubDiffDescriptor.getOwner(),
        githubDiffDescriptor.getRepoName(),
        githubDiffDescriptor.getCommitHash(),
        commitFile.getFilename()
    );



    return CommitAndBlameData.builder()
        .commitFile(commitFile)
        .githubDiffDescriptor(githubDiffDescriptor)
        .blameRanges(blameRanges)
        .build();

  }

  public List<BlameRange> getBlameRanges(String owner, String repo, String commit, String filename) throws IOException {

    String query = loadFile("commitAndBlame.gql")
        .replaceAll("__owner__", owner)
        .replaceAll("__repoName__", repo)
        .replaceAll("__commitHash__", commit)
        .replaceAll("__filename__", filename)
        .replaceAll("", "");

    HttpResponse<String> result = executeGraphQlQuery(query);
    System.out.println(result.getBody());

    List<BlameRange> blameRanges = new ArrayList<>();

    JsonNode jsonNode = objectMapper.readTree(result.getBody().toString());
    JsonNode rangesNode = jsonNode
        .get("data")
        .get("repository")
        .get("object")
        .get("blame")
        .get("ranges");

    for (JsonNode rangeNode : rangesNode) {

      val blameRange = BlameRange.builder()
          .age(rangeNode.get("age").intValue())
          .endingLine(rangeNode.get("endingLine").intValue())
          .startingLine(rangeNode.get("startingLine").intValue())
          .commitHash(rangeNode.get("commit").get("oid").asText())
          .committedDate(rangeNode.get("commit").get("committedDate").asText())
          .committer(rangeNode.get("commit").get("committer").get("user").get("name").asText())
          .changedFilesCount(rangeNode.get("commit").get("changedFiles").intValue())
          .build();

      blameRanges.add(blameRange);

    }
    return blameRanges;

  }

  public List<CommitAndBlameData> getLineHistory(String diffUrl) throws Exception {

    // get the first BlameData
    CommitAndBlameData firstBlameData = getCommitAndBlameData(diffUrl);

    // look up the next one using blame



    return ImmutableList.of(firstBlameData);

  }

  // public


  private HttpResponse<String> executeGraphQlQuery(String query)  {
    GraphQLRequest graphQLRequest = new GraphQLRequest();
    graphQLRequest.setQuery(query);

    try {
      HttpResponse<String> response = Unirest.post("https://api.github.com/graphql")
          .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
          .body(graphQLRequest)
          .asString();

      if (response.getStatus() != 200) {
        throw new RuntimeException("Unexpected response code " + response.getStatus());
      }

      return response;

    } catch (UnirestException excp) {
      throw new RuntimeException(excp);
    }

  }

  private String md5(String input) {
    String hash = Hashing.md5().hashString(input, Charsets.UTF_8).toString();
    return hash;
  }

  //private HttpResponse<JsonNode> getAsJson(String url) throws Exception {
  //
  //  return Unirest.get(url)
  //      // .header("Authorization","Bearer " + Constants.AUTH_TOKEN)
  //      // .body(graphQLRequest)
  //      .asJson()
  //      .getBody();
  //
  //}

  private String loadFile(String filename) {
    URL url = Resources.getResource("graphqlqueries/" + filename);
    try {
      return Resources.toString(url, Charsets.UTF_8);
    } catch (IOException ioExcp) {
      throw new RuntimeException(ioExcp);
    }

  }

}
