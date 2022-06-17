rootProject.name = "ws-cloud-native-testing"

include("contract-testing:pact:http:consumer-one")
include("contract-testing:pact:http:consumer-two")
include("contract-testing:pact:http:provider")

include("contract-testing:pact:messaging:consumer")
include("contract-testing:pact:messaging:provider")

include("contract-testing:spring-cloud-contract:http:consumer-one")
include("contract-testing:spring-cloud-contract:http:consumer-two")
include("contract-testing:spring-cloud-contract:http:provider")

include("contract-testing:spring-cloud-contract:messaging:consumer")
include("contract-testing:spring-cloud-contract:messaging:provider")

include("spring-boot:advanced:end-to-end")
include("spring-boot:advanced:scoping")
include("spring-boot:basics")
include("spring-boot:caching")
include("spring-boot:data-jdbc")
include("spring-boot:data-jpa")
include("spring-boot:data-mongodb")
include("spring-boot:data-redis")
include("spring-boot:graphql")
include("spring-boot:http-clients")
include("spring-boot:jdbc")
include("spring-boot:messaging-jms")
include("spring-boot:messaging-kafka")
include("spring-boot:messaging-rabbitmq")
include("spring-boot:security")
include("spring-boot:webflux")
include("spring-boot:webmvc")
