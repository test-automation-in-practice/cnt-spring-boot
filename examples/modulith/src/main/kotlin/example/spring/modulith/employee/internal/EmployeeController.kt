package example.spring.modulith.employee.internal

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
@RequestMapping("/api/employees")
class EmployeeController(
    private val service: EmployeeService
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@RequestBody body: CreateRequest): EmployeeRepresentation {
        val data = body.toEmployeeData()
        val employee = service.create(data)
        return employee.toRepresentation()
    }

    data class CreateRequest(
        val firstName: String,
        val lastName: String,
    ) {
        fun toEmployeeData() = Employee.Data(firstName = firstName, lastName = lastName)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<EmployeeRepresentation> =
        when (val employee = service.get(id)) {
            null -> noContent().build()
            else -> ok(employee.toRepresentation())
        }

    @PutMapping("/{id}")
    fun put(
        @PathVariable id: UUID,
        @RequestBody body: UpdateRequest
    ): ResponseEntity<EmployeeRepresentation> {
        val employee = service.updateData(id) { currentData ->
            currentData.copy(firstName = body.firstName, lastName = body.lastName)
        }
        return when (employee) {
            null -> notFound().build()
            else -> ok(employee.toRepresentation())
        }
    }

    data class UpdateRequest(
        val firstName: String,
        val lastName: String,
    )

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        service.delete(id)
    }

}
