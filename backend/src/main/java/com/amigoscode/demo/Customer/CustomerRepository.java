package com.amigoscode.demo.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {

    // JPQL : SOLO ESCRIBES EL METODO Y JPA SE ENCARGA
    boolean existsCustomerByEmail(String email);

    boolean existsCustomerById(Integer id);
}
