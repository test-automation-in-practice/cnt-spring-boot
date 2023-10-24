package example.spring.modulith.employee.internal

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.IdGenerator
import java.time.Clock
import java.util.UUID

@Service
@Transactional
class EmployeeService(
    private val repository: EmployeeRepository,
    private val publisher: EmployeeEventPublisher,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    fun get(id: UUID): Employee? =
        repository.findByIdOrNull(id)

    fun create(data: Employee.Data): Employee {
        val id = idGenerator.generateId()
        val now = clock.instant()

        val employee = Employee(
            id = id,
            data = data,
            knowledge = emptyList(),
            created = now,
            lastModified = now
        )

        repository.save(employee)
        publisher.publishEmployeeCreated(employee)

        return employee
    }

    fun updateData(id: UUID, block: (Employee.Data) -> Employee.Data): Employee? {
        var oldEmployee: Employee? = null

        val updatedEmployee = update(id) { employee ->
            oldEmployee = employee
            employee.copy(data = block(employee.data))
        }

        if (oldEmployee != null && updatedEmployee != null) {
            publisher.publishEmployeeDataUpdated(oldEmployee!!, updatedEmployee)
        }

        return updatedEmployee
    }

    fun update(id: UUID, block: (Employee) -> Employee): Employee? {
        val employee = get(id) ?: return null
        val updatedEmployee = block(employee)
        repository.save(updatedEmployee)
        return updatedEmployee
    }

    fun delete(id: UUID) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
            publisher.publishEmployeeDeleted(id)
        }
    }

}
