package com.amigoscode.demo.journey;

import com.amigoscode.demo.Customer.Customer;
import com.amigoscode.demo.Customer.CustomerRegistrationRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;
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

       Customer expectedCustomer =  new Customer(name,email,age);

        Assertions.assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);
        // make sure that customer is present
        // get customer by id
    }

}
