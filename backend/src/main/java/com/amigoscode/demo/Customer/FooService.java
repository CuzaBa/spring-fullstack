package com.amigoscode.demo.Customer;

import org.springframework.stereotype.Service;
@Service
public class FooService {

    public Main.Foo foo;

    public FooService(Main.Foo foo){
        this.foo=foo;
        System.out.println("foo = " + foo);
    }
}
