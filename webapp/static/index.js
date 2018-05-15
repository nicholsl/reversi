/*
	Created by Cameron Kline-Sharpe and Joshua Gerstein
	
*/

var latInput = document.getElementById("lat_input")
var lonInput = document.getElementById("lon_input")

function getLocation() {
	// This function was mostly copied from w3schools.com tutorial on geolocation. 
	if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(fillLocation, showError);
    } else { 
        alert("Your browser does not support geolocation");
    }
}

function fillLocation(position) {
	// Given a geolocation object, write the latitude and longitude in the appropriate form elements
	var lat = position.coords.latitude;
    var lon = position.coords.longitude;
	latInput.value = lat.toString();
	lonInput.value = lon.toString();
}
	
function showError(error) {
	// This function was mostly copied from w3schools.com tutorial on geolocation. Handles errors.
    switch(error.code) {
        case error.PERMISSION_DENIED:
            break;
        case error.POSITION_UNAVAILABLE:
            alert("Your location is unavailable.");
            break;
        case error.TIMEOUT:
            alert("Location request timed out.");
            break;
        case error.UNKNOWN_ERROR:
            alert("An unknown error occurred.");
            break;
    }
}