<script>
  $(function () {
    $('[data-toggle="tooltip"]').tooltip()
  })
</script>
<section class="post">
  <div class="container-fluid">
    <div class="card d mb-3" style="width: 100%;" id="archive-cards">
      <div class="card-header font-weight-bold">
        <i class="fas fa-list-ol"></i> All Listening Parties
      </div>
      <#list all_list as slot>
        <div class="card-body">
          <table style="width: 100%;">
            <tr>
              <td width="30%" class="font-weight-light" style="text-align:left"><a href="${slot.spotifyLink}"
                                                                                   target="_blank"><img
                          src="${slot.getSpotifyImageLink()}" alt="album" data-toggle="tooltip" data-placement="top"
                          title="${slot.album} Spotify Link" style="width:80px;height:80px;"></a><br>
                <hr style="width:80px;margin-left:0;">${slot.dateDisplayString()}</td>
              <td width="40%" style="text-align:left;">
                <a href="${slot.getCollectionLink()}" target="_blank" class="text-muted">
                  <b>${slot.band}</b><br/>${slot.album}
                </a>
                ${slot.buildTweeterLinks()}
                <br><small>${slot.spotifyDateDisplay()}</small>
              </td>
              <td width="30%">

                <a class="pure-button pure-button-active" data-toggle="tooltip" data-placement="top"
                   title="Tweet Link"
                   href="${slot.link}" target="_blank"><i class="fab fa-twitter-square"></i></a>
                <hr>
                <a
                        <#if slot.replayLink?has_content >
                          class="pure-button pure-button-active"
                        <#else>
                          class="pure-button-disabled"
                        </#if>
                        href="${slot.replayLink}" target="_blank" data-toggle="tooltip" data-placement="top"
                        title="Replay Link"><i class="fas fa-redo"></i></a>
                <hr>
                <a
                        <#if slot.twitterCollectionLink?has_content >
                          class="pure-button pure-button-active"
                        <#else>
                          class="pure-button-disabled"
                        </#if>
                        href="${slot.getCollectionLink()}" target="_blank" data-toggle="tooltip" data-placement="top"
                        title="Archive Tweet Link"><i class="fas fa-list-ol"></i></a>

              </td>

            </tr>
          </table>
          <hr/>
        </div>
      </#list>
      <hr/>
    </div>
  </div>
</section>
