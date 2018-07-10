package jpa.foo

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FooRepository : JpaRepository<FooEntity, UUID>
