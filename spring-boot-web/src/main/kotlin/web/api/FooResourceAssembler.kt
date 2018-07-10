package web.api

import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component
import web.business.PersistedFoo

@Component
class FooResourceAssembler
    : ResourceAssemblerSupport<PersistedFoo, FooResource>(FooController::class.java, FooResource::class.java) {

    override fun toResource(entity: PersistedFoo): FooResource = createResourceWithId(entity.id, entity)

    override fun instantiateResource(persistedFoo: PersistedFoo) = FooResource(
            bar = persistedFoo.data.bar,
            xur = persistedFoo.data.xur
    )

}