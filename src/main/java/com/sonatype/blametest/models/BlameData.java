package com.sonatype.blametest.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BlameData {
  private String filename;
  private GithubDiffDescriptor githubDiffDescriptor;
}
