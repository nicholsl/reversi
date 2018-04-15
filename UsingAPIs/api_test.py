""" By Josh Gerstein, started with template by Jeff Ondich. 4/11/2018
Command line application to get search results from OEIS using their JSON API
Requires Python 3.6 or higher
"""
import argparse
import json
import sys
import traceback
import urllib.request
from collections import OrderedDict
from math import ceil
from textwrap import TextWrapper

# Constants based on current OEIS that might change at some point
RESULTS_PER_QUERY = 10
OEIS_ID_DIGITS = 6


def seq_id_from_number(sequence_number):
    return "A" + str(sequence_number).zfill(6)


def retrieve_object_from_url(url):
    """Helper function that requests data from an url, then interprets the data as a JSON document and returns an
    equivalent python object (OrderedDict if the JSON document encodes an object). Shortened version of what the
    template code does, with error checking."""
    server_response = urllib.request.urlopen(url)
    try:
        return json.load(server_response, object_hook=OrderedDict)
    except json.decoder.JSONDecodeError:
        # If there's a decode error, print the normal traceback error message along with a message about the
        # probable cause of the error
        traceback.print_exc()
        print("The server's response was not valid JSON, probably because it couldn't parse the query", file=sys.stderr)
        exit(1)


def print_matching_sequences_summary(terms_string: str, unordered, show_all):
    """Prints a list of OEIS sequences which match the given search term. Uses the OEIS search API. Boolean parameter
    unordered determines if the query should search for the terms in order or not. Boolean parameter show_all
    determines if all results should be printed or just first query. """
    if unordered:
        # OEIS interprets space-separated integers as an unordered term query, comma-separated integers as ordered.
        query = terms_string.replace(",", " ")
    else:
        query = terms_string

    base_url = "https://oeis.org/search?fmt=json&q={}&start={}"
    url = base_url.format(urllib.request.quote(query), "0")
    data_from_server = retrieve_object_from_url(url)

    # First print the number of results found
    number_of_results = data_from_server["count"]
    result_count_report = f"Found {number_of_results} sequences matching {query}"
    if number_of_results == 0:
        result_count_report += "."
    # OEIS API gives null results if there are too many to list
    elif data_from_server["results"] is None:
        result_count_report += ", please specify more terms and try again."
    elif not show_all and number_of_results > RESULTS_PER_QUERY:
        result_count_report += f", only showing first {RESULTS_PER_QUERY}:"
    else:
        result_count_report += ":"
    print(result_count_report)

    # Quit early if we didn't get any results (happens when too many to list or when there are no matches)
    if data_from_server["results"] is None:
        return

    # The OEIS API gives a limited number (10 = RESULTS_PER_QUERY) sequences per query
    if show_all:
        queries_to_perform = ceil(number_of_results / RESULTS_PER_QUERY)
    else:
        queries_to_perform = 1
    query_counter = 0

    description_wrapper = TextWrapper(width=80, break_on_hyphens=False)
    terms_wrapper = TextWrapper(width=80, initial_indent="Terms: ", max_lines=2, placeholder="...")
    description_template = "{}) ID: {}, {}"

    while query_counter < queries_to_perform:
        # Don't want to redo the first query
        if query_counter > 0:
            url = base_url.format(urllib.request.quote(query), str(query_counter * 10))
            data_from_server = retrieve_object_from_url(url)

        cur_result_number = query_counter * RESULTS_PER_QUERY + 1
        for i, sequence_data in enumerate(data_from_server["results"], start=cur_result_number):
            sequence_id = seq_id_from_number(sequence_data["number"])
            terms = sequence_data["data"].replace(',', ', ')
            description = description_template.format(i, sequence_id, sequence_data["name"])
            terms = terms_wrapper.fill(terms)
            description = description_wrapper.fill(description)
            print(description, terms, sep='\n')
            # Print newline after entries to separate them, except for the last one.
            if i != number_of_results:
                print()
        query_counter += 1


def replace_links(string: str):
    split_on_links = string.split("<a href=\"")
    for i, s in enumerate(split_on_links):
        split_on_links[i] = s.replace("\">", " ", 1).replace("</a>", "", 1)
    return ''.join(split_on_links)


def print_all_info_for_sequence(search_string):
    data_from_server = retrieve_object_from_url("https://oeis.org/search?fmt=json&q=id:" + search_string)
    print(f"All information on sequence {search_string}:")
    wrapper = TextWrapper(width=80, break_on_hyphens=False, break_long_words=False, initial_indent=' ' * 2,
                          subsequent_indent=' ' * 4)
    for key, value in data_from_server["results"][0].items():
        header = key.capitalize() + ":"
        if isinstance(value, list) and len(value) == 1:
            value = value[0]

        if isinstance(value, list):
            replaced = [replace_links(s) for s in value]
            content = '\n'.join(map(wrapper.fill, replaced))
        elif key == "data":
            header = "Terms:"
            content = wrapper.fill(value.replace(",", ", "))
        elif key == "number":
            header = "ID:"
            content = wrapper.fill(seq_id_from_number(value))
        else:
            content = wrapper.fill(str(value))
        content = replace_links(content)
        if len(header + content) <= 80:
            print(header + content)
        else:
            print(header, content, sep='\n')


def oeis_query(string):
    """Given the user's inputted search string, return a tuple (query type, formatted query), where query type is either
    "sequence id" or "terms", and formatted query is a corrected query string. Raises argparse.ArgumentTypeError if
    the string does not fit either format."""
    if string[0] == "A" and len(string) == 7 and string[1:].isdigit():
        # Search for single sequence must be of the form A<6 digits>
        return "sequence id", string

    # If the string is not a sequence id, then we assume it's a list of terms, which must be a comma-separated
    # sequence of things interpretable as integers
    formatted_terms = []
    for term in string.split(','):
        try:
            term_as_integer = int(term)
        except ValueError:
            error_msg = f"{string} is not a sequence ID and the term {term!r} cannot be interpreted as an integer"
            raise argparse.ArgumentTypeError(error_msg)
        formatted_terms.append(str(term_as_integer))
    formatted_string = ','.join(formatted_terms)
    return "terms", formatted_string


def get_parsed_args():
    """Uses argparse to parse commandline arguments, and returns the argument namespace object."""
    parser = argparse.ArgumentParser(description='Get sequence information from OEIS API')
    parser.add_argument("query", type=oeis_query,
                        help="either a comma-separated sequence of integer terms to search for, or an OEIS sequence ID"
                             f" (starting with 'A' and followed by exactly {OEIS_ID_DIGITS} digits).\naccepts "
                             "underscores as visual separators for digit grouping instead of commas within a term")
    parser.add_argument("-u", "--unordered", action="store_true",
                        help="search for sequences containing the given terms in any order")
    parser.add_argument("-a", "--show-all", action="store_true",
                        help=f"show all matching sequences, rather than just the first {RESULTS_PER_QUERY} (may take "
                             "a long time)")
    return parser.parse_args()


def main():
    args = get_parsed_args()
    query_type, formatted_query = args.query
    if query_type == "terms":
        print_matching_sequences_summary(formatted_query, args.unordered, args.show_all)
    elif query_type == "sequence id":
        print_all_info_for_sequence(formatted_query)


if __name__ == '__main__':
    main()
