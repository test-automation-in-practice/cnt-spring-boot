package example.spring.modulith.employee

import example.spring.modulith.employee.internal.Employee
import java.util.UUID

data class EmployeeDto(
    val id: UUID,
    val firstName: String,
    val lastName: String,
)

fun Employee.toDto(): EmployeeDto =
    EmployeeDto(
        id = id,
        firstName = data.firstName,
        lastName = data.lastName
    )
