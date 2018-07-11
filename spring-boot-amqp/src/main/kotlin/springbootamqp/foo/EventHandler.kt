package springbootamqp.foo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EventHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    fun handleCreatedEvent(event: FooCreated) {
        log.info("Foo [${event.id}] was created!")
    }

    fun handleDeletedEvent(event: FooDeleted) {
        log.info("Foo [${event.id}] was deleted!")
    }

}