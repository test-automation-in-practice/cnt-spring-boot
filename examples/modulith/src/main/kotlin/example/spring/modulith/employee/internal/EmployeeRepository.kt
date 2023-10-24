package example.spring.modulith.employee.internal

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.stream.Stream

@Repository
interface EmployeeRepository : MongoRepository<Employee, UUID> {

    @Query("{ 'knowledge.skillId' : ?0 }")
    fun findAllWithKnowledgeOfSkill(skillId: UUID): Stream<Employee>

}
