package com.sonatype.blametest.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitFile
{

  private String filename;
  private String status;
  private int additions;
  private int deletions;
  private int changes;
  private String patch;
  private String rawUrl;
}
