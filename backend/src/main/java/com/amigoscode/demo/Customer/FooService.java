package com.amigoscode.demo.Customer;

import org.springframework.stereotype.Service;

@Service
public class FooService {

    public DemoApplication.Foo foo;

    public FooService(DemoApplication.Foo foo){
        this.foo=foo;
        System.out.println("foo = " + foo);
    }
}
