package com.cst.ict.model;

import java.io.Serializable;

public class Person implements Serializable {
    private final int departmentNumber;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String emailAddress;


    public Person(int departmentNumber, String firstName, String lastName, String phoneNumber, String emailAddress) {
        this.departmentNumber = departmentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }


    public int getDepartmentNumber() {
        return departmentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public String toString() {
        return "Person{" +
                "departmentNumber=" + departmentNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}