package com.sonatype.blametest.views;

import io.dropwizard.views.View;

public class BlameTestView extends View {
  public BlameTestView() {
    super("blametest.ftl");
  }
}
