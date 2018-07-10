package web.api

import org.slf4j.LoggerFactory
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import web.business.Foo
import web.business.FooNotFoundException
import web.business.FooService
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/foos")
class FooController(
        private val service: FooService,
        private val resourceAssembler: FooResourceAssembler
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun get(): Resources<FooResource> {
        val resources = resourceAssembler.toResources(service.getAll())
        val links = listOf(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(javaClass).get()).withSelfRel())
        return Resources(resources, links)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@Valid @RequestBody request: CreateFooRequest): FooResource {
        val foo = Foo(
                bar = request.bar,
                xur = request.xur
        )
        val persistedFoo = service.create(foo)
        return resourceAssembler.toResource(persistedFoo)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: UUID): FooResource {
        val persistedFoo = service.get(id)
        return resourceAssembler.toResource(persistedFoo)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: UUID) {
        service.delete(id)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FooNotFoundException::class)
    fun handleNotFoundException(e: FooNotFoundException) {
        log.debug("Could not find Foo [${e.id}], responding with '404 Not Found'.")
    }

}