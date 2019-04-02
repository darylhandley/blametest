package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.BlameRange;
import com.sonatype.blametest.models.CommitFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileCommitHistory {

  private String commitHash;
  private List<BlameRange> blameRanges;
  private String fileContent;
  private Integer additions;
  private Integer deletions;
  private Integer changes;
  private String patch;
  private String rawUrl;


}
