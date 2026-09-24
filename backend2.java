import java.util.ArrayList;
import java.util.Scanner;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


class Opportunity {

    int id;
    String title;
    String company;
    String type;
    String location;

    Opportunity(int id, String title, String company,
                String type, String location) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.type = type;
        this.location = location;
    }

    void display() {

        System.out.println("\nOpportunity ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Company: " + company);
        System.out.println("Type: " + type);
        System.out.println("Location: " + location);
    }
}


class Application {

    int applicationId;
    int opportunityId;
    String status;

    Application(int applicationId, int opportunityId) {

        this.applicationId = applicationId;
        this.opportunityId = opportunityId;
        this.status = "Applied";
    }

    void display() {

        System.out.println("\nApplication ID: " + applicationId);
        System.out.println("Opportunity ID: " + opportunityId);
        System.out.println("Status: " + status);
    }
}


public class backend2 {

    static ArrayList<Opportunity> opportunities =
            new ArrayList<>();

    static ArrayList<Application> applications =
            new ArrayList<>();

    static Scanner sc = new Scanner(System.in);

    static int opportunityId = 1;
    static int applicationId = 1;


    // --------------------------------------------------
    // CREATE OPPORTUNITY
    // --------------------------------------------------

    static void createOpportunity() {

        System.out.print("Enter Opportunity Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Company Name: ");
        String company = sc.nextLine();

        System.out.print("Enter Type (Job/Internship): ");
        String type = sc.nextLine();

        System.out.print("Enter Location: ");
        String location = sc.nextLine();

        Opportunity opportunity =
                new Opportunity(
                        opportunityId++,
                        title,
                        company,
                        type,
                        location
                );

        opportunities.add(opportunity);

        System.out.println(
                "\nOpportunity created successfully!"
        );

        opportunity.display();
    }


    // --------------------------------------------------
    // DISPLAY OPPORTUNITIES
    // --------------------------------------------------

    static void displayOpportunities() {

        if (opportunities.isEmpty()) {

            System.out.println(
                    "\nNo opportunities available."
            );

            return;
        }

        System.out.println(
                "\n===== AVAILABLE OPPORTUNITIES ====="
        );

        for (Opportunity opportunity : opportunities) {

            opportunity.display();
        }
    }


    // --------------------------------------------------
    // APPLY FOR OPPORTUNITY
    // --------------------------------------------------

    static void applyForOpportunity() {

        if (opportunities.isEmpty()) {

            System.out.println(
                    "\nNo opportunities available."
            );

            return;
        }

        displayOpportunities();

        System.out.print(
                "\nEnter Opportunity ID to apply: "
        );

        int oppId = sc.nextInt();
        sc.nextLine();

        for (Opportunity opportunity : opportunities) {

            if (opportunity.id == oppId) {

                Application application =
                        new Application(
                                applicationId++,
                                oppId
                        );

                applications.add(application);

                System.out.println(
                        "\nApplication submitted successfully!"
                );

                application.display();

                return;
            }
        }

        System.out.println(
                "\nOpportunity not found."
        );
    }


    // --------------------------------------------------
    // DISPLAY APPLICATIONS
    // --------------------------------------------------

    static void displayApplications() {

        if (applications.isEmpty()) {

            System.out.println(
                    "\nNo applications found."
            );

            return;
        }

        System.out.println(
                "\n===== APPLICATIONS ====="
        );

        for (Application application : applications) {

            application.display();
        }
    }


    // --------------------------------------------------
    // GET JSON VALUE
    // --------------------------------------------------

    static String getValue(String json, String key) {

        String search = "\"" + key + "\":";

        int start = json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start = start + search.length();

        while (start < json.length() &&
               (json.charAt(start) == ' ' ||
                json.charAt(start) == '"')) {

            start++;
        }

        int end = start;

        while (end < json.length() &&
               json.charAt(end) != ',' &&
               json.charAt(end) != '}') {

            end++;
        }

        return json.substring(start, end)
                .replace("\"", "")
                .trim();
    }


    // --------------------------------------------------
    // SEND RESPONSE
    // --------------------------------------------------

    static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        OutputStream os =
                exchange.getResponseBody();

        os.write(responseBytes);
        os.close();
    }


    // --------------------------------------------------
    // START WEB SERVER
    // --------------------------------------------------

    static void startServer() throws IOException {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );


        // --------------------------------------------------
        // GET OPPORTUNITIES
        // --------------------------------------------------

        server.createContext(
                "/api/opportunities",
                exchange -> {

                    exchange.getResponseHeaders().add(
                            "Access-Control-Allow-Origin",
                            "*"
                    );

                    exchange.getResponseHeaders().add(
                            "Access-Control-Allow-Methods",
                            "GET, POST, OPTIONS"
                    );

                    exchange.getResponseHeaders().add(
                            "Access-Control-Allow-Headers",
                            "Content-Type"
                    );


                    if ("OPTIONS".equals(
                            exchange.getRequestMethod())) {

                        exchange.sendResponseHeaders(
                                204,
                                -1
                        );

                        exchange.close();

                        return;
                    }


                    if ("GET".equals(
                            exchange.getRequestMethod())) {

                        StringBuilder json =
                                new StringBuilder();

                        json.append("[");

                        for (int i = 0;
                             i < opportunities.size();
                             i++) {

                            Opportunity opportunity =
                                    opportunities.get(i);

                            json.append("{");

                            json.append(
                                    "\"id\":"
                            ).append(
                                    opportunity.id
                            ).append(",");

                            json.append(
                                    "\"title\":\""
                            ).append(
                                    opportunity.title
                            ).append("\",");

                            json.append(
                                    "\"company\":\""
                            ).append(
                                    opportunity.company
                            ).append("\",");

                            json.append(
                                    "\"type\":\""
                            ).append(
                                    opportunity.type
                            ).append("\",");

                            json.append(
                                    "\"location\":\""
                            ).append(
                                    opportunity.location
                            ).append("\"");

                            json.append("}");


                            if (i <
                                opportunities.size() - 1) {

                                json.append(",");
                            }
                        }

                        json.append("]");


                        sendResponse(
                                exchange,
                                200,
                                json.toString()
                        );

                        return;
                    }


                    // --------------------------------------------------
                    // CREATE OPPORTUNITY FROM MY MANAGEMENT
                    // --------------------------------------------------

                    if ("POST".equals(
                            exchange.getRequestMethod())) {

                        String body =
                                new String(
                                        exchange.getRequestBody()
                                                .readAllBytes(),
                                        StandardCharsets.UTF_8
                                );


                        String title =
                                getValue(
                                        body,
                                        "title"
                                );

                        String company =
                                getValue(
                                        body,
                                        "company"
                                );

                        String type =
                                getValue(
                                        body,
                                        "type"
                                );

                        String location =
                                getValue(
                                        body,
                                        "location"
                                );


                        if (title.isEmpty() ||
                            company.isEmpty()) {

                            sendResponse(
                                    exchange,
                                    400,
                                    "Title and Company are required."
                            );

                            return;
                        }


                        Opportunity opportunity =
                                new Opportunity(
                                        opportunityId++,
                                        title,
                                        company,
                                        type,
                                        location
                                );

                        opportunities.add(
                                opportunity
                        );


                        System.out.println(
                                "\nNew opportunity created from My Management:"
                        );

                        opportunity.display();


                        sendResponse(
                                exchange,
                                200,
                                "Opportunity created successfully!"
                        );

                        return;
                    }


                    sendResponse(
                            exchange,
                            405,
                            "Method not allowed."
                    );
                }
        );


        // --------------------------------------------------
        // APPLICATIONS
        // --------------------------------------------------

        server.createContext(
                "/api/applications",
                exchange -> {

                    exchange.getResponseHeaders().add(
                            "Access-Control-Allow-Origin",
                            "*"
                    );

                    exchange.getResponseHeaders().add(
                            "Access-Control-Allow-Methods",
                            "POST, OPTIONS"
                    );

                    exchange.getResponseHeaders().add(
                            "Access-Control-Allow-Headers",
                            "Content-Type"
                    );


                    if ("OPTIONS".equals(
                            exchange.getRequestMethod())) {

                        exchange.sendResponseHeaders(
                                204,
                                -1
                        );

                        exchange.close();

                        return;
                    }


                    if ("POST".equals(
                            exchange.getRequestMethod())) {


                        String body =
                                new String(
                                        exchange.getRequestBody()
                                                .readAllBytes(),
                                        StandardCharsets.UTF_8
                                );


                        // ONLY OPPORTUNITY ID
                        String opportunityIdText =
                                getValue(
                                        body,
                                        "opportunityId"
                                );


                        if (opportunityIdText.isEmpty()) {

                            sendResponse(
                                    exchange,
                                    400,
                                    "Opportunity ID is required."
                            );

                            return;
                        }


                        int oppId;

                        try {

                            oppId =
                                    Integer.parseInt(
                                            opportunityIdText
                                    );

                        } catch (NumberFormatException e) {

                            sendResponse(
                                    exchange,
                                    400,
                                    "Invalid Opportunity ID."
                            );

                            return;
                        }


                        // FIND OPPORTUNITY
                        for (Opportunity opportunity :
                                opportunities) {

                            if (opportunity.id == oppId) {


                                Application application =
                                        new Application(
                                                applicationId++,
                                                oppId
                                        );


                                applications.add(
                                        application
                                );


                                System.out.println(
                                        "\nApplication submitted:"
                                );

                                application.display();


                                sendResponse(
                                        exchange,
                                        200,
                                        "Application submitted successfully!"
                                );

                                return;
                            }
                        }


                        sendResponse(
                                exchange,
                                404,
                                "Opportunity not found."
                        );

                        return;
                    }


                    sendResponse(
                            exchange,
                            405,
                            "Method not allowed."
                    );
                }
        );


        server.start();


        System.out.println(
                "\nCampusConnect server started!"
        );

        System.out.println(
                "http://localhost:8080"
        );
    }


    // --------------------------------------------------
    // MAIN
    // --------------------------------------------------

    public static void main(String[] args) {

        try {

            startServer();

        } catch (IOException e) {

            System.out.println(
                    "Server error: " + e.getMessage()
            );
        }


        while (true) {

            System.out.println(
                    "\n\n===== CAMPUSCONNECT ====="
            );

            System.out.println(
                    "1. Create Opportunity"
            );

            System.out.println(
                    "2. Display Opportunities"
            );

            System.out.println(
                    "3. Apply for Opportunity"
            );

            System.out.println(
                    "4. Display Applications"
            );

            System.out.println(
                    "5. Exit"
            );

            System.out.print(
                    "\nEnter your choice: "
            );


            int choice = sc.nextInt();

            sc.nextLine();


            switch (choice) {

                case 1:
                    createOpportunity();
                    break;

                case 2:
                    displayOpportunities();
                    break;

                case 3:
                    applyForOpportunity();
                    break;

                case 4:
                    displayApplications();
                    break;

                case 5:

                    System.out.println(
                            "Exiting CampusConnect..."
                    );

                    return;

                default:

                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }
}