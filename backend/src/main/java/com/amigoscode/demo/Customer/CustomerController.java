package com.amigoscode.demo.Customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;
    public CustomerController(CustomerService customerService){
        this.customerService=customerService;
    }
    /*@GetMapping("/greet")
    public GreetResponse greet(@RequestParam(value = "name", required = false) String name) {
        String greetMessage = name == null || name.isBlank() ? "Hello" : "Hello " + name;
        return new GreetResponse(greetMessage, List.of("GoLang", "Javascript"), new Person("Alex", 28, 30_000));
    }*/


    //@RequestMapping(path="api/v1/customer" , method = RequestMethod.GET)
    @GetMapping
    public List<Customer> getCustomers() {

        return customerService.getAllCustomers();
    }



    @GetMapping("{id}")
    public Customer getCustomersById(@PathVariable("id") int id) {
        return customerService.getCustomer(id);
    }
    /*
        When the client send the post request they have to include json
       in the request body,in order to map the request body(json) to the
       CustomerRegistrationRequest we use @RequestBody annotation
    *
    */
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
    }
    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer customerId){
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer customerId,
                                @RequestBody CustomerUpdateRequest updateRequest){
        customerService.updateCustomer(customerId, updateRequest);

    }


    record Person(String name, int age, double savings) {
    }
    record GreetResponse(String greet, List<String> favProgrammingLanguages, Person person) {
    }
}
