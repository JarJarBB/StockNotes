package edu.utap.stocknotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MyViewModel : ViewModel(){

    private val stockValues = MutableLiveData<Pair<String, List<Value>>>()

    fun postDataPoints(pair: Pair<String, List<Value>>) {
        stockValues.postValue(pair)
    }

    fun observeStockValues(): LiveData<Pair<String, List<Value>>> {
        return stockValues
    }
}
