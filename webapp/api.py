#!/usr/bin/env python3
"""api.py, by Josh Gerstein and Cameron Kline-Sharpe, 5/4/2018. Written for CS 257 Software Design with Jeff Ondich
at Carleton College, Spring 2018
Implementation of our api endpoint, /services?parameters, using flask.
Accesses and performs queries on our database on perlman.mathcs.carleton.edu with psycopg2
Adapted from example program written by Jeff Ondich
"""
import json
import sys
import traceback

import flask
import psycopg2

from db_config import database
from db_config import password
from db_config import user

app = flask.Flask(__name__)


@app.route('/')
def hello():
    return "Hello, LAX services API user. Maybe you were looking for <a href=\"services\">/services</a>?"


@app.route('/services')
def get_services():
    """ Returns a list of services within LAX that match GET parameters:
          terminal: A terminal name, one of "Terminal X" for X in 1,...,8 or "TBIT" for Tom Bradley International
                    Terminal. Filters out all services which are not in the given terminal
            coordinates: Coordinates (+-latitude,+-longitude), filters out services that are not in a circle of radius
                        <radius> around the given coordinates. Radius is optional--if omitted, will use a default of 100
                         meters.
            search_string: Filters out any services that do not have search_string as a substring of the CompanyName. 
            service_type: Filters out any services that do not have their category field matching the service_type.
            company_location_id: Finds the service with the CompanyLocationID <id>.
        If a GET parameter is absent, then any service is treated as though
        it meets the corresponding constraint. (That is, accept a movie unless
        it is explicitly rejected by a GET parameter.)
    """
    parameters_dict = {
        "terminal": flask.request.args.get('terminal'),
        "coordinates": flask.request.args.get('coordinates'),
        "radius": flask.request.args.get('radius', type=float),
        "search_string": flask.request.args.get('search_string'),
        "category": flask.request.args.get('service_type'),
        "id": flask.request.args.get('company_location_id', type=int)}
    #use parameters_tuple to avoid the possibility of sql injection
    query_string, parameters_tuple = create_sql_query(parameters_dict)
    services_list = query_perlman(query_string, parameters_tuple)
    json_services = json.dumps(services_list, indent=4, ensure_ascii=False)
    # Minimal html formatting to make it display as a readable json with monospaced font, instead of all on one line
    # with a standard font.
    formatted_html_response = "<html>\n<meta charset=\"utf-8\" />\n<pre>\n{}</pre>\n</html>".format(json_services)
    return formatted_html_response


def create_sql_query(parameters_dict):
    """
    Given a dictionary of optional GET parameters, constructs a string PostgresSQL query using those parameters.
        e.g. if the recieved parameter was "terminal = TBIT", the query would be:
            SELECT * FROM services WHERE terminal = TBIT
    :param parameters_dict the dictionary of all GET parameters.
    :return: string PostgresSQL query, and the tuple of parameters to be used by the cursor to avoid SQL injection
    """
    parameters = []
    query_string = "SELECT * FROM services"
    if any(v is not None for v in parameters_dict.values()):
        # if any GET parameter was provided, begin to modify the original query
        query_string += " WHERE "
        for (key, value) in parameters_dict.items():
            if value is None:
                # do not consider parameters for which no value was received
                continue
            elif key == 'coordinates':
                user_coordinates = parameters_dict["coordinates"].split(",")
                # basic error checking: make sure the coordinates were input correctly
                if len(user_coordinates) != 2:
                    print("coordinate parameter incorrectly formatted: {0}.".format(parameters_dict["coordinates"]),
                          file=sys.stderr)
                    continue
                else:
                    try:
                        lat1 = float(user_coordinates[0])
                        lon1 = float(user_coordinates[1])
                    except ValueError as e:
                        print(e, file=sys.stderr)
                        continue
                    if parameters_dict["radius"] is None:
                        #creating default value for radius if no radius was given
                        current_radius = 100
                    else:
                        current_radius = parameters_dict["radius"]
                    temp = '''abs((6378.1*1000)*acos((sin(radians({0}))*sin(radians(lat)))+(cos(radians({0}))
                           *cos(radians(lat))*cos(radians({1})-radians(long))))) < {2} AND '''
                    query_string += temp.format(lat1, lon1, current_radius)
            elif key == 'radius':
                # since the coordinates parameter handles this case, it should not be added to the query separately
                continue
            elif key == "search_string":
                # PSQL does case-insensitive pattern matching with: str ILIKE str
                # It uses _ to match a single character, and % to match any string of 0 or more characters.
                query_string += "company_name ILIKE %s AND "
                # We need to add the %'s to the parameter instead of query string because the parameter gets inserted
                # with quotes around it
                parameters.append('%' + value + '%')
            else:
                query_string += key + " = %s AND "
                parameters.append(value)
        # remove the final ' ADD ' from the query
        query_string = query_string[:-5]

    return query_string, tuple(parameters)


def query_perlman(query_string, parameters_tuple):
    """Connects to our database, then executes the given query using query_string and parameters_tuple using a cursor
        to read the information. Returns a list of dictionaries for all services which are returned when the
        given query is executed.
        :param query_string the PostgreSQL query to be executed
        :param parameters_tuple the tuple of GET parameters
        :return: services_list the list of dictionaries where each dictionary contains the data of a single service
    """
    # Login to the database
    connection = None
    cursor = None
    try:
        connection = psycopg2.connect(database=database, user=user, password=password)
    except Exception:
        traceback.print_exc()
        exit()

    # Use cursor to execute query using query_string
    try:
        cursor = connection.cursor()
        cursor.execute(query_string, parameters_tuple)
    except Exception:
        traceback.print_exc()
        print("Query:", query_string, "\nParameters tuple:", parameters_tuple)
        exit()

    # create the list of dictionaries which will be returned
    services_list = []
    for row in cursor:
        services_list.append(cursor_row_to_service_dict(row))
    connection.close()
    num_services = len(services_list)
    if num_services == 1:
        print("Query found 1 service.")
    else:
        print("Query found {} services.".format(num_services))
    return services_list


def cursor_row_to_service_dict(row):
    """
    :param row: The list of all the data for a single service
    :return: the dictionary containing all the data for the given service
    """
    keys = ("id", "company_name", "company_description", "company_location_description", "terminal", "location",
            "category", "location_phone", "lat", "long")
    return dict(zip(keys, row))


def main():
    """
    Process command line arguments and run the API
    """
    if len(sys.argv) != 3:
        print('Usage:\n    {} host port'.format(sys.argv[0]))
        print('Example:\n    {} perlman.mathcs.carleton.edu 5101'.format(sys.argv[0]))
        exit()

    host = sys.argv[1]
    port = int(sys.argv[2])
    app.run(host=host, port=port, debug=True)


if __name__ == '__main__':
    main()
