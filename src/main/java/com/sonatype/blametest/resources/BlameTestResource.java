package com.sonatype.blametest.resources;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sonatype.blametest.BlamePuller2;
import com.sonatype.blametest.GithubBlamePuller;
import com.sonatype.blametest.models.FileCommitHistoryItem;
import com.sonatype.blametest.models.GithubDiffDescriptor;
import com.sonatype.blametest.models.githubgraphql.Blame;
import com.sonatype.blametest.util.GithubUtil;
import com.sonatype.blametest.views.BlameTestView;

@Path("/blame")
@Produces(MediaType.TEXT_HTML)
public class BlameTestResource {

  private GithubBlamePuller githubBlamePuller = new GithubBlamePuller();

  @GET
  public BlameTestView getBlameTest(@QueryParam("url") Optional<String> url ) {
    BlameTestView view = new BlameTestView();

    if (url.isPresent()) {

      GithubDiffDescriptor githubDiffDescriptor = GithubUtil.parseDiffDescriptor(url.get());
      String filename = githubBlamePuller.getFilenameFromDiffUrl(url.get());

      BlamePuller2 blamePuller2 = new BlamePuller2(
          githubDiffDescriptor.getOwner(),
          githubDiffDescriptor.getRepoName(),
          filename,
          githubDiffDescriptor.getCommitHash()
      );

      List<FileCommitHistoryItem> fileCommitHistory = blamePuller2.loadFileCommitHistory();
      view.setFileCommitHistory(fileCommitHistory);

    }


    view.setUrl(url.orElse(""));
    return view;
  }
}
