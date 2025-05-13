package com.cst.ict.repository;

import com.cst.ict.model.Person;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PersonRepository {
    private List<Person> database = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        database.add(new Person(1, "John", "Doe", "555-1234", "john.doe@cst.edu"));
        database.add(new Person(1, "Jane", "Smith", "555-5678", "jane.smith@cst.edu"));
        database.add(new Person(2, "Alice", "Johnson", "555-9012", "alice.johnson@cst.edu"));
        database.add(new Person(2, "Bob", "Brown", "555-3456", "bob.brown@cst.edu"));
        database.add(new Person(3, "Charlie", "Davis", "555-7890", "charlie.davis@cst.edu"));
        database.add(new Person(3, "Diana", "Wilson", "555-2345", "diana.wilson@cst.edu"));
        database.add(new Person (4,"Manzi","David","555-5555","manzi.david@cst.edu"));
        database.add(new Person (4,"sano","Ethan","555-7655","manzi.david@cst.edu"));

    }

    public List<Person> findAll() {
        return new ArrayList<>(database);
    }

    public List<Person> findByDepartmentNumber(int departmentNumber) {
        return database.stream()
                .filter(person -> person.getDepartmentNumber() == departmentNumber)
                .collect(Collectors.toList());
    }

    public Optional<Person> findByFirstNameAndLastName(String firstName, String lastName) {
        return database.stream()
                .filter(person -> person.getFirstName().equalsIgnoreCase(firstName)
                        && person.getLastName().equalsIgnoreCase(lastName))
                .findFirst();
    }

    public Optional<Person> findByLastNameAndDepartmentNumber(String lastName, int departmentNumber) {
        return database.stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName)
                        && person.getDepartmentNumber() == departmentNumber)
                .findFirst();
    }
}