package example.spring.boot.advanced.scoping

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import example.spring.boot.advanced.scoping.api.MediaCollectionController.MediaRegistration
import example.spring.boot.advanced.scoping.domain.Book
import example.spring.boot.advanced.scoping.domain.Game
import example.spring.boot.advanced.scoping.domain.TypeOfMedia
import example.spring.boot.advanced.scoping.domain.TypeOfMedia.BOOK
import example.spring.boot.advanced.scoping.domain.TypeOfMedia.GAME
import java.util.UUID

object Examples {

    private val objectMapper = jacksonObjectMapper()

    val book_bobiverse1_id = UUID.fromString("b3fc0be8-463e-4875-9629-67921a1e00f4")
    val book_bobiverse1_label = "We Are Legion (We Are Bob)"
    val book_bobiverse1 = Book(book_bobiverse1_id, book_bobiverse1_label)
    val book_bobiverse1_registration = MediaRegistration(BOOK, book_bobiverse1_id, book_bobiverse1_label)
    val book_bobiverse1_registration_json = registrationJson(BOOK, book_bobiverse1_id, book_bobiverse1_label)

    val book_bobiverse2_id = UUID.fromString("aadfe61a-4bbd-44c4-85ba-6bddbe7d10a2")
    val book_bobiverse2_label = "For We Are Many"
    val book_bobiverse2 = Book(book_bobiverse2_id, book_bobiverse2_label)
    val book_bobiverse2_registration = MediaRegistration(BOOK, book_bobiverse2_id, book_bobiverse2_label)
    val book_bobiverse2_registration_json = registrationJson(BOOK, book_bobiverse2_id, book_bobiverse2_label)

    val book_bobiverse3_id = UUID.fromString("02f33cf8-a8a4-4e94-b44e-21c5c0603dd7")
    val book_bobiverse3_label = "All These Worlds"
    val book_bobiverse3 = Book(book_bobiverse3_id, book_bobiverse3_label)
    val book_bobiverse3_registration = MediaRegistration(BOOK, book_bobiverse3_id, book_bobiverse3_label)
    val book_bobiverse3_registration_json = registrationJson(BOOK, book_bobiverse3_id, book_bobiverse3_label)

    val game_eldenring_id = UUID.fromString("0c29c054-69b6-4544-8772-b2da82691cdf")
    val game_eldenring_label = "Elden Ring"
    val game_eldenring = Game(game_eldenring_id, game_eldenring_label)
    val book_eldenring_registration = MediaRegistration(GAME, game_eldenring_id, game_eldenring_label)
    val game_eldenring_registration_json = registrationJson(GAME, game_eldenring_id, game_eldenring_label)

    fun bookRegistrationJson(id: UUID = book_bobiverse1_id, label: String = book_bobiverse1_label) =
        registrationJson(BOOK, id, label)

    fun gameRegistrationJson(id: UUID = game_eldenring_id, label: String = game_eldenring_label) =
        registrationJson(GAME, id, label)

    fun registrationJson(type: TypeOfMedia, id: UUID, label: String) =
        registrationJson(MediaRegistration(type, id, label))

    fun registrationJson(registration: MediaRegistration) =
        objectMapper.writeValueAsString(registration)

    fun stringOfLength(length: Int): String =
        (1..length).map { 'a' }.joinToString(separator = "")
            .also { check(it.length == length) }
}
