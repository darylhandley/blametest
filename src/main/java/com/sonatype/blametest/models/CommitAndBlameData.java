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

  /* find the specific blame range for the change based on the line number */
  public BlameRange getSpecificBlameRange() {

    int line = githubDiffDescriptor.getLineNumber();
    for (BlameRange blameRange: blameRanges) {
      if (blameRange.getStartingLine() <= line && blameRange.getEndingLine() >= line) {
        return blameRange;
      }
    }

    // this shouldn't happen since we should always find something
    throw new RuntimeException("Unable to find the blameRange");

  }
}
