package com.amigoscode.demo.Customer;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {


    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();

    @Test
    void canRegisterACustomer(){
        // Create registration request
        Faker faker =  new Faker();
        Name fakerName = faker.name();
        String name = faker.name().fullName();
        String email = fakerName.lastName() + "-"+ UUID.randomUUID()+"@amogiscode.com";
        int age = RANDOM.nextInt(1,100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email,age
        );


        // send a post request
        String customerPostUri= "/api/v1/customer";

        webTestClient.post()
                .uri(customerPostUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request) , CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers=  webTestClient.get().uri(customerPostUri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();
        // make sure that customer is present
        Customer expectedCustomer =  new Customer(name,email,age);
        // usingRecursiveFieldByFieldElementComparatorIgnoringFields to ingnoe the id when we compare with the assert
       Assertions.assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        int id =allCustomers.stream().filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();



        expectedCustomer.setId(id);
        // get customer by id
          /*webTestClient.get().uri(customerPostUri+"/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                  .isEqualTo(expectedCustomer);*/




         Customer  customerResult = webTestClient.get().uri(customerPostUri + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(customerResult.getId()).isEqualTo(expectedCustomer.getId());
        Assertions.assertThat(customerResult.getName()).isEqualTo(expectedCustomer.getName());
        Assertions.assertThat(customerResult.getEmail()).isEqualTo(expectedCustomer.getEmail());
        Assertions.assertThat(customerResult.getAge()).isEqualTo(expectedCustomer.getAge());
    }


    @Test
    void canDeleteACustomer(){
        // Create registration request
        Faker faker =  new Faker();
        Name fakerName = faker.name();
        String name = faker.name().fullName();
        String email = fakerName.lastName() + "-"+ UUID.randomUUID()+"@amogiscode.com";
        int age = RANDOM.nextInt(1,100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email,age
        );


        // send a post request
        String customerPostUri= "/api/v1/customer";

        webTestClient.post()
                .uri(customerPostUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request) , CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers=  webTestClient.get().uri(customerPostUri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        int customerId =allCustomers.stream().filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        // delete the customer
        webTestClient.delete()
                .uri(customerPostUri+"/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // check if customer is deleted
        webTestClient.get().uri(customerPostUri+"/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @Test
    void canUpdateCustomer(){


    // Create registration request
    Faker faker =  new Faker();
    Name fakerName = faker.name();
    String name = faker.name().fullName();
    String email = fakerName.lastName() + "-"+ UUID.randomUUID()+"@amogiscode.com";
    int age = RANDOM.nextInt(1,100);

    CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            name, email,age
    );


    // send a post request
    String customerPostUri= "/api/v1/customer";

    webTestClient.post()
                .uri(customerPostUri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request) , CustomerRegistrationRequest.class)
            .exchange()
                .expectStatus()
                .isOk();
    // get all customers
    List<Customer> allCustomers=  webTestClient.get().uri(customerPostUri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(new ParameterizedTypeReference<Customer>() {})
            .returnResult()
            .getResponseBody();

    int customerId =allCustomers.stream().filter(customer -> customer.getEmail().equals(email))
            .map(Customer::getId)
            .findFirst()
            .orElseThrow();

    // update the customer
        String newName= "Ali";
        CustomerUpdateRequest updateRequest =  new CustomerUpdateRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(customerPostUri+"/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

    // get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(customerPostUri + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer( customerId , newName,email, age);

        Assertions.assertThat(updatedCustomer.getId()).isEqualTo(expected.getId());
        Assertions.assertThat(updatedCustomer.getName()).isEqualTo(expected.getName());
        Assertions.assertThat(updatedCustomer.getAge()).isEqualTo(expected.getAge());
        Assertions.assertThat(updatedCustomer.getEmail()).isEqualTo(expected.getEmail());
    }





}
