package edu.utap.stocknotes

import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest


object Repository {

    private var callCounter = 0

    private const val templateUrl = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&apikey=EMTACUXM8K7GV9WH&symbol="
    private const val templateUrl2 = "https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&apikey=P6K5LAUKD7XZCQ9C&symbol="
    fun netInfo(symbol: String, viewModel: MyViewModel, queue: RequestQueue) {
        var url = ""
        if (callCounter++ % 2 == 1) url = templateUrl + symbol
        else url = templateUrl2 + symbol
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            {
                // on success
                viewModel.postDataPoints(Pair(symbol, parseDataPoints(it)))
                if (it.length > 100) Log.d("StringRequest", "Success: url: $url \n ${it.subSequence(0, 100)}")
                else Log.d("StringRequest", "Success: url: $url \n ${it}")

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
