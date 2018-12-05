package chihane.jdaddressselector

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province
import mlxy.utils.Lists

class AddressSelector(private val context: Context) : AdapterView.OnItemClickListener {

    private val handler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            WHAT_PROVINCES_PROVIDED -> {
                provinces = msg.obj as List<Province>
                provinceAdapter!!.notifyDataSetChanged()
                listView!!.adapter = provinceAdapter
            }

            WHAT_CITIES_PROVIDED -> {
                cities = msg.obj as List<City>
                cityAdapter!!.notifyDataSetChanged()
                if (Lists.notEmpty(cities)) {
                    // 以次级内容更新列表
                    listView!!.adapter = cityAdapter
                    // 更新索引为次级
                    tabIndex = INDEX_TAB_CITY
                } else {
                    // 次级无内容，回调
                    callbackInternal()
                }
            }

            WHAT_COUNTIES_PROVIDED -> {
                counties = msg.obj as List<County>
                countyAdapter!!.notifyDataSetChanged()
                if (Lists.notEmpty(counties)) {
                    listView!!.adapter = countyAdapter
                    tabIndex = INDEX_TAB_COUNTY
                } else {
                    callbackInternal()
                }
            }

        }

        updateTabsVisibility()
        updateProgressVisibility()
        updateIndicator()

        true
    })
    var closeListener: CloseListener? = null
    var onAddressSelectedListener: OnAddressSelectedListener? = null
    private var addressProvider: AddressProvider? = null

    var view: View? = null
        private set

    private var indicator: View? = null

    private var textViewProvince: TextView? = null
    private var textViewCity: TextView? = null
    private var textViewCounty: TextView? = null

    private var progressBar: ProgressBar? = null

    private var listView: ListView? = null
    private var provinceAdapter: ProvinceAdapter? = null
    private var cityAdapter: CityAdapter? = null
    private var countyAdapter: CountyAdapter? = null

    private var provinces: List<Province>? = null
    private var cities: List<City>? = null
    private var counties: List<County>? = null

    private var provinceIndex = INDEX_INVALID
    private var cityIndex = INDEX_INVALID
    private var countyIndex = INDEX_INVALID

    private var tabIndex = INDEX_TAB_PROVINCE

    init {

        DEFAULT_ADDRESS_PROVIDER = DefaultAddressProvider(context)
        addressProvider = DEFAULT_ADDRESS_PROVIDER

        initViews()
        initAdapters()
        retrieveProvinces()
    }

    private fun initAdapters() {
        provinceAdapter = ProvinceAdapter()
        cityAdapter = CityAdapter()
        countyAdapter = CountyAdapter()
    }

    private fun initViews() {
        view = LayoutInflater.from(context).inflate(R.layout.address_selector, null)

        this.progressBar = view!!.findViewById(R.id.progressBar) as ProgressBar

        this.listView = view!!.findViewById(R.id.listView) as ListView
        this.indicator = view!!.findViewById(R.id.indicator)

        this.textViewProvince = view!!.findViewById(R.id.textViewProvince) as TextView
        this.textViewCity = view!!.findViewById(R.id.textViewCity) as TextView
        this.textViewCounty = view!!.findViewById(R.id.textViewCounty) as TextView
        view?.findViewById<ImageView>(R.id.iv_close)?.setOnClickListener { closeListener?.onClose() }
        this.textViewProvince!!.setOnClickListener(OnProvinceTabClickListener())
        this.textViewCity!!.setOnClickListener(OnCityTabClickListener())
        this.textViewCounty!!.setOnClickListener(OnCountyTabClickListener())

        this.listView!!.onItemClickListener = this

        updateIndicator()
    }

    private fun updateIndicator() {
        view!!.post {
            when (tabIndex) {
                INDEX_TAB_PROVINCE -> buildIndicatorAnimatorTowards(textViewProvince!!).start()
                INDEX_TAB_CITY -> buildIndicatorAnimatorTowards(textViewCity!!).start()
                INDEX_TAB_COUNTY -> buildIndicatorAnimatorTowards(textViewCounty!!).start()
            }
        }
    }

    private fun buildIndicatorAnimatorTowards(tab: TextView): AnimatorSet {
        val xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator!!.x, tab.x)

        val params = indicator!!.layoutParams
        val widthAnimator = ValueAnimator.ofInt(params.width, tab.measuredWidth)
        widthAnimator.addUpdateListener { animation ->
            params.width = animation.animatedValue as Int
            indicator!!.layoutParams = params
        }

        val set = AnimatorSet()
        set.interpolator = FastOutSlowInInterpolator()
        set.playTogether(xAnimator, widthAnimator)

        return set
    }

    private inner class OnProvinceTabClickListener : View.OnClickListener {

        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_PROVINCE
            listView!!.adapter = provinceAdapter

            if (provinceIndex != INDEX_INVALID) {
                listView!!.setSelection(provinceIndex)
            }

            updateTabsVisibility()
            updateIndicator()
        }
    }

    private inner class OnCityTabClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_CITY
            listView!!.adapter = cityAdapter

            if (cityIndex != INDEX_INVALID) {
                listView!!.setSelection(cityIndex)
            }

            updateTabsVisibility()
            updateIndicator()
        }
    }

    private inner class OnCountyTabClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_COUNTY
            listView!!.adapter = countyAdapter

            if (countyIndex != INDEX_INVALID) {
                listView!!.setSelection(countyIndex)
            }

            updateTabsVisibility()
            updateIndicator()
        }
    }

    private fun updateTabsVisibility() {
        textViewProvince!!.visibility = if (Lists.notEmpty(provinces)) View.VISIBLE else View.GONE
        textViewCity!!.visibility = if (Lists.notEmpty(cities)) View.VISIBLE else View.GONE
        textViewCounty!!.visibility = if (Lists.notEmpty(counties)) View.VISIBLE else View.GONE

        textViewProvince!!.isEnabled = tabIndex != INDEX_TAB_PROVINCE
        textViewCity!!.isEnabled = tabIndex != INDEX_TAB_CITY
        textViewCounty!!.isEnabled = tabIndex != INDEX_TAB_COUNTY
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (tabIndex) {
            INDEX_TAB_PROVINCE -> {
                val province = provinceAdapter!!.getItem(position)

                // 更新当前级别及子级标签文本
                textViewProvince!!.text = province.name
                textViewCity!!.text = "请选择"
                textViewCounty!!.text = "请选择"

                // 清空子级数据
                cities = null
                counties = null
                cityAdapter!!.notifyDataSetChanged()
                countyAdapter!!.notifyDataSetChanged()

                // 更新已选中项
                this.provinceIndex = position
                this.cityIndex = INDEX_INVALID
                this.countyIndex = INDEX_INVALID

                // 更新选中效果
                provinceAdapter!!.notifyDataSetChanged()

                retrieveCitiesWith(province.adcode.toInt())
            }

            INDEX_TAB_CITY -> {
                val city = cityAdapter!!.getItem(position)

                textViewCity!!.text = city.name
                textViewCounty!!.text = "请选择"

                counties = null
                countyAdapter!!.notifyDataSetChanged()

                this.cityIndex = position
                this.countyIndex = INDEX_INVALID

                cityAdapter!!.notifyDataSetChanged()

                retrieveCountiesWith(city.adcode.toInt())
            }

            INDEX_TAB_COUNTY -> {
                val county = countyAdapter!!.getItem(position)

                textViewCounty!!.text = county.name


                this.countyIndex = position

                countyAdapter!!.notifyDataSetChanged()

            }

        }

        updateTabsVisibility()
        updateIndicator()
    }

    private fun callbackInternal() {
        if (onAddressSelectedListener != null) {
            val province = if (provinces == null || provinceIndex == INDEX_INVALID) null else provinces!![provinceIndex]
            val city = if (cities == null || cityIndex == INDEX_INVALID) null else cities!![cityIndex]
            val county = if (counties == null || countyIndex == INDEX_INVALID) null else counties!![countyIndex]

            onAddressSelectedListener!!.onAddressSelected(province, city, county)
        }
    }

    private fun updateProgressVisibility() {
        val adapter = listView!!.adapter
        val itemCount = adapter.count
        progressBar!!.visibility = if (itemCount > 0) View.GONE else View.VISIBLE
    }

    private fun retrieveProvinces() {
        progressBar!!.visibility = View.VISIBLE
        addressProvider!!.provideProvinces(object : AddressProvider.AddressReceiver<Province> {
            override fun send(data: List<Province>) {
                handler.sendMessage(Message.obtain(handler, WHAT_PROVINCES_PROVIDED, data))
            }
        })
    }

    private fun retrieveCitiesWith(provinceId: Int) {
        progressBar!!.visibility = View.VISIBLE
        addressProvider!!.provideCitiesWith(provinceId, object : AddressProvider.AddressReceiver<City> {
            override fun send(data: List<City>) {
                handler.sendMessage(Message.obtain(handler, WHAT_CITIES_PROVIDED, data))
            }
        })
    }

    private fun retrieveCountiesWith(cityId: Int) {
        progressBar!!.visibility = View.VISIBLE
        addressProvider!!.provideCountiesWith(cityId, object : AddressProvider.AddressReceiver<County> {
            override fun send(data: List<County>) {
                handler.sendMessage(Message.obtain(handler, WHAT_COUNTIES_PROVIDED, data))
            }
        })
    }

    private inner class ProvinceAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return if (provinces == null) 0 else provinces!!.size
        }

        override fun getItem(position: Int): Province {
            return provinces!![position]
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).adcode.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: Holder

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_area, parent, false)

                holder = Holder()
                holder.textView = convertView!!.findViewById(R.id.textView) as TextView
                holder.imageViewCheckMark = convertView.findViewById(R.id.imageViewCheckMark) as ImageView

                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }

            val item = getItem(position)
            holder.textView!!.text = item.name

            val checked = provinceIndex != INDEX_INVALID && provinces!![provinceIndex].adcode.toInt() == item.adcode.toInt()
            holder.textView!!.isEnabled = !checked
            holder.imageViewCheckMark!!.visibility = if (checked) View.VISIBLE else View.GONE

            return convertView
        }

        internal inner class Holder {
            var textView: TextView? = null
            var imageViewCheckMark: ImageView? = null
        }
    }

    private inner class CityAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return if (cities == null) 0 else cities!!.size
        }

        override fun getItem(position: Int): City {
            return cities!![position]
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).adcode.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: Holder

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_area, parent, false)

                holder = Holder()
                holder.textView = convertView!!.findViewById(R.id.textView) as TextView
                holder.imageViewCheckMark = convertView.findViewById(R.id.imageViewCheckMark) as ImageView

                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }

            val item = getItem(position)
            holder.textView!!.text = item.name

            val checked = cityIndex != INDEX_INVALID && cities!![cityIndex].adcode == item.adcode
            holder.textView!!.isEnabled = !checked
            holder.imageViewCheckMark!!.visibility = if (checked) View.VISIBLE else View.GONE

            return convertView
        }

        internal inner class Holder {
            var textView: TextView? = null
            var imageViewCheckMark: ImageView? = null
        }
    }

    private inner class CountyAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return if (counties == null) 0 else counties!!.size
        }

        override fun getItem(position: Int): County {
            return counties!![position]
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).adcode.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: Holder

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_area, parent, false)

                holder = Holder()
                holder.textView = convertView!!.findViewById(R.id.textView) as TextView
                holder.imageViewCheckMark = convertView.findViewById(R.id.imageViewCheckMark) as ImageView

                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }

            val item = getItem(position)
            holder.textView!!.text = item.name

            val checked = countyIndex != INDEX_INVALID && counties!![countyIndex].adcode == item.adcode
            holder.textView!!.isEnabled = !checked
            holder.imageViewCheckMark!!.visibility = if (checked) View.VISIBLE else View.GONE

            return convertView
        }

        internal inner class Holder {
            var textView: TextView? = null
            var imageViewCheckMark: ImageView? = null
        }
    }

    companion object {
        private val INDEX_TAB_PROVINCE = 0
        private val INDEX_TAB_CITY = 1
        private val INDEX_TAB_COUNTY = 2

        private val INDEX_INVALID = -1

        private val WHAT_PROVINCES_PROVIDED = 0
        private val WHAT_CITIES_PROVIDED = 1
        private val WHAT_COUNTIES_PROVIDED = 2

        private var DEFAULT_ADDRESS_PROVIDER: AddressProvider? = null
    }

}

interface CloseListener {
    fun onClose()
}
