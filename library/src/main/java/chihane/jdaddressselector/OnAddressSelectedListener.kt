package chihane.jdaddressselector

import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province

interface OnAddressSelectedListener {
    fun onAddressSelected(province: Province?, city: City?, county: County?)
}
