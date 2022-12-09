package example.spring.boot.data.jpa.model

data class Title(val value: String) {
    init {
        require(value.isNotBlank()) { "Titles must not be blank!" }
        require(value.length <= 100) { "Titles longer than 100 characters are not allowed!" }
    }
}
