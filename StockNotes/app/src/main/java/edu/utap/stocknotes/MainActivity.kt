package edu.utap.stocknotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.android.volley.toolbox.Volley


object PlaceholderData {
    val values = listOf(SymbolNote("AAPL", "Good stock to own... Maybe!"),
            SymbolNote("SBUX", "Delicious stock to own... Fingers coffee!"),
            SymbolNote("NKE", "Not sure about buying. Maybe I should go for a run instead."))
}

class MainActivity : AppCompatActivity() {

    private val myView = R.layout.graph_note
    private val viewModel: MyViewModel by viewModels()
    private val map = mutableMapOf<String, List<Value>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager: ViewPager = findViewById(R.id.viewPager)

        for (value in PlaceholderData.values) {
            map[value.symbol] = listOf()
        }

        val queue = Volley.newRequestQueue(this)
        for (i in PlaceholderData.values) {
            Repository.netInfo(i.symbol, viewModel, queue)
        }

        viewModel.observeStockValues().observe(this, Observer {
            map[it.first] = it.second
            viewPager.adapter = MyPageAdapter(myView, this, PlaceholderData.values, map)
        })
    }
}
