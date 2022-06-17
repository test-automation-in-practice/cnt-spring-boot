package example.spring.boot.advanced.scoping.api

import example.spring.boot.advanced.scoping.domain.Book
import example.spring.boot.advanced.scoping.domain.Game
import example.spring.boot.advanced.scoping.domain.MediaCollection
import example.spring.boot.advanced.scoping.domain.TypeOfMedia
import example.spring.boot.advanced.scoping.domain.TypeOfMedia.BOOK
import example.spring.boot.advanced.scoping.domain.TypeOfMedia.GAME
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
@RequestMapping("/api/media")
class MediaCollectionController(
    private val collection: MediaCollection
) {

    private val log = getLogger(javaClass)

    @PostMapping
    @ResponseStatus(ACCEPTED)
    fun register(@Valid @RequestBody registration: MediaRegistration) {
        log.debug("received registration of new media item: $registration")
        val item = createMediaItem(registration)
        collection.register(item)
    }

    private fun createMediaItem(registration: MediaRegistration) =
        when (registration.type) {
            BOOK -> Book(registration.id, registration.label)
            GAME -> Game(registration.id, registration.label)
        }

    data class MediaRegistration(
        val type: TypeOfMedia,
        val id: UUID,
        @field:Size(max = 100) val label: String
    )

}
