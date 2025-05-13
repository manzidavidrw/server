package com.cst.ict.service;

import com.cst.ict.model.Person;
import com.cst.ict.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TCPServerService implements CommandLineRunner {

    @Value("${server.tcp.port:8090}")
    private int port;

    private final PersonRepository personRepository;

    @Autowired
    public TCPServerService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                executorService.execute(new ClientHandler(socket, personRepository));
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final PersonRepository personRepository;

        public ClientHandler(Socket socket, PersonRepository personRepository) {
            this.clientSocket = socket;
            this.personRepository = personRepository;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received request: " + inputLine);
                    String response = processRequest(inputLine);
                    out.println(response);
                }
            } catch (Exception e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private String processRequest(String request) {
            String[] parts = request.split("\\|");
            String requestType = parts[0];

            switch (requestType) {
                case "GET_ALL":
                    return getAllPersons();

                case "GET_BY_DEPT":
                    if (parts.length < 2) {
                        return "ERROR: Department number required";
                    }
                    try {
                        int deptNumber = Integer.parseInt(parts[1]);
                        return getPersonsByDepartment(deptNumber);
                    } catch (NumberFormatException e) {
                        return "ERROR: Invalid department number";
                    }

                case "GET_EMAIL":
                    if (parts.length < 3) {
                        return "ERROR: Insufficient parameters";
                    }

                    String secondArgs = parts[2];
                    try {
                        int deptNumber = Integer.parseInt(secondArgs);
                        String lastName = parts[1];
                        return getEmailByLastNameAndDepartment(lastName, deptNumber);
                    } catch (NumberFormatException e) {
                        String firstName = parts[1];
                        String lastName = parts[2];
                        return getEmailByFirstNameAndLastName(firstName, lastName);
                    }

                case "GET_PHONE":
                    if (parts.length < 3) {
                        return "ERROR: Insufficient parameters";
                    }

                    String secondArg = parts[2];

                    // Check if secondArg is an integer (i.e., DeptNumber)
                    try {
                        int deptNumber = Integer.parseInt(secondArg);
                        String lastName = parts[1];
                        return getPhoneByLastNameAndDepartment(lastName, deptNumber);
                    } catch (NumberFormatException e) {
                        // Not an integer, treat as FirstName + LastName
                        String firstName = parts[1];
                        String lastName = parts[2];
                        return getPhoneByFirstNameAndLastName(firstName, lastName);
                    }


                default:
                    return "ERROR: Unknown request type";
            }
        }

        private String getAllPersons() {
            List<Person> persons = personRepository.findAll();
            return formatPersonList(persons);
        }

        private String getPersonsByDepartment(int deptNumber) {
            List<Person> persons = personRepository.findByDepartmentNumber(deptNumber);
            if (persons.isEmpty()) {
                return "ERROR: No persons found in department " + deptNumber;
            }
            return formatPersonList(persons);
        }

        private String getEmailByFirstNameAndLastName(String firstName, String lastName) {
            Optional<Person> personOpt = personRepository.findByFirstNameAndLastName(firstName, lastName);
            if (personOpt.isPresent()) {
                return "EMAIL: " + personOpt.get().getEmailAddress();
            } else {
                return "ERROR: Person not found with name " + firstName + " " + lastName;
            }
        }

        private String getEmailByLastNameAndDepartment(String lastName, int deptNumber) {
            Optional<Person> personOpt = personRepository.findByLastNameAndDepartmentNumber(lastName, deptNumber);
            if (personOpt.isPresent()) {
                return "EMAIL: " + personOpt.get().getEmailAddress();
            } else {
                return "ERROR: Person not found with last name " + lastName + " in department " + deptNumber;
            }
        }

        private String getPhoneByFirstNameAndLastName(String firstName, String lastName) {
            Optional<Person> personOpt = personRepository.findByFirstNameAndLastName(firstName, lastName);
            if (personOpt.isPresent()) {
                return "PHONE: " + personOpt.get().getPhoneNumber();
            } else {
                return "ERROR: Person not found with name " + firstName + " " + lastName;
            }
        }


        private String getPhoneByLastNameAndDepartment(String lastName, int deptNumber) {
            Optional<Person> personOpt = personRepository.findByLastNameAndDepartmentNumber(lastName, deptNumber);
            if (personOpt.isPresent()) {
                return "PHONE: " + personOpt.get().getPhoneNumber();
            } else {
                return "ERROR: Person not found with last name " + lastName + " in department " + deptNumber;
            }
        }

        private String formatPersonList(List<Person> persons) {
            if (persons.isEmpty()) {
                return "No persons found";
            }

            StringBuilder sb = new StringBuilder();
            for (Person person : persons) {
                sb.append(person.getDepartmentNumber()).append(",")
                        .append(person.getFirstName()).append(",")
                        .append(person.getLastName()).append(",")
                        .append(person.getPhoneNumber()).append(",")
                        .append(person.getEmailAddress()).append(";");
            }
            return sb.toString();
        }
    }
}