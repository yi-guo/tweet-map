<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="tweetstreamer.TweetStreamer" %>

<%
    // Start Twitter streamer upon running.
    TweetStreamer.main(new String[] {});
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-type" content="text/html; charset=utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>-- Now I See You, Twitters! --</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
        <style type="text/css">
            #map-canvas {
                height: 600px;
                width: 1100px;
                margin-top: 120px;
            }
            #alert {
                float: right;
                margin-left: 30px;
                height: 35px;
                line-height: 3.5px;
                display: none;
            }
            .footer {
                bottom: 0;
                width: 100%;
                height: 60px;
                margin-top: 60px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="page-header">
                <h1>Now I See You, Twitters!</h1>
            </div>
            <form class="navbar-form navbar-left" role="search">
                <div class="form-group">
                    <input type="text" class="form-control" id="keyword" name="keyword" placeholder="Keyword">
                    <select class="form-control" id="language" name="language">
                        <option value="default">Language</option>
                        <option value="fr">French</option>
                        <option value="en">English</option>
                        <option value="ar">Arabic</option>
                        <option value="ja">Japanese</option>
                        <option value="es">Spanish</option>
                        <option value="de">German</option>
                        <option value="it">Italian</option>
                        <option value="id">Indonesian</option>
                        <option value="pt">Portuguese</option>
                        <option value="ko">Korean</option>
                        <option value="tr">Turkish</option>
                        <option value="ru">Russian</option>
                        <option value="nl">Dutch</option>
                        <option value="fil">Filipino</option>
                        <option value="msa">Malay</option>
                        <option value="zh-tw">Traditional Chinese</option>
                        <option value="zh-cn">Simplified Chinese</option>
                        <option value="hi">Hindi</option>
                        <option value="no">Norwegian</option>
                        <option value="sv">Swedish</option>
                        <option value="fi">Finnish</option>
                        <option value="da">Danish</option>
                        <option value="pl">Polish</option>
                        <option value="hu">Hungarian</option>
                        <option value="fa">Persian</option>
                        <option value="he">Hebrew</option>
                        <option value="th">Thai</option>
                        <option value="uk">Ukrainian</option>
                        <option value="cs">Czech</option>
                        <option value="ro">Romanian</option>
                        <option value="en-gb">British English</option>
                        <option value="vi">Vietnamese</option>
                        <option value="bn">Bengali</option>
                    </select>
                    <input type="text" class="form-control" id="from" placeholder="From" onfocus="(this.type='date')" onblur="(this.type='text')">
                    <input type="text" class="form-control" id="to" placeholder="To" onfocus="(this.type='date')" onblur="(this.type='text')">
                </div>
                <button type="submit" class="btn btn-primary" id="input">Submit</button>
                <div class="alert alert-danger" role="alert" id="alert">Invalid Dates</div>
            </form>
            <div id="map-canvas"></div>
        </div>
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script>
        <script>
            var timer;
            var submit = document.getElementById("input");
            submit.addEventListener("click", function(event) {
                event.preventDefault();
                time();
                plot();
            });
            
            function areValidDates(startDate, endDate) {
                var today = new Date();
                var start = new Date(startDate);
                var end = new Date(endDate);
                if (startDate.length > 0) {
                    if (endDate.length > 0) {
                        return start < end && end < today;
                    }
                    return start < today;
                }
                if (endDate.length > 0) {
                    return end < today;
                }
                return true;
            }
            
            function reqListener() {
                var response = JSON.parse(this.responseText);
                if (response.status == 200) {
                    var coordinates = [];
                    response.coordinates.forEach(function(coordinate) {
                        coordinates.push(new google.maps.LatLng(coordinate.latitude, coordinate.longitude));
                    });
                    var pointArray = new google.maps.MVCArray(coordinates);
                    heatmap.setData(pointArray);                    
                } else {
                    console.log(response.message);
                }
            };

            function plot() {
                console.log(new Date());
                var keyword = document.getElementById("keyword").value;
                var language = document.getElementById("language").value;
                var startDate = document.getElementById("from").value;
                var endDate = document.getElementById("to").value;
                if (areValidDates(startDate, endDate)) {
                    document.getElementById("alert").style.display = "none";
                    var request = new XMLHttpRequest();
                    request.onload = reqListener;
                    request.open("GET", "Tweets?keyword=" + encodeURI(keyword)
                        + "&language=" + encodeURI(language)
                        + "&startDate=" + encodeURI(startDate)
                        + "&endDate=" + encodeURI(endDate), true);
                    request.send();
                } else {
                    document.getElementById("alert").style.display = "block";
                    var pointArray = new google.maps.MVCArray([]);
                    heatmap.setData(pointArray);            
                }
            };

            function time() {
                clearInterval(timer);
                timer = setInterval(function() {
                    plot();
                }, 30000);
            }

            time();
        </script>
        <script>
            var map, pointarray, heatmap;
            function initialize() {
                var mapOptions = {
                    zoom: 2,
                    center: new google.maps.LatLng(30.52, -34.34),
                    mapTypeId: google.maps.MapTypeId.SATELLITE
                };
                map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
                heatmap = new google.maps.visualization.HeatmapLayer();
                heatmap.setMap(map);
            }
            google.maps.event.addDomListener(window, 'load', initialize);
            plot();
        </script>
        <footer class="footer">
            <div class="container">
                <p class="text-muted">Copyright Â© 2015 Yi Guo Programmed.</p>
            </div>
        </footer>
    </body>
</html>