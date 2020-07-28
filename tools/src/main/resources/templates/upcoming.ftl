<script>
  $(function () {
    $('[data-toggle="tooltip"]').tooltip()
  })
</script>
<section class="post">
   <div class="container-fluid">
    <#assign hr = "">
    <#assign date = startDate>
    <div class="card d mb-3 border-dark rounded" style="width: 100%;">
      <div class="card-header font-weight-bold">
        <i class="fas fa-calendar-day"></i> ${startDateFormatted}
      </div>
      <#list upcoming_list as slot>
      <#if slot.isAfter(date) >
    </div>
    <div class="card d mb-3" style="width: 100%;">
      <div class="card-header font-weight-bold">
        <i class="fas fa-calendar-day"></i> ${slot.dateDisplayString()}
      </div>
      <#else>
        ${hr}
        <#assign hr = "<hr/>">
      </#if>

      <div class="card-body">
        <table style="width: 100%;">
          <tr>
            <td style="width:35%;text-align:left" class="font-weight-light"><a
                      href="${slot.spotifyLink}" target="_blank">
                <img data-toggle="tooltip" data-placement="top"

                        <#if slot.isActuallySpotifyLink() >
                          title="${slot.album} Spotify album link   "
                        <#else>
                          title="${slot.album} link"
                        </#if>

                        <#if slot.spotifyImgLink?has_content >
                          src="${slot.spotifyImgLink}"
                        <#else>
                          src="https://timstwitterlisteningparty.com/img/blankcd.png"
                        </#if>

                     alt="${slot.album} spotify album"
                     style="width:80px;height:80px;"></a><br>
              <hr style="width:80px;margin-left:0;">
              ${slot.timeDisplayString()}<sup> ${slot.amPmDisplayString()}</sup>
            </td>
            <td style="width:50%;text-align:left;">
              <b>${slot.band}</b><br/>${slot.album}
              <#if slot.isToday() >
                ${slot.buildTweeterLinks()}
              </#if>
              <br><small>${slot.spotifyDateDisplay()}</small>
            </td>
            <td style="width:15%"><a class="pure-button pure-button-active"
                                     href="${slot.link}" target="_blank" data-toggle="tooltip" data-placement="top"
                                     title="Twitter Listening Party Announcement">

                <#if slot.isToday() >
                  <i class="fab fa-twitter"></i>
                <#else>
                  <i class="fab fa-twitter-square"></i>
                </#if>
              </a>
            </td>
          </tr>
        </table>
      </div>

      <#assign date = slot.isoDate>
      </#list>

    </div>
  </div>
</section>
