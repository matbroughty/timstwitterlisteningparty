<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="apple-mobile-web-app-title" content="LP">
  <meta name="description" content="Tim's Twitter Listening Party. Dates to be confirmed. Stream or play the album in question and follow me and the relevant twitterer(s) and watch the
        tweets in real time. Ask questions/ share memories etc using #TimsTwitterListeningParty">
  <title>timstwitterlisteningparty Album Wall</title>

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
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());

    gtag('config', 'UA-165104432-1');

  </script>

  <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.1/build/pure-min.css" integrity="sha384-"
        crossorigin="anonymous">

  <!--[if lte IE 8]>
  <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.1/build/grids-responsive-old-ie-min.css">
  <![endif]-->
  <!--[if gt IE 8]><!-->
  <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.1/build/grids-responsive-min.css">
  <!--<![endif]-->


  <!--[if lte IE 8]>
  <link rel="stylesheet" href="../css/layouts/blog-old-ie.css">
  <![endif]-->
  <!--[if gt IE 8]><!-->
  <link rel="stylesheet" href="../css/layouts/blog.css">
  <!--<![endif]-->

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
  <meta name="msapplication-TileColor" content="#ffffff">
  <meta name="msapplication-TileImage" content="../img/ms-icon-144x144.png">
  <meta name="theme-color" content="#ffffff">

  <meta name="twitter:card" content="summary"/>
  <meta name="twitter:site" content="@Tim_Burgess"/>
  <meta name="twitter:creator" content="@matbroughty"/>
  <meta name="twitter:title" content="#TimsTwitterListeningParty - Album Wall"/>
  <meta name="twitter:description" content="Album Covers from all Listening parties"/>
  <meta name="twitter:image" content="https://timstwitterlisteningparty.com/img/rsz_timogen.jpg"/>

</head>
<body>

<div id="layout" class="pure-g">
  <div class="sidebar pure-u-1 pure-u-md-1-3 pure-u-lg-1-4">
    <div class="header">
      <h1 class="brand-title">Album Artwork Wall</h1>
      <h4 class="status">Together Apart</h4>
      <h5 class="status"><a class="text-white justify-content-end"
                            href="shop.html"><i
        class="fas fa-shopping-basket"></i> BUY RECORDS</a></h5>
      <a href="https://twitter.com/Tim_Burgess/status/1243311699941261313">
        <img src="../img/ladylisteningroom_opt.jpg" class="img-fluid rounded mx-auto d-block"
             alt="tim's listening party room">
      </a>

      <ul class="nav-list">
        <li class="nav-item">
          <a class="pure-button" href="../index.html"><i class="fas fa-home"></i> Home</a>
        </li>

      </ul>
    </div>
  </div>

  <div class="content pure-u-1 pure-u-md-2-3 pure-u-lg-3-4">
    <div>
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
    </div>
  </div>
</div>
</div>
</body>
  </html>
