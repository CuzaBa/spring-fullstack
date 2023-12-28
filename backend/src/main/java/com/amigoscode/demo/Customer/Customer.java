package com.amigoscode.demo.Customer;

import jakarta.persistence.*;

@Entity
@Table(name="customer" , uniqueConstraints = { @UniqueConstraint(
    name="customer_email_unique" , columnNames = "email"
)} )
public  class Customer {
    @Id
    @SequenceGenerator(
            name="customer_id_seq",
            sequenceName="customer_id_seq",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Integer age;

    public Customer() {
    }
    public Customer(Integer id, String name, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Customer(String name, String email, Integer age) {

        this.name = name;
        this.email = email;
        this.age = age;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }
}
