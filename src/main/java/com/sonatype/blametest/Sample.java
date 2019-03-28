package com.sonatype.blametest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.sonatype.blametest.models.User;

import io.aexp.nodes.graphql.Argument;
import io.aexp.nodes.graphql.Arguments;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;
import io.aexp.nodes.graphql.Variable;

public class Sample {
  public static void main(String[] args) throws Exception {

    String YOUR_AUTH_TOKEN = "eeb7987aef9ccd7440a49cae2acc7f58bb415059";

    Map<String, String> headers = new HashMap();
    headers.put("Authorization", "bearer " + YOUR_AUTH_TOKEN);

    GraphQLTemplate template = new GraphQLTemplate();
    GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
        .url("https://api.github.com/graphql")
        .request(User.class)
        .headers(headers)
        .arguments(new Arguments("user", new Argument("login", "chemdrew")))
        .variables(new Variable("isFork", false))
        .scalars(BigDecimal.class, BigInteger.class)
        .build();
    GraphQLResponseEntity responseEnitity = template.query(requestEntity, User.class);

    System.out.println("request: " + requestEntity.getRequest());
    System.out.println("response: " + responseEnitity.getResponse());


  }
}
