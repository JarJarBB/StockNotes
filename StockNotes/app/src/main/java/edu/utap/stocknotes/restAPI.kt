package edu.utap.stocknotes

import android.util.Log
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class restAPI {

    private val sapi: StockApi
    private val searchBase = "https://www.alphavantage.co/"

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(searchBase)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        sapi= retrofit.create(StockApi::class.java)
    }

    fun getStocks(keyword:String): Call<StockSearchResp> {
        Log.d("inside","getStocks ")
        return sapi.fetchStocks(keyword)
    }

}