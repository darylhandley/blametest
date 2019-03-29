package com.sonatype.blametest.aexpgraghql;

import com.sonatype.blametest.aexpgraghql.RepositoryConnection;

import io.aexp.nodes.graphql.annotations.GraphQLVariable;

// @GraphQLProperty(name = "user", arguments = {new Argument("login", "dhandley")})
public class User {

  String name = null;
  String location = null;

  @GraphQLVariable(name = "isFork", scalar = "Boolean!")
  RepositoryConnection repositories = null;

  @Override
  public String toString() {
    return "User{" +
        "name='" + name + '\'' +
        ", location='" + location + '\'' +
        ", repositories=" + repositories +
        '}';
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(final String location) {
    this.location = location;
  }

  public RepositoryConnection getRepositories() {
    return repositories;
  }

  public void setRepositories(final RepositoryConnection repositories) {
    this.repositories = repositories;
  }
}
