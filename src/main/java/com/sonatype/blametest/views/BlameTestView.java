package com.sonatype.blametest.views;

import io.dropwizard.views.View;
import lombok.Data;

@Data
public class BlameTestView extends View {
  private String url;
  public BlameTestView() {
    super("blametest.ftl");
  }
}
