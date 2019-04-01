package com.sonatype.blametest.models;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommitAndBlameData
{
  private CommitFile commitFile;
  private GithubDiffDescriptor githubDiffDescriptor;
  private List<BlameRange> blameRanges;
}
