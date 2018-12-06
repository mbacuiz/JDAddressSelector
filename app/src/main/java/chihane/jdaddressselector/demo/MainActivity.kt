package chihane.jdaddressselector.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.FrameLayout
import chihane.jdaddressselector.AddressSelector
import chihane.jdaddressselector.BottomDialog
import chihane.jdaddressselector.OnAddressSelectedListener
import chihane.jdaddressselector.model.City
import chihane.jdaddressselector.model.County
import chihane.jdaddressselector.model.Province

class MainActivity : AppCompatActivity(), OnAddressSelectedListener {
    override fun onAddressSelected(province: Province?, city: City?, county: County?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val frameLayout = findViewById(R.id.frameLayout) as FrameLayout

        val selector = AddressSelector(this)
        selector.onAddressSelectedListener = this
        selector.setDef(410922,"清丰县")
        //        selector.setAddressProvider(new TestAddressProvider());

        assert(frameLayout != null)
        frameLayout.addView(selector.view)

        val buttonBottomDialog = findViewById(R.id.buttonBottomDialog) as Button
        assert(buttonBottomDialog != null)
        buttonBottomDialog.setOnClickListener {
            //                BottomDialog.show(MainActivity.this, MainActivity.this);
            val dialog = BottomDialog(this@MainActivity)
            dialog.setOnAddressSelectedListener(this@MainActivity)
            dialog.setDef(410922,"清县")
            dialog.show()
        }
    }

}
