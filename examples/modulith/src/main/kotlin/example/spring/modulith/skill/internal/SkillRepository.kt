package example.spring.modulith.skill.internal

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SkillRepository : MongoRepository<Skill, UUID>
