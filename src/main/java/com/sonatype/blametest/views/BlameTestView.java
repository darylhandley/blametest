package com.sonatype.blametest.views;

import java.util.List;

import com.sonatype.blametest.models.FileCommitHistoryItem;

import io.dropwizard.views.View;
import lombok.Data;

@Data
public class BlameTestView extends View {
  private String url;
  private List<FileCommitHistoryItem> fileCommitHistory;
  public BlameTestView() {
    super("blametest.ftl");
  }
}
