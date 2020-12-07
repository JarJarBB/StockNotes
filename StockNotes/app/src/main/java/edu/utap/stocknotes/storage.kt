package edu.utap.stocknotes

import android.util.Log
import com.android.volley.toolbox.Volley

class StorageST(viewModel: MyViewModel?) {

        val currDoc = MainActivity.docref
        val curuser = MainActivity.currUser
        val viewm = viewModel
//        val favs = MutableLiveData<List<SymbolNote>>()

        fun getAllStock(){

            currDoc.collection("stocks")
                .get()
                .addOnSuccessListener { res ->
                    Log.d("HERE", "StorageST allNotes fetch successful ")
                    viewm!!.favs.postValue(res.documents.mapNotNull {
                        it.toObject(SymbolNote::class.java)
                    })
                }
                .addOnFailureListener{exception->
                    Log.d("HERE", "StorageST allNotes fetch FAILED ")
                }

        }



        fun addStock(stock: SymbolNote) {
            // Delete the file
            // XXX Write me
            // https://firebase.google.com/docs/storage/android/delete-files
            var symbolMap = HashMap<String, String>()
            symbolMap.put("symbol", stock.symbol!!)
            symbolMap.put("name", stock.name!!)
            symbolMap.put("note", stock.note!!)
            symbolMap.put("deleted",true.toString())

            currDoc.collection("stocks").document(stock.symbol).set(symbolMap)
            /*
            if (viewm != null) {
                Repository.netInfo(stock.symbol!!, viewm, MainActivity.queue2)
                Log.d("HERE", "setting the LiveData marker")
                viewm.marker.postValue(true)
            }*/
        }

        fun deleteStock(stock: SymbolNote){

            currDoc.collection("stocks").document(stock.symbol!!).delete()
            Log.d("HERE", "in deleteStock")
            for (e in DataHolder.values) {
                Log.d("HERE", "e.name = ${e.name!!}, stock.symbol = ${stock.symbol!!}")
                if (e.symbol!! == stock.symbol!!) {
                    DataHolder.values.remove(e)
                    Log.d("HERE", "removed the deletable guy")
                    break
                }
            }
            if (viewm != null) {
                Log.d("HERE", "setting the LiveData marker")
                viewm.marker.postValue(true)
            }
            else {
                Log.d("HERE", "viewm is null")
            }

        }



}


