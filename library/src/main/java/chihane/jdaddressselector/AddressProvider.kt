package chihane.jdaddressselector

import chihane.jdaddressselector.model.*

interface AddressProvider {
    fun provideProvinces(addressReceiver: AddressReceiver<Province>)
    fun provideCitiesWith(provinceId: Int, addressReceiver: AddressReceiver<City>)
    fun provideCountiesWith(cityId: Int, addressReceiver: AddressReceiver<County>)

    interface AddressReceiver<T> {
        fun send(data: List<T>)
    }
}