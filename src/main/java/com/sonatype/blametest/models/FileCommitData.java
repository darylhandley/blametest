package com.sonatype.blametest.models;

import com.sonatype.blametest.models.githubgraphql.Commit;

/**
 * All the data we need to know for a single commit for a single file
 */
public class FileCommitData {

  private Commit commit;
  private CommitFile commitFile;
}
