package edu.utap.stocknotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MyViewModel : ViewModel(){

     val stockValues = MutableLiveData<Pair<String, List<Value>>>()
    val sres = MutableLiveData<StockSearchResp>()


    fun convertToSymbol(st:stocksResult):SymbolNote {
        var symb = SymbolNote(st.symbol,"",st.name,false)
        return symb

    }

    fun postDataPoints(pair: Pair<String, List<Value>>) {
        stockValues.postValue(pair)
    }

    fun observeStockValues(): LiveData<Pair<String, List<Value>>> {
        return stockValues
    }

    fun postSearchStock(item:StockSearchResp) {
        sres.postValue(item)

    }

    fun observetSearchStock(): MutableLiveData<StockSearchResp> {
        return sres
    }

    fun getStockAt(id:Int):SymbolNote{

        val st = sres.value!!.stocksRes.get(id)
        return convertToSymbol(st)
    }


}

//data class SymbolNote (
//        val symbol: String,
//        val note: String,
//        val desc:String ,
//        val deleted : Boolean
//
//)
