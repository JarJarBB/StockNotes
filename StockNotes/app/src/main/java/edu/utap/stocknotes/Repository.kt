package edu.utap.stocknotes

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


object Repository {

    private const val templateUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&apikey=MS444HQW0HB7WHKU&symbol="

    fun netInfo(symbol: String, context: Context, viewModel: MyViewModel) {
        val url = templateUrl + symbol
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {
                // on success
                viewModel.postDataPoints(Pair(symbol, parseDataPoints(it)))
            },
            {
                // on error
                Log.d("StringRequest", "Unable to get content from server")
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun parseDataPoints(raw: String) : List<Value> {
        val list = mutableListOf<Value>()
        var id = 0
        val searchTerm = "4. close"
        var startIndex = raw.indexOf(searchTerm, 0)
        while (startIndex >= 0) {
            val endIndex = raw.indexOf("\"", startIndex + 12)
            list.add(Value(id++, raw.subSequence(startIndex + 12, endIndex).toString().toFloat()))
            startIndex = raw.indexOf(searchTerm, startIndex + 1)
        }
        return list
    }
}
