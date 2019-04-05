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
import lombok.SneakyThrows;
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

    String authToken = Config.getGithubApiKey();
    HttpResponse<String> result = Unirest
        .get(url)
        .header("Authorization", "Bearer " + Constants.AUTH_TOKEN)
        .asString();

    if (result.getStatus() != 200) {
      throw new RuntimeException("Unexpected response code " + result.getStatus());
    }

    String json = result.getBody();

    // result.getBody().getArray("files").
    List<CommitFile> files = new ArrayList<CommitFile>();

    com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(json);

    for (com.fasterxml.jackson.databind.JsonNode fileNode : rootNode.get("files")) {
      files.add(
          CommitFile.builder()
              .filename(fileNode.get("filename").textValue())
              .patch(fileNode.get("patch") ==  null ? "" : fileNode.get("patch").textValue())
              .rawUrl(fileNode.get("raw_url").textValue())
              .status(fileNode.get("status").textValue())
              .additions(fileNode.get("additions").intValue())
              .deletions(fileNode.get("deletions").intValue())
              .changes(fileNode.get("changes").intValue())
              .build()
      );

    }

    //com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(json);
    //for (com.fasterxml.jackson.databind.JsonNode fileNode : rootNode.get("files")) {
    //  files.add(
    //      objectMapper.treeToValue(fileNode, CommitFile.class)
    //  );
    //}

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

    return getCommitAndBlameData(
        githubDiffDescriptor.getOwner(),
        githubDiffDescriptor.getRepoName(),
        githubDiffDescriptor.getCommitHash(),
        commitFile.getFilename(),
        githubDiffDescriptor.getLineNumber()
    );

  }

  @SneakyThrows
  public String getFilenameFromDiffUrl(String diffUrl)  {

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

    return commitFile.getFilename();


  }

  public CommitAndBlameData getCommitAndBlameData(String owner, String repo, String commit, String filename,
                                                  Integer lineNumber) throws Exception {

    List<CommitFile> files = getFilesChangedForHash(
        owner,
        repo,
        commit
    );

    // iterate over the files and find the first one that matches on filename
    CommitFile commitFile = files.stream()
        .filter(file -> file.getFilename().equals(filename))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("unable to find file for filename"));


    // get the blame data
    List<BlameRange> blameRanges = getBlameRanges(
        owner,
        repo,
        commit,
        filename
    );


    GithubDiffDescriptor githubDiffDescriptor = GithubDiffDescriptor.builder()
        .commitHash(commit)
        .filenameHash("None")
        .lineNumber(lineNumber)
        .owner(owner)
        .repoName(repo)
        .build();


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
    String owner = firstBlameData.getGithubDiffDescriptor().getOwner();
    String repo = firstBlameData.getGithubDiffDescriptor().getRepoName();
    String commit = firstBlameData.getGithubDiffDescriptor().getCommitHash();
    String filename = firstBlameData.getCommitFile().getFilename();
    Integer lineNumber = firstBlameData.getGithubDiffDescriptor().getLineNumber();
    BlameRange firstBlameRange = firstBlameData.getSpecificBlameRange();

    // get the previous commit for the line in question
    // how do we do this ?
    // get the commit history for the file to find the next commit

    // look up the next one using the blame and the next hash
    CommitAndBlameData currentBlameData = firstBlameData;
    while (true) {
      BlameRange blameRange = currentBlameData.getSpecificBlameRange();

      // if the same as the current then get next in history for the file
      if (currentBlameData.getGithubDiffDescriptor().getCommitHash().equals(blameRange.getCommitHash())) {
        // get previous commit history for file from this commit backwards

        // TODO: load the file later here and find the line number ?

        // load the currentBlameData
        currentBlameData = getCommitAndBlameData(owner, repo, "previousCommit", filename, lineNumber);
      }

      if (currentBlameData == null) {
        break;
      }
      currentBlameData = getCommitAndBlameData(owner, repo, blameRange.getCommitHash(), filename, lineNumber);



    }







    return ImmutableList.of(firstBlameData);

  }

  /**
   * Finds the previous commit for a file in the commit history.
   */
  //public List<String> getPreviousCommit(String owner, String repo, String commit) throws Exception {
  //
  //  String query = loadFile("previousCommit.gql")
  //      .replaceAll("__owner__", owner)
  //      .replaceAll("__repoName__", repo)
  //      .replaceAll("__commitHash__", commit)
  //      .replaceAll("", "");
  //
  //  HttpResponse<String> result = executeGraphQlQuery(query);
  //  System.out.println(result.getBody());
  //
  //  List<BlameRange> blameRanges = new ArrayList<>();
  //
  //  JsonNode jsonNode = objectMapper.readTree(result.getBody().toString());
  //  JsonNode rangesNode = jsonNode
  //      .get("data")
  //      .get("repository")
  //      .get("object")
  //      .get("blame")
  //      .get("ranges");
  //
  //}


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
