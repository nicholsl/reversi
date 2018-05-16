#!/usr/bin/env python3
""" Template by Jeff Ondich, modified by Josh Gerstein and Cameron Kline-Sharpe
	Used along with api.py to create a webapp which which searches for services
	available in the LAX airport.
	
"""
import sys

import flask

app = flask.Flask(__name__, static_folder='static', template_folder='templates')


@app.route('/')
def get_main_page():
	#When the site is first accessed, run the homepage
    return flask.render_template('index.html')


@app.route('/results')
def get_results_page():
    # parameters_dict = {
    #     "terminal": flask.request.args.get('terminal'),
    #     "coordinates": flask.request.args.get('coordinates'),
    #     "radius": flask.request.args.get('radius', type=float),
    #     "search_string": flask.request.args.get('search_string'),
    #     "categories": flask.request.args.get('service_type')}
    # return flask.render_template('results.html', get_parameters=parameters_dict)
    return flask.render_template('results.html', api_port=int(sys.argv[3]))


@app.route('/results/<int:service_id>')
def get_details_page(service_id):
    return flask.render_template('details.html', api_port=int(sys.argv[3]))


def main():
	# basic input error checking
    if len(sys.argv) != 4:
        print('Usage: {0} host port api-port'.format(sys.argv[0]), file=sys.stderr)
        exit()
    host = sys.argv[1]
    port = int(sys.argv[2])
    # api_port = sys.argv[3]
    if host == "perlman.mathcs.carleton.edu":
        context = ("gersteinj.carleton.edu.crt", "gersteinj.carleton.edu.key")
        app.run(host=host, port=port, debug=True, ssl_context=context)
    else:
        app.run(host=host, port=port, debug=True)


if __name__ == '__main__':
    main()
