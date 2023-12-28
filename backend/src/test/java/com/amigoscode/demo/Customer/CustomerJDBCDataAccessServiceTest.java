package com.amigoscode.demo.Customer;

import com.amigoscode.demo.AbstractTestcontainers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.org.apache.commons.lang3.CharSequenceUtils;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {
    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp(){
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer  = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() +"-"+ UUID.randomUUID(),
                20);
        underTest.insertCustomer(customer);
        // When
        List<Customer> actual = underTest.selectAllCustomers();
        // Then
        Assertions.assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer  = new Customer(
                FAKER.name().fullName(),
                email,
                20);
        underTest.insertCustomer(customer);
        int id= underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        Assertions.assertThat(actual).isPresent().hasValueSatisfying(c -> {
                Assertions.assertThat(c.getId()).isEqualTo(id);
                Assertions.assertThat(c.getName()).isEqualTo(customer.getName());
                Assertions.assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                Assertions.assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        // -1 para no meter un id que podia estar ya en algun test de los otros
        int id =-1;
        // When
        var actual = underTest.selectCustomerById(id);
        // Then
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given
        // When
        // Then
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress()+ "-" + UUID.randomUUID();
        String name =FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        // When
        boolean actual = underTest.existsPersonWithEmail(email);
        // Then
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = FAKER.internet().safeEmailAddress()+ "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsPersonWithEmail(email);
        // Then
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress()+ "-" + UUID.randomUUID();
        Customer customer= new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        // When
        boolean actual = underTest.existsPersonWithId(id);
        // Then
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdReturnFalseWhenIdNotPresent() {
        // Given
        int id = -1;
        // When
        var actual = underTest.existsPersonWithId(id);
        // Then
        Assertions.assertThat(actual).isFalse();
    }


    @Test
    void deleteCustomer() {
        // Given
        String email= FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id =underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteCustomerById(id);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        Assertions.assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Given
        String email= FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName ="foo";

        // When age is name
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        Assertions.assertThat(actual).isPresent().hasValueSatisfying(c ->{
            Assertions.assertThat(c.getId()).isEqualTo(id);
            Assertions.assertThat(c.getName()).isEqualTo(newName);
            Assertions.assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            Assertions.assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }


    @Test
    void updateCustomerEmail() {
        // Given
        String email= FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        var newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When email is changed
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        Assertions.assertThat(actual).isPresent().hasValueSatisfying(c ->{
            Assertions.assertThat(c.getId()).isEqualTo(id);
            Assertions.assertThat(c.getEmail()).isEqualTo(newEmail);
            Assertions.assertThat(c.getName()).isEqualTo(customer.getName());
            Assertions.assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String email= FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newAge = 55;
        // When age is changed
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        Assertions.assertThat(actual).isPresent().hasValueSatisfying(c ->{
            Assertions.assertThat(c.getId()).isEqualTo(id);
            Assertions.assertThat(c.getAge()).isEqualTo(newAge);
            Assertions.assertThat(c.getName()).isEqualTo(customer.getName());
            Assertions.assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });

    }

    @Test
    void willUpdateAllPropertiesCustomer() {
       String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
       Customer customer = new Customer(
         FAKER.name().fullName(),
         email,
         20
       );

       underTest.insertCustomer(customer);

       int id = underTest.selectAllCustomers()
               .stream()
               .filter(c -> c.getEmail().equals(email))
               .map(Customer::getId)
               .findFirst()
               .orElseThrow();
       // When update with new name, age and email
        Customer update  = new Customer();
        update.setId(id);
        update.setName("foo");
        update.setEmail(UUID.randomUUID().toString());
        update.setAge(22);

        underTest.updateCustomer(update);
        // Then
        Optional <Customer> actual = underTest.selectCustomerById(id);
        //Assertions.assertThat(actual).isPresent().hasValue(update);

        Assertions.assertThat(actual).hasValueSatisfying(c -> {
            Assertions.assertThat(c.getId()).isEqualTo(update.getId());
            Assertions.assertThat(c.getAge()).isEqualTo(update.getAge());
            Assertions.assertThat(c.getEmail()).isEqualTo(update.getEmail());
            Assertions.assertThat(c.getName()).isEqualTo(update.getName());
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
       // Given
       String email = FAKER.internet().safeEmailAddress()+ "-" + UUID.randomUUID();
       Customer customer= new Customer(
            FAKER.name().fullName(),
            email,
            20

       );

       underTest.insertCustomer(customer);

       int id = underTest.selectAllCustomers()
               .stream()
               .filter(c -> c.getEmail().equals(email))
               .map(Customer::getId)
               .findFirst()
               .orElseThrow();

       // Whenn update without no changes
        Customer  update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual= underTest.selectCustomerById(id);

        Assertions.assertThat(actual).hasValueSatisfying(c -> {
            Assertions.assertThat(c.getId()).isEqualTo(id);
            Assertions.assertThat(c.getAge()).isEqualTo(customer.getAge());
            Assertions.assertThat(c.getName()).isEqualTo(customer.getName());
            Assertions.assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });


    }
    
}