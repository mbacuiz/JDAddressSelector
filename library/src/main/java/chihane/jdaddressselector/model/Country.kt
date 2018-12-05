package chihane.jdaddressselector.model

data class Country(
        val adcode: String,
        val name: String,
        val center: String,
        val level: String,
        val districts: List<Province>
)