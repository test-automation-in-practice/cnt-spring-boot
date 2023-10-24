package example.spring.modulith.skill

import example.spring.modulith.skill.internal.SkillService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SkillAccessor(
    private val service: SkillService
) {
    fun getById(skillId: UUID): SkillDto? =
        service.get(skillId)?.toDto()
}
