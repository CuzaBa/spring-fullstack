package com.amigoscode.demo.Customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
