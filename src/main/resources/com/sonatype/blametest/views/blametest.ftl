<#-- @ftlvariable name="" type="com.sonatype.blametest.resources.BlameView" -->
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
          <form action="/blame">
            <fieldset>
              <div class="form-group">
                <label for="url">Url</label>
                <input type="text" class="form-control" id="url" name="url" aria-describedby="urlHelp"
                placeholder="Enter url" value="${url}">
                <small id="urlHelp" class="form-text text-muted">Enter the url for the fix commit link.</small>
              </div>

              <button type="submit" class="btn btn-primary">Do it</button>

                <div class="form-group">
                  <label for="url">Current Url</label>
                  <input type="text" class="form-control" id="url" name="url" aria-describedby="urlHelp" placeholder="Enter url">
                  <small id="urlHelp" class="form-text text-muted">Enter the url for the fix commit link.</small>
                </div>
            </fieldset>
          </form>
        </div>
      </div>


      <div class="card border-secondary mb-3">
        <div class="card-header">Header</div>
        <div class="card-body">
          Current Url is ${url}
        </div>
      </div>

      <div class="card border-secondary mb-3">
        <div class="card-header">Header</div>
        <div class="card-body">
          <h4 class="card-title">Secondary card title</h4>
          <p class="card-text">Some quick example text to build on the card title and make up the bulk of the card's content.</p>
        </div
      </div>


    </div>



    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>
  </body>
</html>
