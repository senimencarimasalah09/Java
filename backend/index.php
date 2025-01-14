
<!DOCTYPE html>
<html lang="en" class="cookie-manager">

<head>

  <meta charset="UTF-8">

<link rel="apple-touch-icon" type="image/png" href="https://cpwebassets.codepen.io/assets/favicon/apple-touch-icon-5ae1a0698dcc2402e9712f7d01ed509a57814f994c660df9f7a952f3060705ee.png" />
<meta name="apple-mobile-web-app-title" content="CodePen">

<link rel="shortcut icon" type="image/x-icon" href="https://cpwebassets.codepen.io/assets/favicon/favicon-aec34940fbc1a6e787974dcd360f2c6b63348d4b1f4e06c77743096d55480f33.ico" />

<link rel="mask-icon" type="" href="https://cpwebassets.codepen.io/assets/favicon/logo-pin-8f3771b1072e3c38bd662872f6b673a722f4b3ca2421637d5596661b4e2132cc.svg" color="#111" />


  <title>CodePen - Cookie Manager (Pure)</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.0/css/bootstrap.min.css'>


  <script>
  window.console = window.console || function(t) {};
</script>



  <script>
  if (document.location.search.match(/type=embed/gi)) {
    window.parent.postMessage("resize", "*");
  }
</script>


</head>

<body translate="no" >
  <div class="container h-100 m-5">
  <div class="row mb-2">
    <pre id="allcookies" class=""></pre>
  </div>
  <div class="row h-100 justify-content-center align-items-center">
    <form action="#create" id="create" class="form-inline col-12">
      <div class="form-group mb-2">
        <input type="text" class="form-control" placeholder="cookie name" value="myCookieName" name="name" id="cookie-name">
      </div>
      <div class="form-group mx-sm-3 mb-2">
        <input type="text" class="form-control" placeholder="cookie value" value="myCookieValue" name="value" id="cookie-value">
      </div>

      <div class="btn-group mx-sm-3 mb-2">
        <button class="btn btn-primary" type="submit">Create</button>
        <button class="btn btn-danger" type="button" id="deleteAllCookie">Delete</button>
      </div>
    </form>
  </div>
</div>
    <script src="https://cpwebassets.codepen.io/assets/common/stopExecutionOnTimeout-157cd5b220a5c80d4ff8e0e70ac069bffd87a61252088146915e8726e5d9f147.js"></script>

  <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>
<script src='https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.5.3/js/bootstrap.min.js'></script>
<script src='https://raw.githack.com/dimaslanjaka/smartform/master/dist/js/form-saver.js?ver=1.0.5'></script>
      <script id="rendered-js" >
(function () {
  smartform();
})();
$("#create").on("submit", function (e) {
  e.preventDefault();
  var form = $(this);
  var url = form.attr("action");
  var data = $(this).serialize().split("&");
  //console.log(data);
  var obj = {};
  for (var key in data) {
    console.log(data[key]);
    obj[data[key].split("=")[0]] = data[key].split("=")[1];
  }
  setCookie(`cookie_${obj.name}`, obj.value, 60);
  //console.log(obj);
});
$("#deleteAllCookie").on("click", function (e) {
  e.preventDefault();
  deleteAllCookies();
});
setInterval(() => {
  var tobeprinted = getCookies();
  tobeprinted.localStorageAvailable = localStorageAvailable();
  $("#allcookies").text(JSON.stringify(tobeprinted, null, 4));
}, 1000);

function getCookies() {
  var pairs = document.cookie.split(";");
  var cookies = {};
  for (var i = 0; i < pairs.length; i++) {
    var pair = pairs[i].split("=");
    cookies[(pair[0] + "").trim()] = unescape(pair.slice(1).join("="));
  }
  return cookies;
}

function setCookie(cname, cvalue, minutes) {
  var d = new Date();
  d.setTime(d.getTime() + minutes * 60 * 1000);
  var expires = "expires=" + d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  var ca = decodedCookie.split(";");
  for (var i = 0; i < ca.length; i++) {
    if (window.CP.shouldStopExecution(0)) break;
    var c = ca[i];
    while (c.charAt(0) == " ") {
      if (window.CP.shouldStopExecution(1)) break;
      c = c.substring(1);
    }
    window.CP.exitedLoop(1);
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  window.CP.exitedLoop(0);
  return "";
}

function localStorageAvailable() {
  var test = "test";
  try {
    localStorage.setItem(test, test);
    localStorage.removeItem(test);
    return true;
  } catch (e) {
    return false;
  }
}

function deleteAllCookies() {
  var cookies = document.cookie.split(";");

  for (var i = 0; i < cookies.length; i++) {
    var cookie = cookies[i];
    var eqPos = cookie.indexOf("=");
    var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
  }
}
//# sourceURL=pen.js
    </script>



</body>

</html>

