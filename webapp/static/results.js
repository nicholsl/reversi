/*
	Created by Cameron Kline-Sharpe and Joshua Gerstein
	
	IMPORTANT CONFIGURATION INFORMATION
	The contents of getBaseURL below reflects our assumption that
	the web application (books_website.py) and the API (books_api.py)
	will be running on the same host but on different ports.
	
*/

var searchParams = new URLSearchParams(window.location.search.substring(1));
var resultsTable = document.getElementById("resultsTable");
var locationProvided = searchParams.get("latitude") && searchParams.get("longitude");
var thisPage = 0;

initialize();

function initialize() {
	// Send the request to the services API /services endpoint
    fetch(getAPISearchUrl(), {method: 'get'})

    // When the results come back, transform them from JSON string into
    // a Javascript object (in this case, a list of author dictionaries).
    .then((response) => response.json())

    // Use the service dictionaries to build
    // an HTML table displaying information about the services
    .then(fillTable)
	.then(sortTableAlphabetically);
	
}

function fillTable(resultsArray) {
	// Check to make sure the current page number isn't too high
	// The too low case is handled in flipPage().
	if ((thisPage * 25) - 1 > resultsArray.length) {
		thisPage -= 1;
	}
	//set the internal variable to the global variable
	let currentPage = thisPage;
	
	//set the value on the current page button
	document.getElementById("CurrentPageButton").innerHTML = " " + currentPage.toString() + " ";

	// Build the table body.
	let tableBody = '';
	// Add in table headings
	tableBody += "<tr>"
	tableBody += "<th onclick='sortTableAlphabetically(0)' id='columnheading0'>Company Name</th>"
	tableBody += "<th onclick='sortTableAlphabetically(1)' id='columnheading1'>Description</th>"
	tableBody += "<th onclick='sortTableAlphabetically(2)' id='columnheading2'>Terminal</th>"
	if (locationProvided){
		tableBody += "<th onclick='sortTableNumerically(3)' id='columnheading3'>Distance</th>"
	}
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
		var description = '<td class="unimportant">Not Provided';
		if (resultsArray[k]["company_location_description"]){
			description = '<td>' + resultsArray[k]["company_location_description"];
		} else if (resultsArray[k]["company_description"]) {
			description = '<td>' + resultsArray[k]["company_description"];
		}
		
		// cut the long descriptions down to a reasonable 25 characters long
		if (description.length > 25 && description.indexOf('<td ') != 0) {
			description = description.slice(0, 25) + "...";
		}
		
		tableRow += description + '</td>';
		
		// Add in the Terminal
		var terminalDescription = '<td class="unimportant">Not Provided';
		if (resultsArray[k]["terminal"]) {
			terminalDescription = "<td>" + resultsArray[k]["terminal"];
		}
		tableRow += terminalDescription + '</td>';
		
		// Add in the Distance (only if provided current location)
		if (locationProvided) {
			let serviceLat = resultsArray[k]["lat"];
			let serviceLon = resultsArray[k]["long"];
			tableRow += getUserDistanceFromCoords(serviceLat, serviceLon) + '</td>';
		}
		
		tableBody += tableRow + '</tr>';
	}
	resultsTable.innerHTML = tableBody;	
	/* Extremely hacky solution, returns zero so that sortTableAlphabetically gets called with argument 0
	after this function finishes in initialize() */
	return 0;
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

function flipPage(change){
	// Causes the thisPage global variable to change when a page changing button is pressed
	let page = searchParams.get("page");
	// Ensure that if no page number was given, there is an initial value
	if (!page) {
		page = 0;
	} else {
		page = parseInt(currentPage);
	}
	page += change;
	// Ensure sure the current page does not fall below 0
	if (page < 0) {
		page = 0;
	}
	thisPage = page;
	initialize()
}

function getUserDistanceFromCoords(serviceLat, serviceLon) {
	// Calculates how far away the given coordinates are from a service's coordinates
	serviceLat = toRadians(serviceLat);
	serviceLon = toRadians(serviceLon);
	if (!serviceLat || !serviceLon) {
		return '<td class="unimportant">Unknown';
	}
	let userLat = toRadians(parseFloat(searchParams.get("latitude")));
	let userLon = toRadians(parseFloat(searchParams.get("longitude")));
	
	// Great Circle calculation
	let angleSeparation = Math.acos(Math.sin(userLat) * Math.sin(serviceLat) + 
									Math.cos(userLat) * Math.cos(serviceLat) *
									Math.cos(Math.abs(userLon-serviceLon)));
	
	let distance = 3959*angleSeparation;
	return '<td>' + distance.toFixed(2) + ' mi';
}


function toRadians(degree){
	// simple math function used by getUserDistanceFromCoords()
	return degree * Math.PI / 180;
}

/* 	Code below adapted from w3schools code at 
	https://www.w3schools.com/howto/tryit.asp?filename=tryhow_js_sort_table_desc.
	Sorts a table based on the column values in column n, but can only sort alphabetically.*/
function sortTableAlphabetically(n) {
	var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
	// prevent the anchors' link string from being considered.
	let itemTag;
	if (n == 0){
		itemTag = "A";
	} else {
		itemTag = "TD";
	}
	table = document.getElementById("resultsTable");
	switching = true;
	//Set the sorting direction to ascending:
	dir = "asc"; 
	/*Make a loop that will continue until
	no switching has been done:*/
	while (switching) {
		//start by saying: no switching is done:
		switching = false;
		rows = table.getElementsByTagName("TR");
		/*Loop through all table rows (except the
		first, which contains table headers):*/
		for (i = 1; i < (rows.length - 1); i++) {
			//start by saying there should be no switching:
			shouldSwitch = false;
			/*Get the two elements you want to compare,
			one from current row and one from the next:*/
			x = rows[i].getElementsByTagName(itemTag)[n];
			y = rows[i + 1].getElementsByTagName(itemTag)[n];
			/*check if the two rows should switch place,
			based on the direction, asc or desc:*/
			if (dir == "asc") {
				if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
					//if so, mark as a switch and break the loop:
					shouldSwitch= true;
					break;
				}
			} else if (dir == "desc") {
				if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
					//if so, mark as a switch and break the loop:
					shouldSwitch = true;
					break;
				}
			}
		}
		if (shouldSwitch) {
			/*If a switch has been marked, make the switch
			and mark that a switch has been done:*/
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
			//Each time a switch is done, increase this count by 1:
			switchcount ++;      
		} else {
			/*If no switching has been done AND the direction is "asc",
			set the direction to "desc" and run the while loop again.*/
			if (switchcount == 0 && dir == "asc") {
				dir = "desc";
				switching = true;
			}
		}
	}
	
	// Mark table so that person using webapp knows what the table is organized by
	let indicator = " &darr;"
	if (dir == "desc") {
		indicator = " &uarr;"
	}
	let sortingColumn = "columnheading" + n.toString();
	document.getElementById("columnheading0").innerHTML = "Company Name";
	document.getElementById("columnheading1").innerHTML = "Description";
	document.getElementById("columnheading2").innerHTML = "Terminal";
	if (locationProvided){
		document.getElementById("columnheading3").innerHTML = "Distance";
	}
	document.getElementById(sortingColumn).innerHTML += indicator;
}

/* 	Code below adapted from w3schools code at 
	https://www.w3schools.com/howto/tryit.asp?filename=tryhow_js_sort_table_desc.
	Sorts a table based on the column values in column n, but can only sort numerically.*/
function sortTableNumerically(n) {
	var table, rows, switching, i, x, y, shouldSwitch, switchcount = 0;
	table = document.getElementById("myTable");
	switching = true;
	/*Make a loop that will continue until
	no switching has been done:*/
	while (switching) {
		//start by saying: no switching is done:
		switching = false;
		rows = table.getElementsByTagName("TR");
		/*Loop through all table rows (except the
		first, which contains table headers):*/
		for (i = 1; i < (rows.length - 1); i++) {
			//start by saying there should be no switching:
			shouldSwitch = false;
			/*Get the two elements you want to compare,
			one from current row and one from the next:*/
			x = rows[i].getElementsByTagName("TD")[n];
			y = rows[i + 1].getElementsByTagName("TD")[n];
			//check if the two rows should switch place:
			if (dir == "asc") {
				if (Number(x.innerHTML) > Number(y.innerHTML)) {
					//if so, mark as a switch and break the loop:
					shouldSwitch= true;
					break;
				}
			} else if (dir == "desc") {
				if (Number(x.innerHTML) < Number(y.innerHTML)) {
					//if so, mark as a switch and break the loop:
					shouldSwitch = true;
					break;
				}
			}
		}
		if (shouldSwitch) {
			/*If a switch has been marked, make the switch
			and mark that a switch has been done:*/
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
			switchcount++;
		} else {
			/*If no switching has been done AND the direction is "asc",
			set the direction to "desc" and run the while loop again.*/
			if (switchcount == 0 && dir == "asc") {
				dir = "desc";
				switching = true;
			}
		}
	}
	
	// Mark table so that person using webapp knows what the table is organized by
	let indicator = " &darr;"
	if (dir == "desc") {
		indicator = " &uarr;"
		// if the sorting direction is down, use an up arrow instead of a down arrow.
	}
	let sortingColumn = "columnheading" + n.toString();
	// clear all non-indicating columns to original form
	document.getElementById("columnheading0").innerHTML = "Company Name";
	document.getElementById("columnheading1").innerHTML = "Description";
	document.getElementById("columnheading2").innerHTML = "Terminal";
	if (locationProvided){
		document.getElementById("columnheading3").innerHTML = "Distance";
	}
	// add indicator to the correct column heading
	document.getElementById(sortingColumn).innerHTML += indicator;
}