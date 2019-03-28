package com.sonatype.blametest;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class GraphQLTest {

    @BeforeClass
    public static void init() {
        Unirest.setObjectMapper(new JacksonObjectMapper());
    }


    @Test
    public void loadschema() throws Exception {
        String query = loadFile("loadschema.gql");
        HttpResponse<JsonNode> jsonResponse = executeQuery(query);

        System.out.println(jsonResponse.getBody());

    }

    private HttpResponse<JsonNode> executeQuery(String query) throws Exception {
        GraphQLRequest graphQLRequest = new GraphQLRequest();
        graphQLRequest.setQuery(query);

        return Unirest.post("https://api.github.com/graphql")
            .header("Authorization","Bearer " + Constants.AUTH_TOKEN)
            .body(graphQLRequest)
            .asJson();

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
