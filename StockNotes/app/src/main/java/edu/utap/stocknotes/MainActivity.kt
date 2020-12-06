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
import com.android.volley.toolbox.Volley

//import kotlinx.android.synthetic.main.search_rv.*
import kotlinx.android.synthetic.main.content_main.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


object PlaceholderData {
    var values = listOf(SymbolNote("AAPL", "Good stock to own... Maybe!","Apple Inc","false"),
            SymbolNote("SBUX", "Delicious stock to own... Fingers coffee!","Some random Stock","true"),
            SymbolNote("NKE", "Not sure about buying. Maybe I should go for a run instead.","Nike","true"))
}



class MainActivity : AppCompatActivity() {

    private val myView = R.layout.graph_note
    private val viewModel: MyViewModel by viewModels()
    private val map = mutableMapOf<String, List<Value>>()
    private val searchBase = "https://www.alphavantage.co/"
    private val restap = restAPI()
    lateinit  var recycleradap :RecAdap
    private val favslist = MutableLiveData<ArrayList<SymbolNote>>()





    companion object {
        lateinit var currUser :String
        lateinit var docref :DocumentReference
        var store = FirebaseFirestore.getInstance()
    }
    lateinit var st:StorageST

    fun getAllStock():ArrayList<SymbolNote>{

        val stocks= ArrayList<SymbolNote>()

        val q = docref.collection("stocks") .orderBy("name")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fireAuth()
        val viewPager: ViewPager = findViewById(R.id.viewPager)


        for (value in PlaceholderData.values) {
            map[value.symbol!!] = listOf()
        }

        viewModel.observeStockValues().observe(this, Observer {
            map[it.first] = it.second
            viewPager.adapter = MyPageAdapter(myView, this, PlaceholderData.values, map)
        })

//        val allstocks=st.getAllStock()
//        PlaceholderData.values=allstocks
        viewModel.favs.observe(this,{
            Log.d("got favs",viewModel.favs.value!!.size.toString())
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


//                Log.d("AllStocks",allstocks.size.toString())
                val sym = PlaceholderData.values[0].symbol
                viewModel.postDataPoints(Pair(sym, map[sym]) as Pair<String, List<Value>>)

                // fetch the graph data from network
                val queue = Volley.newRequestQueue(this)
                for (i in PlaceholderData.values) {
                    Repository.netInfo(i.symbol!!, viewModel, queue)
                }

            } else {
                // Sign in failed.
                Log.d("HERE", "Sign-in failed")
            }
        }
    }

}
