<script>
  $(function () {
    $('[data-toggle="tooltip"]').tooltip();
  });

  $(".tztooltip").hover(function () {
    const format = 'Do MMM, h:mma';
    const local = moment.tz.guess();
    var item = this;
    var id = item.getAttribute('data-id');
    var timezoneDiv = document.getElementById('timezone-' + id);
    var dateTime = item.getAttribute('data-dt');
    var date = dateTime.substr(0, 10);
    var time = dateTime.substr(11);
    var a = moment.tz(date + ' ' + time, "Europe/London");
    var disp = '<table class="tztooltip-table"><th><b>' +
        local + '</b><br/>' + a.tz(local).format(format) + '</th>' +
        '<tr><td><b>Los Angeles</b><br/>' + a.tz("America/Los_Angeles").format(format)
        + '</td></tr>' +
        '<tr><td><b>New York</b><br/>' + a.tz("America/New_York").format(format) + '</td></tr>' +
        '<tr><td><b>Rio de Janeiro</b><br/>' + a.tz("America/Sao_Paulo").format(format)
        + '</td></tr>' +
        '<tr><td><b>Berlin</b><br/>' + a.tz("Europe/Berlin").format(format) + '</td></tr>' +
        '<tr><td><b>Johannesburg</b><br/>' + a.tz("Africa/Johannesburg").format(format)
        + '</td></tr>' +
        '<tr><td><b>Moscow</b><br/>' + a.tz("Europe/Moscow").format(format) + '</td></tr>' +
        '<tr><td><b>New Delhi</b><br/>' + a.tz("Asia/Kolkata").format(format) + '</td></tr>' +
        '<tr><td><b>Tokyo</b><br/>' + a.tz("Asia/Tokyo").format(format) + '</td></tr>' +
        '<tr><td><b>Sydney</b><br/>' + a.tz("Australia/Sydney").format(format) + '</td></tr>' +
        '</table>';
    timezoneDiv.innerHTML = disp;
  });
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
              <span data-id="${slot.listeningPartyNumber}" data-dt="${slot.isoDate}"
                    class="tztooltip"
                    data-direction="bottom">
                <span
                    class="tztooltip__initiator">${slot.timeDisplayString()}<sup> ${slot.amPmDisplayString()}</sup>
                </span>
                <span id="timezone-${slot.listeningPartyNumber}" class="tztooltip__item">
                </span>
              </span>

            </td>
            <td style="width:50%;text-align:left;">
              <b>${slot.band}</b><br/>${slot.album}
              ${slot.buildTweeterLinks()}
              <br><small>${slot.spotifyDateDisplay()}</small>
            </td>
            <td style="width:15%">
              <a class="pure-button pure-button-active"
                 href="${slot.link}" target="_blank" data-toggle="tooltip" data-placement="top"
                 title="Twitter Listening Party Announcement <#if slot.hasNumber() >
             (ttlp ${slot.listeningPartyNumber} )
              </#if>">

                <#if slot.isToday() >
                  <i class="fab fa-twitter"></i>
                <#else>
                  <i class="fab fa-twitter-square"></i>
                </#if>
              </a>
              <br>
              <#if slot.hasNumber() >
                <small>ttlp ${slot.listeningPartyNumber}</small>

              </#if>
            </td>
          </tr>
        </table>
      </div>

      <#assign date = slot.isoDate>
      </#list>

    </div>
  </div>
</section>
