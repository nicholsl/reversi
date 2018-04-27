# !/usr/bin/env python3
"""
    books_and_authors_converter.py
    Jeff Ondich, 24 April 2017
    Updated 20 April 2018

    Sample code illustrating a simple conversion of the
    books & authors dataset represented as in books_and_authors.csv,
    into the books, authors, and books_authors tables (in
    CSV form).

    This is trickier than my json_to_tables.py example,
    because in the books.csv file, the authors are implicit
    in the list of books rather than being separated out
    into their own data structure as they are in the
    books_and_authors.json file.
"""
import sys
import re
import csv
from collections import OrderedDict


def load_from_services_csv_file(csv_file_name):
    """Collect all the data from the services .csv file, assembling it into a list of services, a dictionary of
    terminals, and a list of service/terminal ID links.
    """
    csv_file = open(csv_file_name)
    reader = csv.reader(csv_file)

    terminals = {"Terminal 1": 0, "Terminal 2": 1, "Terminal 3": 2, "Terminal 4": 3, "Terminal 5": 4, "Terminal 6": 5,
                 "Terminal 7": 6, "Terminal 8": 7, "TBIT": 8}

    services = []
    services_in_terminals = []
    for row in reader:
        assert len(row) == 26
        service = (("id", row[0]), ("CompanyName", row[2]), ("CompanyDescription", row[3]),
                   ("CompanyLocationDescription", row[4]), ("Terminal", row[9]), ("Location", row[10]),
                   ("Categories", row[11]), ("LocationPhone", row[16]), ("Lat", row[20]), ("Long", row[21]))
        service = OrderedDict(service)
        services.append(service)
        # In our dataset, the only instance of two terminals for one service has "Terminal 6/7"
        if service["Terminal"] == "Terminal 6/7":
            services_in_terminals.append({"ServiceId": service["id"], "TerminalId": terminals["Terminal 6"]})
            services_in_terminals.append({"ServiceId": service["id"], "TerminalId": terminals["Terminal 7"]})
        else:
            services_in_terminals.append({"ServiceId": service["id"], "TerminalId": terminals[service["Terminal"]]})

    csv_file.close()
    return services, terminals, services_in_terminals


def save_services_table(services, csv_file_name):
    """Save the services in CSV form, with each row containing: (id, CompanyName, CompanyDescription,
    CompanyLocationDescription, Terminal, Location, Categories, LocationPhone, Lat, Long)
    """
    output_file = open(csv_file_name, 'w')
    writer = csv.writer(output_file)
    for service in services:
        service_row = []
        for value in service.values():
            #There are some non-breaking spaces and breaking spaces in the csv file where there should be NULL values.
            if value in " \xA0":
                service_row.append("NULL")

            else:
                service_row.append(value)
        writer.writerow(service_row)
    output_file.close()


def save_authors_table(authors, csv_file_name):
    """ Save the books in CSV form, with each row containing
        (id, last name, first name, birth year, death year), where
        death year can be NULL. """
    output_file = open(csv_file_name, 'w')
    writer = csv.writer(output_file)
    for author in sorted(authors, key=authors.get):
        (last_name, first_name, birth_year, death_year) = author
        if not death_year:
            death_year = 'NULL'
        author_id = authors[author]
        author_row = [author_id, last_name, first_name, birth_year, death_year]
        writer.writerow(author_row)
    output_file.close()


def save_linking_table(books_authors, csv_file_name):
    """ Save the books in CSV form, with each row containing
        (book id, author id). """
    output_file = open(csv_file_name, 'w')
    writer = csv.writer(output_file)
    for book_author in books_authors:
        books_authors_row = [book_author["book_id"], book_author["author_id"]]
        writer.writerow(books_authors_row)
    output_file.close()


def main():
    services, terminals, services_terminals = load_from_services_csv_file(
        "Los_Angeles_International_Airport_-_Terminal_Concession_Locations")

    save_services_table(services, "services.csv")
    save_authors_table(terminals, "terminals.csv")
    save_linking_table(services_terminals, "services_terminals.csv")


if __name__ == '__main__':
    main()
