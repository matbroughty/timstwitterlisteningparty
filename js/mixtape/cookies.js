function setCookie(cname, cvalue, exmins) {
    const d = new Date();
    d.setTime(d.getTime() + (exmins*60*1000));
    let expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
      }
    }
    return "";
}

function clearCookies(startTxt) {
    $.each(document.cookie.split(/; */), function () {
        var splitCookie = this.split('=');
        if (splitCookie[0].indexOf(startTxt) === 0) {
            setCookie(splitCookie[0], 0, -1);
        }
    });
}

function checkPermission(func) {
    let consentCookie = getCookie('cc_cookie');
    if (consentCookie.length < 1) return false;
    
    consentCookie = decodeURIComponent(consentCookie);
    const cookie = JSON.parse(consentCookie);
    const permissions = cookie['categories']
    return permissions.indexOf(func) >= 0;
}