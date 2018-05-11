#!/usr/bin/env python3
""" Template by Jeff Ondich
"""
import sys

import flask

app = flask.Flask(__name__, static_folder='static', template_folder='templates')


@app.route('/')
def get_main_page():
    return flask.render_template('index.html', api_port=sys.argv[3])


@app.route('/results')
def get_results_page():
    parameters_dict = {
        "terminal": flask.request.args.get('terminal'),
        "coordinates": flask.request.args.get('coordinates'),
        "radius": flask.request.args.get('radius', type=float),
        "search_string": flask.request.args.get('search_string'),
        "categories": flask.request.args.get('service_type')}
    return flask.render_template('results.html', get_parameters=parameters_dict)


@app.route('/details/<int:id>')
def get_details_page():
    return flask.render_template('details.html', id=id)


def main():
    if len(sys.argv) != 4:
        print('Usage: {0} host port api-port'.format(sys.argv[0]), file=sys.stderr)
        exit()

    host = sys.argv[1]
    port = sys.argv[2]
    # api_port = sys.argv[3]

    app.run(host=host, port=port)


if __name__ == '__main__':
    main()