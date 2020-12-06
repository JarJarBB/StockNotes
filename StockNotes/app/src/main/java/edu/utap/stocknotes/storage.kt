package edu.utap.stocknotes

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class StorageST {

        val currDoc = MainActivity.docref
        val curuser = MainActivity.currUser

        fun getAllStock():ArrayList<SymbolNote>{

            val stocks = ArrayList<SymbolNote>()

            currDoc.collection("stocks")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("FAKE", "${document.id} => ${document.data}")

                        stocks.add(document.toObject(SymbolNote::class.java));

                    }
                }
                .addOnFailureListener{exception->
                    Log.w("Failure","Error getting documents.",exception)
                }
            return stocks
        }



        fun addStock(stock: SymbolNote) {
            // Delete the file
            // XXX Write me
            // https://firebase.google.com/docs/storage/android/delete-files
            var symbolMap = HashMap<String, String>()
            symbolMap.put("symbol", stock.symbol)
            symbolMap.put("name", stock.desc)
            symbolMap.put("note", stock.note)
            symbolMap.put("deleted",true.toString())

            currDoc.collection("stocks").document(stock.symbol).set(symbolMap)
        }

        fun deleteStock(stock: SymbolNote){

            currDoc.collection("stocks").document(stock.symbol).delete()
        }



        }


