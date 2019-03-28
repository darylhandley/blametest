package com.sonatype.blametest;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import jdk.nashorn.internal.ir.annotations.Ignore;

public class GraphlClient
{
  public static void main(String[] args) throws Exception {

    init();

    String query = loadFile("loadschema.gql");

    HttpResponse<JsonNode> jsonResponse = executeQuery(query);

    System.out.println(jsonResponse.getBody());

  }

  private static HttpResponse<JsonNode> executeQuery(String query) throws Exception {
    GraphQLRequest graphQLRequest = new GraphQLRequest();
    graphQLRequest.setQuery(query);

    return Unirest.post("https://api.github.com/graphql")
        .header("Authorization","Bearer " + Constants.AUTH_TOKEN)
        .body(graphQLRequest)
        .asJson();

  }

  private static String loadFile(String filename) {
    URL url = Resources.getResource("graphqlqueries/" + filename);
    try {
      return Resources.toString(url, Charsets.UTF_8);
    } catch (IOException ioExcp) {
      throw new RuntimeException(ioExcp);
    }

  }

  private static void init() {
    Unirest.setObjectMapper(new ObjectMapper() {
      com.fasterxml.jackson.databind.ObjectMapper mapper
          = new com.fasterxml.jackson.databind.ObjectMapper();

      public String writeValue(Object value) {
        try {
          return mapper.writeValueAsString(value);
        } catch (JsonProcessingException jsonExcp) {
          throw new RuntimeException(jsonExcp);
        }
      }

      @SuppressWarnings("unchecked")
      public <T> T readValue(String value, Class<T> valueType) {

        try {
          return mapper.readValue(value, valueType);
        } catch (IOException ioExcp) {
          throw new RuntimeException(ioExcp);
        }
      }
    });
  }
}
