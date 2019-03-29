package com.sonatype.blametest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sonatype.blametest.models.BlameData;
import com.sonatype.blametest.models.GithubDiffDescriptor;
import com.sonatype.blametest.models.GraphQLRequest;
import com.sonatype.blametest.util.GithubUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import static java.lang.String.format;

public class GithubBlamePuller {

  private ObjectMapper objectMapper = new ObjectMapper();

  private Gson gson = new Gson();

  static {

  }

  public List<String> getFilesChangedForHash(String owner, String repo, String commitHash) throws Exception {

    String url = format("https://api.github.com/repos/%s/%s/commits/%s",
        owner,
        repo,
        commitHash
    );

    HttpResponse<JsonNode> result = Unirest.get(url).asJson();

    String json = result.getBody().toString();

    // result.getBody().getArray("files").
    List<String> files = new ArrayList<String>();

    JsonObject body = gson.fromJson(json, JsonObject.class);
    for (JsonElement element : body.get("files").getAsJsonArray()) {
      files.add(element.getAsJsonObject().get("filename").getAsString());

    }

    return files;

  }

  public BlameData getBlameData(String diffUrl) throws Exception {

    GithubDiffDescriptor githubDiffDescriptor = GithubUtil.parseDiffDescriptor(diffUrl);
    List<String> files = getFilesChangedForHash(
        githubDiffDescriptor.getOwner(),
        githubDiffDescriptor.getRepoName(),
        githubDiffDescriptor.getCommitHash()
    );

    // iterate over the files and find the first one that matches on hash
    String filename = files.stream()
        .filter(file -> {
          return md5(file).equals(githubDiffDescriptor.getFilenameHash());
        })
        .findFirst()
        .orElseThrow(() -> new RuntimeException("unable to find file for hash"));


    return BlameData.builder()
        .filename(filename)
        .githubDiffDescriptor(githubDiffDescriptor)
        .build();

  }


  private HttpResponse<JsonNode> executeGraphQlQuery(String query) throws Exception {
    GraphQLRequest graphQLRequest = new GraphQLRequest();
    graphQLRequest.setQuery(query);

    return Unirest.post("https://api.github.com/graphql")
        .header("Authorization","Bearer " + Constants.AUTH_TOKEN)
        .body(graphQLRequest)
        .asJson();

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
