package chihane.jdaddressselector.model


data class County(
        val citycode: String,
        val adcode: String,
        val name: String,
        val center: String,
        val level: String,
        val districts: List<Any>
)