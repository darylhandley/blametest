package com.sonatype.blametest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import scala.Int;

/**
 *          "ranges": [
 *             {
 *               "age": 4,
 *               "endingLine": 9,
 *               "startingLine": 1,
 *               "commit": {
 *                 "committedDate": "2019-03-30T15:45:29Z",
 *                 "oid": "5674150d758ef1a11da6bee4608f75accc31255c",
 *                 "author": {
 *                   "user": {
 *                     "name": "Daryl Handley"
 *                   }
 *                 },
 *                 "changedFiles": 3,
 *                 "committer": {
 *                   "user": {
 *                     "name": "Daryl Handley"
 *                   }
 *                 }
 *               }
 *             },
 */

@Getter
@Setter
@Builder
@ToString
public class BlameRange {

  private Integer age;

  private Integer startingLine;

  private Integer endingLine;

  private String committedDate;

  private String commitHash;

  private String committer;

  private Integer changedFilesCount;

}
