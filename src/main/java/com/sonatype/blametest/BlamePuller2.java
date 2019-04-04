package com.sonatype.blametest;

import java.util.List;

import com.sonatype.blametest.models.FileCommitHistoryItem;
import com.sonatype.blametest.models.githubgraphql.Commit;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class BlamePuller2 {

  private String repo;
  private String owner;
  private String filename;
  private String startingCommit;
  private Integer lineNumber;

  private GithubService githubService = new GithubService();


  public BlamePuller2(String owner, String repo, String filename, String startingCommit,
                      Integer lineNumber) {
    this.owner = owner;
    this.repo = repo;
    this.filename = filename;
    this.startingCommit = startingCommit;
    this.lineNumber = lineNumber;
  }


  @SneakyThrows
  public List<FileCommitHistoryItem> loadFileCommitHistory() {
    List<FileCommitHistoryItem> fileCommitHistory =
        githubService.getFileCommitHistory(owner, repo, filename, startingCommit);
    fileCommitHistory.forEach(fileCommitHistoryItem -> {
      fileCommitHistoryItem.setRepo(repo);
      fileCommitHistoryItem.setOwner(owner);
      fileCommitHistoryItem.setFilename(filename);
      // fileCommitHistoryItem.init();
    });


    // set the line number in the first file
    fileCommitHistory.get(0).setLineNumber(lineNumber);

    // for all the other files get the contents of the previous file and the line from the previous
    // file and try to find the closest match
    for (int i=1; i < fileCommitHistory.size(); i++) {
      FileCommitHistoryItem current = fileCommitHistory.get(i);
      FileCommitHistoryItem previous = fileCommitHistory.get(i - 1);
      String previousLine = previous.getTheLine();
      int closestMatchLine = findClosestMatch(previousLine, current.getRawFileContents());
      current.setLineNumber(closestMatchLine);
      String theLine = current.getTheLine();
      boolean value = true;
    }

    return  fileCommitHistory;
  }

  private Integer findClosestMatch(String previousLine, String currentContents) {
    Integer bestMatch = 9999;
    Integer currentValue = -1;
    String [] lines = currentContents.split("\\r?\\n");
    for (int i=0; i< lines.length; i++) {

      int distance = LevenshteinDistance.getDefaultInstance().apply(lines[i], previousLine);
      if (distance < bestMatch) {
        bestMatch = distance;
        currentValue = i +1; // 0 based vs 1 based
      }
    }

    return currentValue;

  }


}
