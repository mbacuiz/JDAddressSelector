package chihane.jdaddressselector.model

data class City(
        val citycode: String,
        val adcode: String,
        val name: String,
        val center: String,
        val level: String,
        val districts: List<County>
)