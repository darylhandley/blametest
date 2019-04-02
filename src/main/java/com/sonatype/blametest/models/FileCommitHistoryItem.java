package com.sonatype.blametest.models;

import com.sonatype.blametest.models.githubgraphql.Commit;

import lombok.Builder;
import lombok.Data;

/**
 * All the data we need to know for a single commit for a single file
 */
@Data
@Builder
public class FileCommitHistoryItem
{

  private Commit commit;
  private CommitFile commitFile;
  private String rawFileContents;
}
