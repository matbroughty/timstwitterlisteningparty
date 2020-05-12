<script>
  $(function () {
  $('[data-toggle="tooltip"]').tooltip()
})
</script>
<section class="post">
  <div class="container-fluid">
    <#assign hr = "">
    <#assign date = startDate>
    <div class="card d mb-3 border-dark" style="width: 100%;">
      <div class="card-header font-weight-bold">
        <i class="fas fa-calendar-day"></i> ${startDateFormatted}
      </div>
    <#list upcoming_list as slot>
      <#if slot.isAfter(date) >
    </div><div class="card d mb-3" style="width: 100%;">
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
            <td width="35%" class="font-weight-light" style="text-align:left"><a
              href="${slot.spotifyLink}" target="_blank">
              <img data-toggle="tooltip" data-placement="top" title="${slot.album} Spotify album link"

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
            <td width="50%" style="text-align:left;">
              <b>${slot.band}</b><br/>${slot.album}
              <#if slot.isToday() >
                ${slot.buildTweeterLinks()}
              </#if>
            </td>
            <td width="15%"><a class="pure-button pure-button-active"
                               href="${slot.link}" target="_blank">

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
