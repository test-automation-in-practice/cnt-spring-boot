rootProject.name = "ws-cloud-native-testing"

include("contract-testing:pact:consumer-one")
include("contract-testing:pact:consumer-two")
include("contract-testing:pact:provider")

include("contract-testing:spring-cloud-contract:consumer-one")
include("contract-testing:spring-cloud-contract:consumer-two")
include("contract-testing:spring-cloud-contract:provider")

include("spring-boot:spring-boot-amqp")
include("spring-boot:spring-boot-cache")
include("spring-boot:spring-boot-feign")
include("spring-boot:spring-boot-jdbc")
include("spring-boot:spring-boot-jpa")
include("spring-boot:spring-boot-mongodb")
include("spring-boot:spring-boot-security")
include("spring-boot:spring-boot-starter")
include("spring-boot:spring-boot-web")
