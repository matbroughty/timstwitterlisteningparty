<script>
  $(function () {
  $('[data-toggle="tooltip"]').tooltip()
})
</script>

<section class="post">
  <div class="container-fluid">
    <div class="card d mb-3" style="width: 100%;">
      <div class="card-header font-weight-bold">
        <i class="fas fa-archive"></i>  Archived Tweet List
      </div>
      <#list completed_list as slot>
      <div class="card-body">
        <table style="width: 100%;">
          <tr>
            <td width="35%" class="font-weight-light" style="text-align:left"><a href="${slot.getCollectionLink()}" target="_blank"><img src="${slot.spotifyImgLink}" alt="album" data-toggle="tooltip" data-placement="top" title="${slot.album} Archive Tweet List" style="width:80px;height:80px;"></a><br><hr style="width:80px;margin-left:0;">${slot.dateDisplayString()}</td>
            <td width="50%" style="text-align:left;">
              <a href="${slot.getCollectionLink()}" target="_blank" class="text-muted">
              <b>${slot.band}</b><br/>${slot.album}
              </a>
            </td>
            <td width="15%"><a class="pure-button pure-button-active"
                               href="${slot.getCollectionLink()}"><i
              class="fab fa-twitter"></i></a></td>
          </tr>
        </table>
        <hr/>
      </div>
     </#list>
      <hr/>
    </div>
  </div>
</section>
