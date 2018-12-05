package chihane.jdaddressselector.model

data class Province(
        val adcode: String,
        val name: String,
        val center: String,
        val level: String,
        val districts: List<City>,
        val citycode: String
)