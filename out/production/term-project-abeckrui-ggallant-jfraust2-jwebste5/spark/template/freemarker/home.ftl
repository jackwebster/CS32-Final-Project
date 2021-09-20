<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1" crossorigin="anonymous">
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/html5bp.css">
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="https://use.typekit.net/xtn1kvy.css">
    <link rel="icon" type="image/png" href="assets/favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="assets/favicon-16x16.png" sizes="16x16" />
  </head>

  <body data-spy="scroll" data-target=".navbar" onclick="transitionToPage('neighbors')" style="cursor: pointer">
      <br>
      <img src="assets/Stardew.png"  alt="Stars"/>
      <br>
      <div class="intro-container">
          <div class="flexbox">
              <p>
                  To begin, please use <br>
                  the terminal to upload <br>
                  a CSV
                  using the <br>following
                  command: <br>
                  <bold>stars &lt;file location&gt;</bold>
                  <br>
                  <h4>${status}</h4>
              </p>
          </div>
      </div>
      <h3> click anywhere to begin</h3>
      <br>
      <br>

      <script>
          document.querySelector('body').style.opacity = 1
          window.addEventListener( "pageshow", function ( event ) {
              var historyTraversal = event.persisted ||
                  ( typeof window.performance != "undefined" &&
                      window.performance.navigation.type === 2 );
              if ( historyTraversal ) {
                  // Handle page restore.
                  window.location.reload();
              }
          });
      </script>

      <script>
          window.transitionToPage = function(href) {
              document.querySelector('body').style.opacity = 0
              setTimeout(function() {
                  window.location.href = href
              }, 500)
          }
          document.addEventListener('DOMContentLoaded', function(event) {
              }
          )
      </script>
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="js/jquery-2.1.1.js"></script>
     <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/js/bootstrap.bundle.min.js" integrity="sha384-ygbV9kiqUc6oa4msXn9868pTtWMgiQaeYH7/t7LECLbyPA2x65Kgf80OJFdroafW" crossorigin="anonymous"></script>
  </body>
  <!-- See http://html5boilerplate.com/ for a good place to start
       dealing with real world issues like old browsers.  -->
</html>