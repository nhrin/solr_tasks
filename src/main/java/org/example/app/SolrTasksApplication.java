package org.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.example")
public class SolrTasksApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolrTasksApplication.class, args);
    }
}