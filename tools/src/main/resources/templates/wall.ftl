<script>
  $(function () {
    $('[data-toggle="tooltip"]').tooltip()
  })
</script>
<div class="container-fluid">
  <h1 class="content-subhead">Completed</h1>
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
