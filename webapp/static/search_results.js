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

initialize();

function initialize() {
	var unclean_url = getSearchUrl();
	var search_url = clean_url(unclean_url);
	// Send the request to the Books API /authors/ endpoint
    fetch(search_url, {method: 'get'})

    // When the results come back, transform them from JSON string into
    // a Javascript object (in this case, a list of author dictionaries).
    .then((response) => response.json())

    // Once you have your list of author dictionaries, use it to build
    // an HTML table displaying the author names and lifespan.
    .then(function(resultsTable)) {
        // Build the table body.
        var tableBody = '';
		// TODO: add in the table headings.
        for (var k = 0; k < resultsTable.length; k++) {
            tableBody += '<tr>';

			// Add in the hypertext name of the company
            tableBody += '<td><a href="' + getBaseURL() + '/results/' + 
				resultsTable[k]["id"] + '"' + resultsTable[k]["company_name"] + 
			'</a></td>';
			
			// Add in the first 25 characters of the company's description.
			// Specifically, add in the company_location_description preferentially,
			// and add in the company_description only if no location description
			// was provided.
			var description = "None Provided"
			if (typeof resultsTable[k]["company_location_description"] == typeof " "){
				description = resultsTable[k]["company_location_description"].splice(,25) + "...";
			} else if (typeof resultsTable[k]["company_description"] == typeof " ") {
				resultsTable[k]["company_description"].splice(,25) + "...";
			}
			tableBody += '<td>' + description + '</td>';
			
			// Add in the Terminal
			var terminal_description = "Not Provided"
			if (typeof resultsTable[k]["terminal"] == typeof " ") {
				terminal_description = resultsTable[k]["terminal"];
			}
			tableBody += '<td>' + terminal_description + '</td>';
			
			// Add in the Distance (only if provided current location)
			if (search_url.contains("gps")) {
				tableBody += '<td>' + getDistance(search_url) + '</td>';
			}
			
            tableBody += '</tr>';
        }
	
		
}

function getOtherLat(url) {
	//somehow get the latitude from the search_url
}

function getOtherLong(url) {
	//somehow get the longitude from the search_url
}

function getDistance(url) {
	latitude = getOtherLat(url);
	longitude = getOtherLong(url);
	//impliment great circle calculation
}

function clean_url(url) {
	//somehow clean up the url to get rid of unneeded parameters
}

function getSearchUrl(){
	//things that I am unsure about: how to get the url of the current page. I think this will work but...
    var current_url_path = window.location.pathname;
	var edited_url_path = current_url_path.splice(8,);
	var search_api_url = getBaseURL() + "/services" + edited_url_path;
}

function getBaseURL() {
    var baseURL = window.location.protocol + '//' + window.location.hostname + ':' + api_port;
    return baseURL;
}
