<#if fullSize ><!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="apple-mobile-web-app-title" content="LP">
  <#if top100 >
    <meta name="description" content="Tim's Twitter Listening Party Top 100 Album Artwork Wall #TimsTwitterListeningParty">
    <title>#TimsTwitterListeningParty Top 100 Album Wall</title>
  <#else>
    <meta name="description" content="Tim's Twitter Listening Party Album Artwork Wall #TimsTwitterListeningParty">
    <title>#TimsTwitterListeningParty Album Wall Full Size</title>
  </#if>


  <script src="https://kit.fontawesome.com/ad4f6abd2a.js" crossorigin="anonymous"></script>
  <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>

  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>

  <!-- Global site tag (gtag.js) - Google Analytics -->
  <script async src="https://www.googletagmanager.com/gtag/js?id=UA-165104432-1"></script>
  <script>
    window.dataLayer = window.dataLayer || [];

    function gtag() {
      dataLayer.push(arguments);
    }

    gtag('js', new Date());

    gtag('config', 'UA-165104432-1');


  </script>


  <script>
    $(function () {
      $('[data-toggle="tooltip"]').tooltip()
    })

  </script>


  <link rel="apple-touch-icon" sizes="57x57" href="../img/apple-icon-57x57.png">
  <link rel="apple-touch-icon" sizes="60x60" href="../img/apple-icon-60x60.png">
  <link rel="apple-touch-icon" sizes="72x72" href="../img/apple-icon-72x72.png">
  <link rel="apple-touch-icon" sizes="76x76" href="../img/apple-icon-76x76.png">
  <link rel="apple-touch-icon" sizes="114x114" href="../img/apple-icon-114x114.png">
  <link rel="apple-touch-icon" sizes="120x120" href="../img/apple-icon-120x120.png">
  <link rel="apple-touch-icon" sizes="144x144" href="../img/apple-icon-144x144.png">
  <link rel="apple-touch-icon" sizes="152x152" href="../img/apple-icon-152x152.png">
  <link rel="apple-touch-icon" sizes="180x180" href="../img/apple-icon-180x180.png">
  <link rel="icon" type="image/png" sizes="192x192" href="../img/android-icon-192x192.png">
  <link rel="icon" type="image/png" sizes="32x32" href="../img/favicon-32x32.png">
  <link rel="icon" type="image/png" sizes="96x96" href="../img/favicon-96x96.png">
  <link rel="icon" type="image/png" sizes="16x16" href="../img/favicon-16x16.png">
  <link rel="manifest" href="../img/manifest.json">

  <link rel="stylesheet" type="text/css" href="https://cdn.wpcc.io/lib/1.0.2/cookieconsent.min.css"/>
  <script src="https://cdn.wpcc.io/lib/1.0.2/cookieconsent.min.js" defer></script>
  <script>window.addEventListener("load", function () {
      window.wpcc.init({
        "border": "thin",
        "corners": "small",
        "colors": {
          "popup": {"background": "#222222", "text": "#ffffff", "border": "#b5e1a0"},
          "button": {"background": "#b5e1a0", "text": "#000000"}
        },
        "transparency": "15",
        "content": {
          "message": "This website uses cookies to ensure you get the best experience when viewing it. By using our website and services, you agree to the use of cookies as described in our",
          "link": "cookie policy",
          "href": "https://www.websitepolicies.com/policies/view/0NHliJ6r"
        }
      })
    });</script>


  <meta name="msapplication-TileColor" content="#ffffff">
  <meta name="msapplication-TileImage" content="../img/ms-icon-144x144.png">
  <meta name="theme-color" content="#ffffff">

  <meta name="twitter:card" content="summary"/>
  <meta name="twitter:site" content="@Tim_Burgess"/>
  <meta name="twitter:creator" content="@matbroughty"/>
  <#if top100 >
    <meta name="twitter:title" content="#TimsTwitterListeningParty - Top 100 Album Wall "/>
    <meta name="twitter:description" content="Album Covers from Top 100 Listening parties"/>
  <#else>
    <meta name="twitter:title" content="#TimsTwitterListeningParty - Album Wall Full Size"/>
    <meta name="twitter:description" content="Album Covers from all Listening parties"/>
  </#if>

  <meta name="twitter:image" content="https://timstwitterlisteningparty.com/img/wall_opt.jpg"/>

</head>
<body>
<#else>
  <script>
    $(function () {
      $('[data-toggle="tooltip"]').tooltip()
    })
  </script>
</#if>


<div class="container-fluid">


  <#if !fullSize >
    <h1 class="content-subhead">Completed Listening Parties</h1>
    <a href="https://twitter.com/share?ref_src=twsrc%5Etfw" class="twitter-share-button" data-size="large"
       data-via="LlSTENlNG_PARTY" data-show-count="false">Tweet</a>
    <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>


    <div class="content pure-u-1 pure-u-md-2-3 pure-u-lg-3-4">
      <div class="btn-group-toggle d-flex justify-content-center" data-toggle="buttons">
        <label class="btn btn-secondary active" data-toggle="tooltip" data-placement="top"
               title="Toggle between the album cover artwork linking to Spotify or the Listening Party Replay page">
          <input id="cover-link" type="checkbox" checked autocomplete="off" onclick="link()"> Change Album Cover Link
        </label>
      </div>
    </div>
  </#if>

  <#if !fullSize >
    <div id="spotify-lnk">
      <h1 class="content-subhead">Spotify Link</h1>
      <#list completed_list as slots>
        <div class="row no-gutters">
          <#list slots as slot>
            <div class="col">
              <a href="${slot.spotifyLink}" target="_blank">
                <img class="img-fluid"
                        <#if fullSize >
                          src="${slot.spotifyImgLink}"
                        <#else>
                          src="${slot.spotifyImgLinkSmall}"
                        </#if>
                     data-toggle="tooltip" data-placement="top"
                     title="${slot.band}  ${slot.album} :Spotify" alt="${slot.band}  ${slot.album} :Spotify"></a>
            </div>
          </#list>
        </div>
      </#list>
    </div>
  </#if>

  <div id="replay-lnk"
          <#if !fullSize >
            onload="link()" style="display: none"
          </#if>
  >

    <#if !fullSize >
    <h1 class="content-subhead">Replay Link</h1>
    </#if>
    <#list completed_list as slots>
      <div class="row no-gutters">
        <#list slots as slot>
          <div class="col">
            <a href="${slot.replayLink}" target="_blank">
              <img class="img-fluid"
                      <#if fullSize >
                        src="${slot.spotifyImgLinkLarge}"
                      <#else>
                        src="${slot.spotifyImgLinkSmall}"
                      </#if>
                   data-toggle="tooltip" data-placement="top"
                   title="${slot.band}  ${slot.album} :Replay" alt="${slot.band}  ${slot.album} :Replay"></a>
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
<#if fullSize >
</body>
</html>
</#if>
