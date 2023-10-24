package example.spring.modulith

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import org.springframework.modulith.docs.Documenter.CanvasOptions
import org.springframework.modulith.docs.Documenter.DiagramOptions
import org.springframework.modulith.docs.Documenter.DiagramOptions.DiagramStyle.C4
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships.VISIBLE

class ArchitectureTests {

    private val modules = ApplicationModules.of(Application::class.java)
    private val documenter = Documenter(modules)

    @Test
    fun `verify modular structure`() {
        modules.verify()
    }

    @Test
    fun `document modular structure`() {
        val diagramOptions = DiagramOptions.defaults()
            .withStyle(C4)
            .withElementsWithoutRelationships(VISIBLE)
        val canvasOptions = CanvasOptions.defaults()
            .revealInternals()
        documenter.writeDocumentation(diagramOptions, canvasOptions)
    }

}
