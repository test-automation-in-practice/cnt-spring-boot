package example.spring.boot.data.jpa.model

private val pattern = Regex("([0-9]{3}(-)?)?[0-9]{10}")

data class Isbn(val value: String) {
    init {
        require(value matches pattern) { "ISBN [$value] must match pattern [$pattern]!" }
    }
}
