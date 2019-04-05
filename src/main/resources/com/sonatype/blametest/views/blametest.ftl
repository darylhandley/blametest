<#-- @ftlvariable name="" type="com.sonatype.blametest.views.BlameTestView" -->
<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css" integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
    <link rel="stylesheet" href="/assets/custom.css">
    <link rel="stylesheet" href="/assets/flatly.css">

    <title>Git Line History</title>
  </head>
  <body>

    <div class="container">
      <h1>Git Line History</h1>

      <div class="card border-secondary mb-3">
        <div class="card-body">
          <form>
            <fieldset>
              <div class="form-group">
                <label for="url">Url</label>
                <input type="text" class="form-control" id="url" name="url" aria-describedby="urlHelp"
                placeholder="Enter url" value="${url}">
                <small id="urlHelp" class="form-text text-muted">Enter the url for the fix commit link.</small>
              </div>

              <button type="submit" class="btn btn-primary">Do it</button>
            </fieldset>
          </form>
        </div>
      </div>



      <#if fileCommitHistory??>
        <#list fileCommitHistory as fileCommitHistoryItem>
          <div class="card border-secondary mb-3">
            <div class="card-header">
              <a href="${fileCommitHistoryItem.diffLink}">
                ${fileCommitHistoryItem.commit.oid}
              </a>
              : ${fileCommitHistoryItem.commit.message}<br/>

            </div>
            <div class="card-body">

              <table class="table table-hover">
                <tbody>
                  <tr class="table-default">
                    <th scope="row">Message</th>
                    <td>${fileCommitHistoryItem.commit.message}</td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">Author</th>
                    <td>
                      <#if fileCommitHistoryItem.commit.author??>
                        <#if fileCommitHistoryItem.commit.author.user??>
                          ${fileCommitHistoryItem.commit.author.user.name}
                        </#if>
                      </#if>
                    </td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">File</th>
                    <td>${fileCommitHistoryItem.filename}</td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">Changed Files</th>
                    <td>${fileCommitHistoryItem.commit.changedFiles}</td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">File Additions</th>
                    <td>${fileCommitHistoryItem.commitFile.additions}</td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">File Deletions</th>
                    <td>${fileCommitHistoryItem.commitFile.deletions}</td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">Line Number</th>
                    <td>${fileCommitHistoryItem.lineNumber}</td>
                  </tr>
                  <tr class="table-default">
                    <th scope="row">Raw Url</th>
                    <td>
                      <a href = "${fileCommitHistoryItem.commitFile.rawUrl}">Click me</a>
                    </td>
                  </tr>
                </tbody>
              </table>


              <p class="card-text">
                <b>Line</b>
              </p>
              <div class="card text-white bg-secondary mb-3">
                <div class="card-body">
                  <p class="card-text">
                    <a class="text-white" href="${fileCommitHistoryItem.linkToLine}">
                     ${fileCommitHistoryItem.lineNumber}
                    </a>&nbsp;&nbsp;
                    ${fileCommitHistoryItem.theLine}
                  </p>
                </div>
              </div>


              <div class="card text-white bg-secondary mb-3">
                <div class="card-header text-white">
                  <a class="card-link text-white" data-toggle="collapse" href="#collapsePatch">
                    <b>Patch (click to toggle)</b>
                  </a>
                </div>
                <div id="collapsePatch" class="collapse hide">
                  <div class="card-body">
                    <p class="card-text">
                      <pre style="color:white">${fileCommitHistoryItem.commitFile.patch}</pre>
                    </p>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </#list>
      </#if>






    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>
  </body>
</html>
