<script>
  $(function () {
    $('[data-toggle="tooltip"]').tooltip()
  })

</script>

<div class="container-fluid">


  <h1 class="content-subhead">Completed Listening Parties</h1>

  <a href="https://twitter.com/share?ref_src=twsrc%5Etfw" class="twitter-share-button" data-size="large" data-via="LlSTENlNG_PARTY" data-show-count="false">Tweet</a><script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>


  <div class="content pure-u-1 pure-u-md-2-3 pure-u-lg-3-4">
    <div class="btn-group-toggle d-flex justify-content-center" data-toggle="buttons">
      <label class="btn btn-secondary active">
        <input id="cover-link" type="checkbox" checked autocomplete="off" onclick="link()"> Change Album Cover Link
      </label>
    </div>
  </div>


  <div id="spotify-lnk">
    <h1 class="content-subhead">Spotify Link</h1>
    <#list completed_list as slots>
      <div class="row no-gutters">
        <#list slots as slot>
          <div class="col">
            <a href="${slot.spotifyLink}" target="_blank">
              <img class="img-fluid" src="${slot.spotifyImgLinkSmall}"
                   data-toggle="tooltip" data-placement="top"
                   title="${slot.band}  ${slot.album}" alt="${slot.band}  ${slot.album}"></a>
          </div>
        </#list>
      </div>
    </#list>
  </div>

  <div id="replay-lnk" onload="link()" style="display: none">
    <h1 class="content-subhead">Replay Link</h1>
    <#list completed_list as slots>
      <div class="row no-gutters">
        <#list slots as slot>
          <div class="col">
            <a href="${slot.replayLink}" target="_blank">
              <img class="img-fluid" src="${slot.spotifyImgLinkSmall}"
                   data-toggle="tooltip" data-placement="top"
                   title="${slot.band}  ${slot.album}" alt="${slot.band}  ${slot.album}"></a>
          </div>
        </#list>
      </div>
    </#list>
  </div>


  <script>
    function link() {
      const spotify = document.getElementById("spotify-lnk");
      const replay = document.getElementById("replay-lnk");

      if (spotify.style.display === "none") {
        spotify.style.display = "block";
        replay.style.display = "none";
      } else {
        spotify.style.display = "none";
        replay.style.display = "block";

      }
    }
  </script>

</div>
