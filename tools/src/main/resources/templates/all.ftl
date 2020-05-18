<section class="post">
  <div class="card bg-light mb-2 border-dark " style="max-width">
    <div class="card-header"><i class="fas fa-calendar-check"></i> All Events - Searchable & Sortable</div>
    <div class="card-body p-0">
      <div class="scroll-table">
        <table id="time-slots" width="100%" class="pure-table">
          <thead>
          <tr>
            <th width="20%">Date</th>
            <th width="25%">Band</th>
            <th width="25%">Album</th>
            <th width="10%">Link</th>
            <th width="10%">Replay</th>
            <th width="10%">Archive</th>
          </tr>
          </thead>
          <tbody>
          <#list all_list as slot>
            <tr>
              <td>

                ${slot.dateDisplayString()}

              </td>
              <td>${slot.band}</td>
              <td>${slot.album}</td>
              <td><a class="pure-button pure-button-active"
                     href="${slot.link}"><i
                          class="fab fa-twitter-square"></i></a></td>

              <td><a
                        <#if slot.replayLink?has_content >
                          class="pure-button pure-button-active"
                        <#else>
                          class="pure-button-disabled"
                        </#if>
                        href="${slot.replayLink}"><i
                          class="fas fa-redo"></i></a></td>
              <td>
                <a
                        <#if slot.twitterCollectionLink?has_content >
                          class="pure-button pure-button-active"
                        <#else>
                          class="pure-button-disabled"
                        </#if>
                        href="${slot.getCollectionLink()}"><i class="fas fa-archive"></i></a></td>
            </tr>
          </#list>
          <script>
            $(document).ready(function () {
              $('#time-slots').DataTable({
                "paging": false
              });
            });
          </script>
          </tbody>
        </table>
      </div>
    </div>
  </div>

</section>
