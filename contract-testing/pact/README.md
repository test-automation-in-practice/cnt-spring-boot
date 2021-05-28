# Showcase: Contract Tests with PACT, JUnit 5 and Spring Boot
Showcase demonstrating how PACT can be used to decouple a JVM consumer /
provider pairs during testing.

Contains three Gradle projects:

1. `provider` - a simple Library Service
2. `consumer-one` - a consumer of the Library Service interested in a books's
`isbn`, `title` and `authors` attributes
3. `consumer-two` - a consumer of the Library Service interested in a books's
`isbn`, `title` and `numberOfPages` attributes

None of the consumers is interested in a books's `description` attribute.
To demonstrate the advantages contract testing provides, the `description` can
be deleted from the provider's `Book` definition without any of the consumers
suffering any consequences. If any of the other attributes is changed at least
one, if not both, of the consumers will break.
