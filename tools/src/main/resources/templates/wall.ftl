<div class="container-fluid">
  <h1 class="content-subhead">Completed</h1>
  <#list completed_list as slots>
    <div class="row no-gutters">
      <#list slots as slot>
        <div class="col">
          <img class="img-fluid" src="${slot.spotifyImgLinkSmall}"
               data-toggle="tooltip" data-placement="top"
               title="${slot.band}  ${slot.album}" alt="album">
        </div>
      </#list>
    </div>
  </#list>
</div>
