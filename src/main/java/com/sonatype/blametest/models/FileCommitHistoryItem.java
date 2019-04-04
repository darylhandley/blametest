package com.sonatype.blametest.models;

import java.util.Arrays;
import java.util.List;

import com.sonatype.blametest.models.githubgraphql.Commit;
import com.sonatype.blametest.util.Md5Util;

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
  private Integer lineNumber;
  private String repo;
  private String owner;
  private String filename;
  private String diffLink;
  private String theLine;

  // https://github.com/darylhandley/blametest/commit/c6b0687e966ea98b1c8bba50a4e5609d1b83834a#diff-ba21194fc596c80deea196e3d7f5104cR10
  // https://github.com/blametest/darylhandley/commit/c6b0687e966ea98b1c8bba50a4e5609d1b83834a#diff-ba21194fc596c80deea196e3d7f5104cR10
  public String getDiffLink() {
    String filehash = Md5Util.md5(commitFile.getFilename());
    String diffLink = String.format("https://github.com/%s/%s/commit/%s#diff-%sR%s",
        owner, repo, commit.getOid(), filehash, lineNumber.toString());
    return diffLink;
  }

  /** TODO: Super cheesy, should be able to just write a method but freemarker pukes */
  public void init() {
    //String filehash = Md5Util.md5(commitFile.getFilename());
    //diffLink = String.format("https://github.com/%s/%s/commit/%s#diff-%sR%s",
    //    owner, repo, commit.getOid(), filehash, lineNumber.toString());
  }

  public String getTheLine() {

    List<String> lines = Arrays.asList(rawFileContents.split("\\r?\\n"));
    String line = lines.get(lineNumber - 1); // 0 based
    return line == null ? "UNKNOWN LINE" : line;
  }

  public String getLinkToLine() {
    // https://github.com/Microsoft/ChakraCore/blob/1a7790f873b1a73d1cfec9548eb08a3b9fd798f3/lib/Parser/RegexParser.cpp#L2496
    String link  = String.format("https://github.com/%s/%s/blob/%s/%s#L%s",
        owner, repo, commit.getOid(), filename, lineNumber.toString());
    return link;
  }


}
