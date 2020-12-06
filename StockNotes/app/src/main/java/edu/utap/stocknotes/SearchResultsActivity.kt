package edu.utap.stocknotes

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.search_rv.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultsActivity : AppCompatActivity() {


    lateinit  var recycleradap :RecAdap
    private val restap = restAPI()
    private val viewModel: MyViewModel by viewModels()

    companion object IntentStrings {
        val userNameKey = "userName"
        val userEmailKey = "userEmail"
    }

//    val nores = stocksResult("NONE")


    fun makeCall(keyw:String){

        val callResponse = restap.getStocks(keyw)

        Log.d("inside","makeCall $keyw ")
        callResponse.enqueue(object : Callback<StockSearchResp> {
            override fun onResponse(call: Call<StockSearchResp>, response: Response<StockSearchResp>) {
                if (response.code() == 200) {
                    Log.d("inside","onResponse200")
                    val stockResp= response.body()!!
                    Log.d("stockResp",response.raw().toString())

//                    if (stockResp.stocksRes.size == 0)

                    viewModel.postSearchStock(stockResp)
                    for( st in stockResp.stocksRes){
                        Log.d("Name of stock", st.name.toString())
                    }
                }
            }
            override fun onFailure(call: Call<StockSearchResp>, t: Throwable) {

                Log.d("FAILURE","in call")
            }
        })

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout for the layout we created
        setContentView(R.layout.search_rv)
        recycleradap = RecAdap(viewModel,this)

        search_RV.layoutManager = LinearLayoutManager(this@SearchResultsActivity)
        // It is unfortunately complex to get dividers in a Recyclerview
        val itemDecor = DividerItemDecoration(search_RV.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(this@SearchResultsActivity, (R.drawable.divider))!!)
        search_RV.addItemDecoration(itemDecor)
        search_RV.adapter=recycleradap


//        makeCall("apple")
        viewModel.observetSearchStock().observe(this,{

            Log.d("observing", "api")

            recycleradap.addAll(it.stocksRes)
            Log.d("sizestock",recycleradap.stocks.size.toString())

//            recycleradap.notifyItemRangeChanged(0,recycleradap.stocks.size)
            recycleradap.notifyDataSetChanged()



            }
        )



        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                Log.d("calling","Search $p0")
                makeCall(p0!!)





                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                //Start filtering the list as user start entering the characters

                return false
            }
        })


        // Get the Intent that called for this Activity to open
        val activityThatCalled = intent
        // Get the data that was sent
        val callingBundle = activityThatCalled.extras
        Log.d("insidesearchactivity", "api")


//EEE

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
