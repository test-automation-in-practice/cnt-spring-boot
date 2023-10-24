package example.spring.modulith.skill.internal

import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/skills")
class SkillController(
    private val service: SkillService
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@RequestBody body: CreateRequest): SkillRepresentation {
        val data = body.toSkillData()
        val skill = service.create(data)
        return skill.toRepresentation()
    }

    data class CreateRequest(
        val label: String,
    ) {
        fun toSkillData() = Skill.Data(label = label)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<SkillRepresentation> =
        when (val skill = service.get(id)) {
            null -> noContent().build()
            else -> ok(skill.toRepresentation())
        }

    @PutMapping("/{id}")
    fun put(
        @PathVariable id: UUID,
        @RequestBody body: UpdateRequest
    ): ResponseEntity<SkillRepresentation> {
        val skill = service.updateData(id) { currentData ->
            currentData.copy(label = body.label)
        }
        return when (skill) {
            null -> notFound().build()
            else -> ok(skill.toRepresentation())
        }
    }

    data class UpdateRequest(
        val label: String,
    )

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        service.delete(id)
    }

}
