package com.amigoscode.demo.Customer;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {


    public static void main(String[] args) {


        ConfigurableApplicationContext appContext =  SpringApplication.run(DemoApplication.class, args);
        // print all the beans available
        printBeans(appContext);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository ){
        return args ->{
            var faker= new Faker();
            var name =faker.name();
            Random random = new Random();
            Customer customer = new Customer(
                    name.fullName(),
                    faker.internet().emailAddress(),
                    random.nextInt(16,99)

            );
           customerRepository.save(customer);
        };
    }

    private static void printBeans(ConfigurableApplicationContext ctx){
        String[] beansDefinitionNames = ctx.getBeanDefinitionNames();
        for (String beanName :beansDefinitionNames){
          //  System.out.println(beanName);

        }
    }

    @Bean
    public Foo getFoo(){
        return new Foo("bar");
    }
    public record Foo(String name){}
}
