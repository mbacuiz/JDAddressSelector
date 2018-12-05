package com.mba.common.widget.AddressSelector

import chihane.jdaddressselector.model.Country
import chihane.jdaddressselector.model.Suggestion

data class Address(
        val status: String,
        val info: String,
        val infocode: String,
        val count: String,
        val suggestion: Suggestion,
        val districts: List<Country>
)