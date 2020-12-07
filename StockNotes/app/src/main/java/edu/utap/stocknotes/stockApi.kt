package edu.utap.stocknotes

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface  StockApi {

    @GET("query?apikey=8Z8N3IM7BYLWD3XZ&symbol=\"&function=SYMBOL_SEARCH")
    fun fetchStocks(
            @Query("keywords") keywords: String,
    ) : Call<StockSearchResp>

}