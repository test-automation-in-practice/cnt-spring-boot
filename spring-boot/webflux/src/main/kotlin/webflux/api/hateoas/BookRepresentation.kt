package webflux.api.hateoas

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import webflux.business.BookRecord

@Relation(value = "book", collectionRelation = "books")
data class BookRepresentation(
    val title: String,
    val isbn: String
) : RepresentationModel<BookRepresentation>()

@Component
class BookRepresentationAssembler : ReactiveRepresentationModelAssembler<BookRecord, BookRepresentation> {

    private val controller: Class<HateoasBookController> = HateoasBookController::class.java

    override fun toModel(entity: BookRecord, exchange: ServerWebExchange): Mono<BookRepresentation> {
        val representation = BookRepresentation(
            title = entity.book.title.toString(),
            isbn = entity.book.isbn.toString()
        )
        return linkTo(methodOn(controller).getById(exchange, entity.id)).withSelfRel().toMono()
            .map { selfLink -> representation.add(selfLink) }
    }
}
