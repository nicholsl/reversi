README.txt
For webapp assignment due 5/15/2018
Written by Cameron Kline-Sharpe and Joshua Gerstein.

First, be aware that our webapp needs to use geolocation, which requires https, 
and because of the restrictions of hosting on perlman, there was no way for us to
get a "real" SSL certificate. We are using a self-signed certificate, which no OS 
or browser will trust. So, browsers will throw a warning about an invalid 
certificate, and to access our webapp one must go around that warning. The private
key is stored directly on perlman, so anyone with access to that could decrypt
your communications including gps location if you provide it, so this is obviously 
not secure, nor intended to be.

This webapp provides the people who use it with the ability to search for
services (restaurants, gift shops, currency exchange, etc.) which are 
available in the LAX airport.

Note: The website's calculation for distance between points is slightly more accurate
than our API's, which causes some apparent contradictions. For example, a search for
services within 300 meters of (33.9416 N, 118.4085 W) returns some services whose 
listed distances are 300-320 meters.


Overview:
	The user of the webapp will arrive at the front page.
	That person will use the tools on the front page to search for a service (food, shopping, etc.) in LAX.
	Upon hitting the "Search" button, the results will be displayed. Right now, 
		we intend to have the results display on a new webpage.
	The person is now on the results page, and can browse through the services
		which they obtained through their search.
	If the person clicks on one of the hyperlinked names of the services displayed,
		they will be taken to a new page with more detailed information about
		that service as well as a google maps image of the location surrounding
		the service in question.

Specifics: All mockups are in the directory webapp/mockups/ .
	Front Page:
		The mockup image of the front page is cs257_webapp_front_page. Note that the
			"Near Me?" option has been selected (there is an X in the square beside
			that option), so the gps coordinates parameters are visible. If the 
			"Near Me?" option is not selected, none of those options (those inside
			the grey dotted box) will appear. The grey box itself will not actually
			be on the webpage, it is simply to deliniate which search parameters
			are normally hidden.
	Search Results:
		The mockup image of the search results page is cs257_webapp_search_results. Note that the
			text in blue and underlined (such as "Service 1", the first element of the "Service
			Name" column) are hyperlinks and will cause the user to be sent to a new page. The results
			can be scrolled through by clicking on the hyperlinked numbers on the botton of the table.
			Note that the distance column is only displayed when the person using the webapp
			inputs their gps coordinates. The text saying such in the image will not be present in the
			final version.
	Service Detals:
		The mockup image of this page is named cs257_webapp_service_specifics. This page contains
		further detail on the service that the person using the webapp has selected, as well as
		a google map of the service in question.
	
