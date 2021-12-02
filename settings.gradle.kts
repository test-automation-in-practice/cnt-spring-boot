rootProject.name = "ws-cloud-native-testing"

include("contract-testing:pact:consumer-one")
include("contract-testing:pact:consumer-two")
include("contract-testing:pact:provider")

include("contract-testing:spring-cloud-contract:consumer-one")
include("contract-testing:spring-cloud-contract:consumer-two")
include("contract-testing:spring-cloud-contract:provider")

include("spring-boot:basics")
include("spring-boot:caching")
include("spring-boot:data-jpa")
include("spring-boot:data-mongodb")
include("spring-boot:http-clients")
include("spring-boot:jdbc")
include("spring-boot:messaging-rabbitmq")
include("spring-boot:security")
include("spring-boot:webflux")
include("spring-boot:webmvc")
