package edu.utap.stocknotes



import android.app.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

//import kotlinx.android.synthetic.main.search_rv.*
import kotlinx.android.synthetic.main.content_main.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


object DataHolder {
    var values = mutableListOf(SymbolNote("...", ""))
}



class MainActivity : AppCompatActivity() {

    private val myView = R.layout.graph_note
    private val viewModel: MyViewModel by viewModels()
    private val map = mutableMapOf<String, List<Value>>()
    private val searchBase = "https://www.alphavantage.co/"
    private val restap = restAPI()
    lateinit  var recycleradap :RecAdap
    private val favslist = MutableLiveData<ArrayList<SymbolNote>>()
    private lateinit var viewPager: ViewPager





    companion object {
        lateinit var currUser :String
        lateinit var docref :DocumentReference
        var store = FirebaseFirestore.getInstance()
        lateinit var queue2: RequestQueue
    }
    lateinit var st:StorageST

    fun getAllStock():ArrayList<SymbolNote>{

        val stocks= ArrayList<SymbolNote>()

        docref.collection("stocks") .orderBy("name")
            .limit(100)
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

    private fun refreshUI() {
        viewPager.adapter = MyPageAdapter(myView, this, DataHolder.values, map, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fireAuth()
        viewPager = findViewById(R.id.viewPager)


//        for (value in PlaceholderData.values) {
//            map[value.symbol!!] = listOf()
//        }

        viewModel.observeStockValues().observe(this, Observer {
            map[it.first] = it.second
            refreshUI()
          //  viewPager.adapter = MyPageAdapter(myView, this, DataHolder.values, map)
        })

//        val allstocks=st.getAllStock()
//        PlaceholderData.values=allstocks
        viewModel.favs.observe(this,{
            Log.d("got favs",viewModel.favs.value!!.size.toString())
            // use this place to update the p;alceholder which will chnage our viepager
            //if this is set then whenever the favs changes ,our main activity il change
            //all you have to do is just add a sanpsjot listener(storgae liveddata),and then
            //upddate viemodels fav

            // fetch the graph data from network
            val queue = Volley.newRequestQueue(this)
            for (i in it) {
                Repository.netInfo(i.symbol!!, viewModel, queue)
            }
            DataHolder.values = it as MutableList<SymbolNote>
            for (value in DataHolder.values) {
                map[value.symbol!!] = listOf()
            }
        })

        viewModel.marker.observe(this,{
            Log.d("HERE", "calling refreshUI()")
            refreshUI()
        })


        search_button.setOnClickListener{
            val getNameScreenIntent = Intent(this, SearchResultsActivity::class.java)
            val myExtras = Bundle()
            //SSS
//            myExtras.putString(callingActivityKey, localClassName)
            getNameScreenIntent.putExtras(myExtras)
            val result = 1
            startActivityForResult(getNameScreenIntent, result)



        }




    }

    private fun fireAuth() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                currUser = FirebaseAuth.getInstance().currentUser!!.uid
                docref = store.collection("usersStocks").document(currUser)

                st= StorageST(viewModel)
                st.getAllStock()
                Log.d("HERE", "Sign-in successful")
                user?.let {
                    Log.d("HERE", "Username is ************** ${user.displayName}")
                    Log.d("HERE", "User email is ************ ${user.email}")
                }


                // just to get the adapter going, without waiting for the network
                listOf(SymbolNote("...", ""))

//                Log.d("AllStocks",allstocks.size.toString())
//                val sym = PlaceholderData.values[0].symbol
               viewModel.postDataPoints(Pair(DataHolder.values[0].symbol!!, listOf()))

                queue2 = Volley.newRequestQueue(this)

            } else {
                // Sign in failed.
                Log.d("HERE", "Sign-in failed")
            }
        } else {
            Log.d("HERE", "Sign-in failed v2")
        }
    }

}
