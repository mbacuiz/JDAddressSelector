package com.mba.common.widget.AddressSelector

import android.content.Context
import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province
import chihane.jdaddressselector.utils.ResourceUtils
import com.google.gson.Gson

/**
 *Create by jiancheung on 2018/12/4
 *　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
 *　＃＃＃　　＃＃＃＃　　　　　　＃＃　　　　　　　　＃＃＃　＃＃＃　　
 *　　＃＃　　＃＃　　　　　　　　＃＃＃　　　　　　　　＃＃　＃＃　　　
 *　　＃＃＃　＃＃　　　　　　　　＃＃＃　　　　　　　　＃＃＃＃　　　　
 *　　＃＃＃＃＃＃　　　　　　　＃＃＃＃＃　　　　　　　　＃＃＃　　　　
 *　　＃＃＃＃＃＃　　　　　　　＃＃＃＃＃　　　　　　　＃＃＃＃　　　　
 *　　＃　＃＃　＃　　　　　　＃＃　　＃＃　　　　　　　＃＃　＃＃　　　
 *　＃＃＃＃＃＃＃＃＃　　　＃＃＃　　＃＃＃　　　　＃＃＃＃　＃＃＃　
 */

object DataSource {
    private var address: Address? = null

    fun with(context: Context): DataSource {
        if (null == address) {
            address = Gson().fromJson(ResourceUtils.readAssets2String(context, "districty.json", null), Address::class.java)
        }
        return this
    }

    fun getProvinces(): List<Province> {
        val provinces = ArrayList<Province>()
        address?.districts?.forEach { country ->
            country.districts.forEach { province ->
                provinces.add(province)
            }
        }
        return provinces
    }

    fun getCities(provinceId: Int): List<City> {
        val cities = ArrayList<City>()
        address?.districts?.forEach { country ->
            country.districts.forEach { province ->
                if (province.adcode.toInt() == provinceId) {
                    province.districts.forEach { city ->
                        cities.add(city)
                    }
                }
            }
        }
        return cities
    }

    fun getCounties(cityId: Int): List<County> {
        val counties = ArrayList<County>()
        address?.districts?.forEach { country ->
            country.districts.forEach { province ->
                province.districts.forEach { city ->
                    if (city.adcode.toInt() == cityId) {
                        city.districts.forEach { county ->
                            counties.add(county)
                        }
                    }
                }
            }
        }
        return counties
    }

}