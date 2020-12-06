package edu.utap.stocknotes

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.volley.toolbox.Volley
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fav_graph.*

class FavActivity : AppCompatActivity() {


    private val viewModel: MyViewModel by viewModels()
    lateinit var st:StorageST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for the layout we created
        setContentView(R.layout.fav_graph)

        val activityThatCalled = intent
        // Get the data that was sent
        val callingBundle = activityThatCalled.extras

        val name = callingBundle?.getString("name")
        val symb = callingBundle?.getString("symbol")


        stock_name.text = symb
        description.text = name


        val stt = SymbolNote(symb!!,"",name!!,"true")

        st = StorageST(viewModel)
        add_button.setOnClickListener{

            Log.d("finish","clicked")
            st.addStock(stt)
            this.finish()
        }

        val queue = Volley.newRequestQueue(this)
        if (symb != null) Repository.netInfo(symb, viewModel, queue)

        viewModel.observeStockValues().observe(this, Observer {
            val series = LineGraphSeries<DataPoint>()
            for (data in it.second) {
                series.appendData(DataPoint(data.id.toDouble(), data.value.toDouble()), true, 1000)
            }
            graph_view.addSeries(series)
        })
    }


    //////////////////////////////////
    // Routines to log Activity lifecycle
    override fun onStart() {
        Log.d(localClassName, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(localClassName, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(localClassName, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(localClassName, "onStop")
        super.onStop()
    }


}