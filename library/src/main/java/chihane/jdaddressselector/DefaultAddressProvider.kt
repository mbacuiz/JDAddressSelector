package chihane.jdaddressselector


import android.content.Context
import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province
import com.mba.common.widget.AddressSelector.DataSource

class DefaultAddressProvider(private val context: Context) : AddressProvider {

    override fun provideProvinces(addressReceiver: AddressProvider.AddressReceiver<Province>) {
        addressReceiver.send(DataSource.with(context).getProvinces())
    }

    override fun provideCitiesWith(provinceId: Int, addressReceiver: AddressProvider.AddressReceiver<City>) {
        addressReceiver.send(DataSource.with(context).getCities(provinceId))
    }

    override fun provideCountiesWith(cityId: Int, addressReceiver: AddressProvider.AddressReceiver<County>) {
        addressReceiver.send(DataSource.with(context).getCounties(cityId))
    }

}
