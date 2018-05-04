#!/usr/bin/env python3
"""api.py, by Josh Gerstein and Cameron Kline-Sharpe, 5/4/2018. Written for CS 257 Software Design with Jeff Ondich
at Carleton College, Spring 2018
Implementation of our api endpoint, /services?parameters, using flask.
Accesses and performs queries on our database on perlman.mathcs.carleton.edu with psycopg2
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
    return "Hello, 137.22.x.x. Maybe you were looking for <a href=\"services\">/services</a>?"


@app.route('/services')
def get_services():
    """ Returns the list of movies that match GET parameters:
          start_year, int: reject any movie released earlier than this year
          end_year, int: reject any movie released later than this year
          genre: reject any movie whose genre does not match this genre exactly
        If a GET parameter is absent, then any movie is treated as though
        it meets the corresponding constraint. (That is, accept a movie unless
        it is explicitly rejected by a GET parameter.)
    """
    parameters_dict = {
        "terminal": flask.request.args.get('terminal'),
        "coordinates": flask.request.args.get('coordinates'),
        "radius": flask.request.args.get('radius', default=100, type=float),
        "search_string": flask.request.args.get('search_string'),
        "service_type": flask.request.args.get('service_type'),
        "company_location": flask.request.args.get('company_location')}
    query_string, parameters_tuple = create_sql_query(parameters_dict)
    services_list = query_perlman(query_string, parameters_tuple)
    json_services = json.dumps(services_list, indent=4, ensure_ascii=False)
    # Minimal html formatting to make it display as a readable json with monospaced font, instead of all on one line
    # with a standard font.
    formatted_html_response = "<html>\n<meta charset=\"utf-8\" />\n<pre>\n{}</pre>\n</html>".format(json_services)
    return formatted_html_response


def create_sql_query(parameters_dict):
    parameters = []
    query_string = "SELECT * FROM services"
    if any(v is not None for v in parameters_dict.values()):
        query_string += " WHERE "
        for (key, value) in parameters_dict.items():
            # TODO: delete debugging part of next line, and actually implement radius, coords.
            if value is None or key in ('radius', 'coordinates'):
                continue

            if key == "search_string":
                # PSQL does case-insensitive pattern matching with: str ILIKE str
                # It uses _ to match a single character, and % to match any string of 0 or more characters.
                query_string += "company_name ILIKE %s AND "
                # We need to add the %'s to the parameter instead of query string because the parameter gets inserted
                # with quotes around it
                parameters.append('%' + value + '%')
            else:
                query_string += key + " = %s AND "
                parameters.append(value)
        query_string = query_string[:-5]
    return query_string, tuple(parameters)


def query_perlman(query_string, parameters_tuple):
    """Connects to our database """
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

    services_list = []
    for row in cursor:
        services_list.append(cursor_row_to_service_dict(row))
    connection.close()
    num_services = len(services_list)
    if num_services == 1:
        print("Query found {} service.".format(len(services_list)))
    else:
        print("Query found {} services.".format(len(services_list)))
    return services_list


def cursor_row_to_service_dict(row):
    keys = ("id", "company_name", "company_description", "company_location_description", "terminal", "location",
            "categories", "location_phone", "lat", "long")
    return dict(zip(keys, row))


def main():
    if len(sys.argv) != 3:
        print('Usage:\n    {} host port'.format(sys.argv[0]))
        print('Example:\n    {} perlman.mathcs.carleton.edu 5101'.format(sys.argv[0]))
        exit()

    host = sys.argv[1]
    port = int(sys.argv[2])
    app.run(host=host, port=port, debug=True)


if __name__ == '__main__':
    main()
