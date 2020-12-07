package edu.utap.stocknotes

import android.util.Log

class StorageST(viewModel: MyViewModel?) {

        val currDoc = MainActivity.docref
        val curuser = MainActivity.currUser
        val viewm = viewModel
//        val favs = MutableLiveData<List<SymbolNote>>()

        fun getAllStock(){

            currDoc.collection("stocks")
                .get()
                .addOnSuccessListener { res ->
                    viewm!!.favs.postValue(res.documents.mapNotNull {
                        it.toObject(SymbolNote::class.java)
                    })
                }
                .addOnFailureListener{exception->
                    Log.d(javaClass.simpleName, "allNotes fetch FAILED ", exception)
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
        }

        fun deleteStock(stock: SymbolNote){

            currDoc.collection("stocks").document(stock.symbol!!).delete()
        }



        }


