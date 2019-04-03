package com.sonatype.blametest.resources;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sonatype.blametest.models.githubgraphql.Blame;
import com.sonatype.blametest.views.BlameTestView;

@Path("/blame")
@Produces(MediaType.TEXT_HTML)
public class BlameTestResource {

  @GET
  public BlameTestView getBlameTest(@QueryParam("url") Optional<String> url ) {
    BlameTestView view = new BlameTestView();
    view.setUrl(url.orElse(""));
    return view;
  }
}
