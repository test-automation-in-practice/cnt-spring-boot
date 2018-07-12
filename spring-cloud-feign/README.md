# Showcase: Testing Feign Clients with Ribbon Load Balancing and Hystrix Circuit Breakers
Showcase demonstrating Spring Cloud Feign clients can be tested using JUnit 5,
WireMock and Spring Boot's test support. The challenge in this is how to direct
Ribbon to WireMock with dynamic ports, and how to test and reset Hystix circuit
breakers.
