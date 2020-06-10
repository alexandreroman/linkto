function makeItShorter() {
  const urlInput = document.getElementById("url");
  const url = urlInput.value.trim();
  if(url == "") {
    urlInput.value = "";
    urlInput.focus();
  } else {
    const xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        const result = JSON.parse(this.responseText);
        document.getElementById("query").style.display = "none";

        const resultDiv = document.getElementById("result");
        resultDiv.innerHTML = "";
        const shorterUrl = document.createElement("a");
        shorterUrl.innerHTML = result.shortUrl;
        shorterUrl.setAttribute("target", "_blank");
        shorterUrl.setAttribute("href", result.shortUrl);
        resultDiv.appendChild(shorterUrl);

        document.getElementById("tryagain").style.display = "block";
      } else if(this.status != 200) {
        urlInput.focus();
      }
    };
    xhttp.open("POST", "/api/v1/url/new", true);
    xhttp.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
    xhttp.send(JSON.stringify({ "url": url }));
  }
}
