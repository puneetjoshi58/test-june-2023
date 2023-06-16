package com.helloworld.helloworld;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.dispatcher.JavaDispatcher.Container;

@SpringBootTest
@Testcontainers
public class DatabaseTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:latest")
                    .withDatabaseName("test")
                    .withUsername("root")
                    .withPassword("password");

    @Autowired
    private CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @BeforeAll
    static void setup() {
        mysqlContainer.start();
    }

    @Test
    @Transactional
    public void testDatabaseConnection() {
        // Insert a customer
        Customer customer = new Customer();
        customer.setName("Puneet Joshi");
        customer.setId(1L);
        customerRepository.save(customer);

        // Retrieve the customer
        Customer savedCustomer = entityManager.find(Customer.class, 1L);
        System.out.println(savedCustomer.getName()); // Print the customer's name
    }
}
    

