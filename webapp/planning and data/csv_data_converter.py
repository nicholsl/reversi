""" Template by Jeff Ondich, modified by Josh Gerstein and Cameron Kline-Sharpe
Updated 26 April 2018
"""
import csv
from collections import OrderedDict


def load_from_services_csv_file(csv_file_name):
    """Collect all the data from the services .csv file, assembling it into a list of services, a dictionary of
    terminals, and a list of service/terminal ID links.
    """
    csv_file = open(csv_file_name, encoding="UTF-8")
    reader = csv.reader(csv_file)

    # Skip the first row, which is data headers.
    next(reader)

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
        # In our dataset, the only instance of two terminals for one service has "Terminal 6/7 "
        if service["Terminal"] == "Terminal 6/7 ":
            services_in_terminals.append({"ServiceId": service["id"], "TerminalId": terminals["Terminal 6"]})
            services_in_terminals.append({"ServiceId": service["id"], "TerminalId": terminals["Terminal 7"]})
        else:
            services_in_terminals.append({"ServiceId": service["id"], "TerminalId": terminals[service["Terminal"]]})

        categories = service["Categories"]
        if "AIRCR" in categories or service["Categories"] == '':
            service["Categories"] = "MISCELLANEOUS"
        elif "GOVERNMENT" in categories:
            service["Categories"] = "GOVERNMENT AGENCIES"
        elif "MEDICAL" in categories:
            service["Categories"] = "MEDICAL FACILITIES"
        elif "CLEANING" in categories:
            service["Categories"] = "CUSTODIAL SERVICES"
        elif "SPAS" in categories or categories == "BUSINESS SERVICES":
            service["Categories"] = "SPAS"
        elif "CURRENCY EXCHANGE" in categories:
            service["Categories"] = "CURRENCY EXCHANGE"
        elif "ALL CARGO AIRLINES" in categories:
            service["Categories"] = "AIR CARRIER"

    csv_file.close()
    return services, terminals, services_in_terminals


def save_services_table(services, csv_file_name):
    """Save the services in CSV form, with each row containing: (id, CompanyName, CompanyDescription,
    CompanyLocationDescription, Terminal, Location, Categories, LocationPhone, Lat, Long)
    """
    with open(csv_file_name, 'w', newline='', encoding="UTF-8") as output_file:
        writer = csv.writer(output_file)
        for service in services:
            service_row = []
            for value in service.values():
                # There are some instances of ", ," and ",\xA0," in the csv file where there should be ",,"
                if value in " \xA0":
                    service_row.append("NULL")
                else:
                    service_row.append(value)
            writer.writerow(service_row)


def save_terminals_table(terminals, csv_file_name):
    """Save the terminals as a csv, each row containing (id, terminal name)"""
    with open(csv_file_name, 'w', newline='', encoding="UTF-8") as output_file:
        writer = csv.writer(output_file)
        for terminal, id_ in terminals.items():
            writer.writerow([id_, terminal])


def save_linking_table(services_in_terminal, csv_file_name):
    """Save links as a csv, with each row containing (terminal id, service id)."""
    with open(csv_file_name, 'w', newline='', encoding="UTF-8") as output_file:
        writer = csv.writer(output_file)
        for service_terminal in services_in_terminal:
            services_in_terminal_row = [service_terminal["TerminalId"], service_terminal["ServiceId"]]
            writer.writerow(services_in_terminal_row)


def main():
    """Read the original data file and create three csv files with data for tables."""
    services, terminals, services_terminals = load_from_services_csv_file(
        "Los_Angeles_International_Airport_-_Terminal_Concession_Locations.csv")

    save_services_table(services, "services.csv")
    save_terminals_table(terminals, "terminals.csv")
    save_linking_table(services_terminals, "services_terminals.csv")


if __name__ == '__main__':
    main()
