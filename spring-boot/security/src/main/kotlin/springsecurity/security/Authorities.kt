package springsecurity.security

object Authorities {
    const val ROLE_USER = "ROLE_${Roles.USER}"
    const val ROLE_CURATOR = "ROLE_${Roles.CURATOR}"

    const val SCOPE_BOOKS = "SCOPE_BOOKS"
    const val SCOPE_ACTUATOR = "SCOPE_ACTUATOR"
}
