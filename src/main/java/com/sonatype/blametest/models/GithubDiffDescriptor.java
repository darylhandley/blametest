package com.sonatype.blametest.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GithubDiffDescriptor {

  private String owner;
  private String repoName;
  private String commitHash;
  private String filenameHash;
  private Integer lineNumber;
}
