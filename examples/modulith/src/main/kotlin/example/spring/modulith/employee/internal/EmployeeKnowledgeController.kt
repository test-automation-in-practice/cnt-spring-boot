package example.spring.modulith.employee.internal

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/employees/{employeeId}/knowledge/{skillId}")
class EmployeeKnowledgeController(
    private val service: EmployeeKnowledgeService
) {

    @PutMapping
    fun setKnowledge(
        @PathVariable employeeId: UUID,
        @PathVariable skillId: UUID,
        @RequestBody body: SetRequest
    ): ResponseEntity<EmployeeRepresentation> =
        when (val employee = service.setKnowledge(employeeId, skillId, body.level)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(employee.toRepresentation())
        }

    data class SetRequest(
        val level: Int
    )

    @DeleteMapping
    fun deleteKnowledge(
        @PathVariable employeeId: UUID,
        @PathVariable skillId: UUID
    ): ResponseEntity<EmployeeRepresentation> =
        when (val employee = service.deleteKnowledge(employeeId, skillId)) {
            null -> ResponseEntity.notFound().build()
            else -> ResponseEntity.ok(employee.toRepresentation())
        }

}
