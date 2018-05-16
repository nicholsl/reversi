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
	/* Fill out the table with appropriate elements of resultsArray */
	
	page = searchParams.get("page");
	// If no page parseFloat given, use page 1.
	if (page === null || page < 1) {
		page = 1;
	} else {
		page = parseInt(page);
	}
	let groups = groupSequentially(resultsArray, 20);
	let numPages = groups.length;
	
	// If given page parseFloat is past end of results, set it to last page.
	if (page > numPages) {
		page = numPages;
	}	
	var curPageServices = groups[page - 1];
	updateHeader(resultsArray.length, (page - 1) * 20 + 1, curPageServices.length);
	
	pageButtonsDiv = document.getElementById("paging_buttons");
	nextPageButton = document.getElementById("next_button");
	for (var i = 1; i <= numPages; i++) {
		pageButtonsDiv.insertBefore(newPageButton(i), nextPageButton);
	}

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
	for (var k = 0; k < curPageServices.length; k++) {
		let tableRow = '<tr>';

		// Add in the hypertext name of the company
		tableRow += '<td><a href="/results/' + curPageServices[k]["id"] + '">';
		tableRow += curPageServices[k]["company_name"] + '</a></td>';
		
		// Add in the first 20 characters of the company's description.
		// Specifically, add in the company_location_description preferentially,
		// and add in the company_description only if no location description
		// was provided.
		var description = '<td class="unimportant description">Not Provided';
		if (curPageServices[k]["company_location_description"]){
			description = '<td class="description">' + curPageServices[k]["company_location_description"];
		} else if (curPageServices[k]["company_description"]) {
			description = '<td class="description">' + curPageServices[k]["company_description"];
		}
		/*
		// cut the long descriptions down to a reasonable 20 characters long
		if (description.length > 20 && description.indexOf('<td ') != 0) {
			description = description.slice(0, 20) + "...";
		}*/
		
		tableRow += description + '</td>';
		
		// Add in the Terminal
		var terminalDescription = '<td class="unimportant">Not Provided';
		if (curPageServices[k]["terminal"]) {
			terminalDescription = "<td>" + curPageServices[k]["terminal"];
		}
		tableRow += terminalDescription + '</td>';
		
		// Add in the Distance (only if provided current location)
		if (locationProvided) {
			let serviceLat = curPageServices[k]["lat"];
			let serviceLon = curPageServices[k]["long"];
			tableRow += getUserDistanceFromCoords(serviceLat, serviceLon) + '</td>';
		}
		
		tableBody += tableRow + '</tr>';
	}
	resultsTable.innerHTML = tableBody;	
	/* Extremely hacky solution, returns zero so that sortTableAlphabetically gets called with argument 0
	after this function finishes in initialize() */
	return 0;
}

function groupSequentially(arr, numPerGroup) {
	/* Given an array arr and n=numPerGroup, return a new array of the form:
	[[first n items of arr], [next n items of arr],...[2nd to last n items of arr], [remaining items of arr]]*/
	var group = [];
    var groups = [];
	for (var i = 0; i < arr.length; i++) {
		if (i % numPerGroup == 0 && i != 0) {
			groups.push(group);
			group = [];
		}
		group.push(arr[i]);
	}
	if (group != []) {
		groups.push(group);
	}
	return groups;
}

function updateHeader(numServices, first, numPerGroup) {
	// Construct the message about query result and what's on the page, and put it in the header element
	let msg = "Found " + numServices + " service";
	if (numServices != 1) {
		msg += "s";
	}
	msg += ", showing items " + first + " to " + (first + numPerGroup - 1) + ":";
	var textNode = document.createTextNode(msg);
	document.getElementById("top_name").appendChild(textNode);
}

function newPageButton(num) {
	// Returns a button element for page #num
	var newButton = document.createElement("button");
	newButton.innerHTML = num.toString();
	newButton.type = "button";
	newButton.onclick = function () {goToPage(num)};
	"goToPage(" + num + ")";
	let cls = "page_button";
	if (num == page) {
		cls += " current";
	}
	newButton.setAttribute("class", cls);
	return newButton;
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
	let coordsParam = '';
	// Convert lat and long to correct format for our API
	if (filteredParams.has('latitude') && filteredParams.has('longitude')){
		let lat = parseFloat(filteredParams.get('latitude'));
		let lon = parseFloat(filteredParams.get('longitude'));
		if (!isNaN(lat) && !isNaN(lon)) {
			coordsParam = '&coordinates=' + lat + ',' + lon;
			console.log(filteredParams.toString());
		}
	}
	
	return getAPIBaseURL() + "/services?" + filteredParams.toString() + coordsParam;
}

function getAPIBaseURL() {
    var APIBaseURL = window.location.protocol + '//' + window.location.hostname + ':' + api_port;
    return APIBaseURL;
}

function getBaseURL() {
    var baseURL = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port;
    return baseURL;
}

function goToPage(newPage){
	// Navigate to URL except with page=newPage
	searchParams.set('page', newPage);
	window.location.href = getBaseURL() + "/results?" + searchParams.toString();
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
	
	let distance = 6371548*angleSeparation;
	return '<td>' + distance.toFixed(2) + ' m';
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
	var table, rows, switching, i, x, y, dir, shouldSwitch, switchcount = 0;
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
			x = rows[i].getElementsByTagName("TD")[n];
			y = rows[i + 1].getElementsByTagName("TD")[n];
			var xValue = parseFloat(x.innerHTML.split(" ")[0]);
			var yValue = parseFloat(y.innerHTML.split(" ")[0]);
			if (!xValue) {
				xValue = 0;
			}
			if (!yValue) {
				yValue = 0;
			}
			/*check if the two rows should switch place,
			based on the direction, asc or desc:*/
			if (dir == "asc") {
				if (xValue > yValue) {
					//if so, mark as a switch and break the loop:
					shouldSwitch= true;
					break;
				}
			} else if (dir == "desc") {
				if (xValue < yValue) {
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