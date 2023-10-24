package example.spring.modulith.employee

import org.jmolecules.event.types.DomainEvent
import org.springframework.modulith.events.Externalized
import java.time.Instant
import java.util.UUID

@Externalized("employee-events::EmployeeCreated")
data class EmployeeCreated(
    override val id: UUID,
    override val timestamp: Instant,
    val employee: EmployeeDto,
) : EmployeeEvent

@Externalized("employee-events::EmployeeDataUpdated")
data class EmployeeDataUpdated(
    override val id: UUID,
    override val timestamp: Instant,
    val oldEmployee: EmployeeDto,
    val newEmployee: EmployeeDto,
) : EmployeeEvent {
    init {
        require(oldEmployee.id == newEmployee.id) {
            "The 'old' and 'new' instances need to reference the same employee!"
        }
    }
}

@Externalized("employee-events::EmployeeDeleted")
data class EmployeeDeleted(
    override val id: UUID,
    override val timestamp: Instant,
    val employeeId: UUID,
) : EmployeeEvent

interface EmployeeEvent : DomainEvent {
    val id: UUID
    val timestamp: Instant
}
