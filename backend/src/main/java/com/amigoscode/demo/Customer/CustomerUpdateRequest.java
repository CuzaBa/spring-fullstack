package com.amigoscode.demo.Customer;

public record CustomerUpdateRequest( String name,
                                     String email,
                                     Integer age) {
}
