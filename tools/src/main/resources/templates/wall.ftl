<div class="container-fluid">
        <div class="row">
          <div class="col-sm">
            <h1 class="content-subhead">Completed</h1>
            <#list completed_list as slots>
            <div class="row no-gutters">
              <#list slots as slot>
              <div>
                <a href="${slot.spotifyLink}" target="_blank"><img class="img-fluid" src="${slot.spotifyImgLinkSmall}"
                                                                   data-toggle="tooltip" data-placement="top"
                                                                   title="${slot.band}  ${slot.album}" alt="album"></a>
              </div>
            </#list>
          </div>
        </#list>
      </div>
      <div class="col-sm">
        <h1 class="content-subhead">Upcoming</h1>
        <#list upcoming_list as slots>
        <div class="row no-gutters">
          <#list slots as slot>
          <div>
            <a href="${slot.spotifyLink}" target="_blank"><img class="img-fluid" src="${slot.spotifyImgLinkSmall}"
                                                               data-toggle="tooltip" data-placement="top"
                                                               title="${slot.band}  ${slot.album}" alt="album"></a>
          </div>
        </#list>
      </div>
    </#list>
  </div>
</div>
