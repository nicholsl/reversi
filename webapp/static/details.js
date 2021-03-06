/* By Josh Gerstein and Cameron Kline-Sharpe
Script for /results/<id> page. */

// Create variables for all the relevant HTML elements.
var titleElt = document.getElementById("title");
var headingElt = document.getElementById("company_name")
var descriptionElt = document.getElementById("description");
var description2Elt = document.getElementById("description2");
var categoryElt = document.getElementById("category");
var terminalElt = document.getElementById("terminal");
var locationElt = document.getElementById("location");
var phoneElt = document.getElementById("phone");

// Get the service id as a slice of the path (which should be "/results/<id>")
var serviceId = parseInt(window.location.pathname.split('?')[0].slice(9));

initialize();

function initialize(){
	// Send the request to the services API /services endpoint
    fetch(getAPISearchURL(), {method: 'get'})

    // When the results come back, transform them from JSON string into
    // a Javascript object (in this case, a list of author dictionaries).
    .then((response) => response.json())

    // Once you have your list of author dictionaries, use it to build
    // an HTML table displaying the author names and lifespan.
    .then(fillPageWithService)
}


/* Populates the page with information about the service being examined*/
function fillPageWithService(serviceJSON) {
	
	//if there is nothing returned, do not try to fill the page
	if (serviceJSON.length != 1) {
		return;
	}
	
	var service = serviceJSON[0];
	// Set the title and main heading to the service's name
	titleElt.innerHTML = service["company_name"];
	headingElt.innerHTML = service["company_name"];
	
	// Set description, and handle case where both descriptions are provided 
	if (service["company_location_description"]) {
		descriptionElt.innerHTML = service["company_location_description"];
		// Descriptions are cut off at 255 characters, so add "..." to show this
		if (service["company_location_description"].length == 255) {
			descriptionElt.innerHTML += "...";
		}
		if (service["company_description"]) {
			description2Elt.innerHTML += service["company_description"];
		}
	} else if (service["company_description"]) {
		descriptionElt.innerHTML = service["company_description"];
	} else {
		descriptionElt.innerHTML = "Not Provided";
		descriptionElt.setAttribute("class", "unimportant");
	}
	
	// Fill in the other table items if present, and if not, put "Not Provided"
	if (service["category"]) {
		categoryElt.innerHTML = service["category"].toTitleCase();
	} else {
		categoryElt.innerHTML = "Not Provided";
		categoryElt.setAttribute("class", "unimportant");
	}
	
	if (service["terminal"]) {
		terminalElt.innerHTML = service["terminal"];
	} else {
		terminalElt.innerHTML = "Not Provided";
		terminalElt.setAttribute("class", "unimportant");
	}
	
	if (service["location"]) {
		locationElt.innerHTML = service["location"];
	} else {
		locationElt.innerHTML = "Not Provided";
		locationElt.setAttribute("class", "unimportant");
	}
	
	if (service["location_phone"]) {
		// URI for phone numbers
		phoneElt.innerHTML = '<a href="tel:+' + service["location_phone"] + '">' + service["location_phone"];
		phoneElt.innerHTML += '</a>';
	} else {
		phoneElt.innerHTML = "Not Provided";
		phoneElt.setAttribute("class", "unimportant");
	}
	
	if (service["lat"] && service["long"]){
		generateMap(parseFloat(service["lat"]), parseFloat(service["long"]), service["company_name"]);
	}
}

String.prototype.toTitleCase = function () {
	/* Function that converts strings to titlecase, written by Tuan from https://stackoverflow.com/a/5574446 */
    return this.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
};

function getAPISearchURL() {
	// Get the url to query the api with
    let APIbaseURL = window.location.protocol + '//' + window.location.hostname + ':' + api_port;
    return APIbaseURL + "/services?company_location_id=" + serviceId;
}

function generateMap(lat, lon, markerTitle){
	// uses the Google Maps api to create a map on the details page
	let coords = new google.maps.LatLng(lat, lon);
	var mapOptions = {
		center: coords,
		zoom: 16
	}
	var map = new google.maps.Map(document.getElementById("map"), mapOptions);
	var markerOptions = {
		position: coords,
		map: map, 
		title: markerTitle
	}
	var marker = new google.maps.Marker(markerOptions);
}


function getSearchUrl() {
	let current_url = window.location.href;
	let url_split = current_url.split("/");
	var baseUrl = url_split[0] + "/";
	return baseUrl;
}
