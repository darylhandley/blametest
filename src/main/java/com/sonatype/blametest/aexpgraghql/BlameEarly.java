package com.sonatype.blametest.aexpgraghql;

import java.awt.image.SampleModel;
import java.math.BigDecimal;

import io.aexp.nodes.graphql.Argument;
import io.aexp.nodes.graphql.Arguments;
import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;
import io.aexp.nodes.graphql.Variable;

public class BlameEarly {
  public static void main(String[] args) throws Exception{
    GraphQLTemplate graphQLTemplate = new GraphQLTemplate();

    GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
        .url("http://graphql.example.com/graphql")
        .variables(new Variable("timeFormat", "MM/dd/yyyy"))
        .arguments(new Arguments("path.to.argument.property",
            new Argument("id", "d070633a9f9")))
        .scalars(BigDecimal.class)
        .request(SampleModel.class)
        .build();
    GraphQLResponseEntity<SampleModel> responseEntity = graphQLTemplate.query(requestEntity, SampleModel.class);
  }
}
