> I collect more resources about the general topic of contract testing
[here](https://github.com/slu-it/learning-contract-testing).

# Showcase: PACT JVM-Consumers with JVM-Provider
Showcase demonstrating how PACT can be used to decouple a JVM consumer /
provider pairs during testing.

Contains three Gradle projects:

1. `provider` - a movie database with a set of pre-defined Batman movies
2. `consumer-one` - a consumer of the movie database interested in a movie's
`title` and `imdbScore` attributes
3. `consumer-two` - a consumer of the movie database interested in a movie's
`title`, `releaseYear` and `metacriticScore`attributes

None of the consumers is interested in a movie's `description` attribute.
To demonstrate the advantages contract testing provides, the description can
be deleted from the provider's movie definition without any of the consumers
suffering any consequences. If any of the other attributes is changed at least
one, if not both, of the consumers will break.
