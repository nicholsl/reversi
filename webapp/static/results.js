/*
	Created by Cameron Kline-Sharpe and Joshua Gerstein
	
	IMPORTANT CONFIGURATION INFORMATION
	The contents of getBaseURL below reflects our assumption that
	the web application (books_website.py) and the API (books_api.py)
	will be running on the same host but on different ports.
	
	TO DO:
		Add table headings
		Impliment 3 great circle calc functions
		Impliment clean_url()
	
*/

var searchParams = new URLSearchParams(window.location.search.substring(1));
var resultsTable = document.getElementById("results_table");
var currentPage = flipPage(0);
initialize();

function initialize() {
	let locationProvided = searchParams.get("latitude") && searchParams.get("longitude");
	
	// Send the request to the services API /services endpoint
    fetch(getAPISearchUrl(), {method: 'get'})

    // When the results come back, transform them from JSON string into
    // a Javascript object (in this case, a list of author dictionaries).
    .then((response) => response.json())

    // Once you have your list of author dictionaries, use it to build
    // an HTML table displaying the author names and lifespan.
    .then(function(resultsArray) {
		// Check to make sure the current page number isn't too high
		// The too low case is handled in flipPage().
		if (((currentPage + 1)*25 - 1) > resultsArray.length) {
			currentPage -= 1;
		}
		
        // Build the table body.
        let tableBody = '';
		// Add in table headings
		tableBody += "<tr>"
		tableBody += "<th>Company Name</th>"
		tableBody += "<th>Description</th>"
		tableBody += "<th>Terminal</th>"
		tableBody += "<th>Distance</th>"
		tableBody += "</tr>"
        for (var k = 25 * currentPage; k < 25 * (currentPage + 1) -1 && k < resultsArray.length; k++) {
            let tableRow = '<tr>';

			// Add in the hypertext name of the company
            tableRow += '<td><a href="/results/' + resultsArray[k]["id"] + '">';
			tableRow += resultsArray[k]["company_name"] + '</a></td>';
			
			// Add in the first 25 characters of the company's description.
			// Specifically, add in the company_location_description preferentially,
			// and add in the company_description only if no location description
			// was provided.
			var description = "Not Provided";
			if (resultsArray[k]["company_location_description"]){
				description = resultsArray[k]["company_location_description"];
			} else if (resultsArray[k]["company_description"]) {
				description = resultsArray[k]["company_description"];
			}
			
			if (description.length > 25) {
				description = description.slice(0, 25) + "...";
			}
			
			tableRow += '<td>' + description + '</td>';
			
			// Add in the Terminal
			var terminalDescription = "Not Provided";
			if (resultsArray[k]["terminal"]) {
				terminalDescription = resultsArray[k]["terminal"];
			}
			tableRow += '<td>' + terminalDescription + '</td>';
			
			// Add in the Distance (only if provided current location)
			if (locationProvided) {
				let serviceLat = resultsArray[k]["latitude"];
				let serviceLon = resultsArray[k]["longitude"];
				tableRow += '<td>' + getUserDistanceFromCoords(serviceLat, serviceLon) + '</td>';
			}
			
            tableBody += tableRow + '</tr>';
        }
		resultsTable.innerHTML = tableBody;
	})
}

function flipPage(change){
	let currentPage = searchParams.get("page");
	// Ensure that if no page number was given, there is an initial value
	if (!currentPage) {
		currentPage = 0;
	} else {
		currentPage = parseInt(currentPage);
	}
	currentPage += change;
	// Ensure sure the current page does not fall below 0
	if (currentPage < 0) {
		currentPage = 0;
	}
	return currentPage;
}

function getUserDistanceFromCoords(serviceLat, serviceLon) {
	if (!serviceLat || !serviceLon) {
		return "Not Given";
	}
	let userLat = parseFloat(searchParams.get("latitude"));
	let userLon = parseFloat(searchParams.get("longitude"));
	
	// Great Circle calculation
	let angleSeparation = Math.acos(Math.sin(userLat) * Math.sin(serviceLat) + 
									Math.cos(userLat) * Math.cos(serviceLat) *
									cos(userLon-serviceLon));
	
	let distance = 3959*angleSeparation;
	return distance;
}


function removeEmptyParams(params) {
	// Given search parameters, return a new parameters object that omits parameters with empty value
	var newParams = new URLSearchParams(params);
	for (var item of params.entries()) {
		if (!item[1]) {
			newParams.delete(item[0]);
		}
	}
	return newParams;
}

function getAPISearchUrl(){
	// Generate URL for the API based on GET parameters of current page.
	let filteredParams = removeEmptyParams(searchParams);
	return getAPIBaseURL() + "/services?" + filteredParams.toString();
}

function getAPIBaseURL() {
    var APIBaseURL = window.location.protocol + '//' + window.location.hostname + ':' + api_port;
    return APIBaseURL;
}

function getSearchUrl(){
	function getSearchUrl() {
	let current_url = window.location.href;
	let url_split = current_url.split("/");
	var baseUrl = url_split[0] + "/";
	return baseUrl;
	}
}
