package edu.utap.stocknotes



import android.app.SearchManager
import android.content.Context

import android.app.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.View
import android.widget.FrameLayout
import android.widget.SearchView

import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.android.volley.toolbox.Volley

import retrofit2.Call
import retrofit2.Callback
import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.search_rv.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.search_rv.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest



object PlaceholderData {
    val values = listOf(SymbolNote("AAPL", "Good stock to own... Maybe!","Apple Inc",true),
            SymbolNote("SBUX", "Delicious stock to own... Fingers coffee!","Some random Stock",true),
            SymbolNote("NKE", "Not sure about buying. Maybe I should go for a run instead.","Nike",true))
}



class MainActivity : AppCompatActivity() {

    private val myView = R.layout.graph_note
    private val viewModel: MyViewModel by viewModels()
    private val map = mutableMapOf<String, List<Value>>()
    private val searchBase = "https://www.alphavantage.co/"
    private val restap = restAPI()
    lateinit  var recycleradap :RecAdap






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fireAuth()
        val viewPager: ViewPager = findViewById(R.id.viewPager)


        for (value in PlaceholderData.values) {
            map[value.symbol] = listOf()
        }

        viewModel.observeStockValues().observe(this, Observer {
            map[it.first] = it.second
            viewPager.adapter = MyPageAdapter(myView, this, PlaceholderData.values, map)
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
                Log.d("HERE", "Sign-in successful")
                user?.let {
                    Log.d("HERE", "Username is ************** ${user.displayName}")
                    Log.d("HERE", "User email is ************ ${user.email}")
                }

                // just to get the adapter going, without waiting for the network
                val sym = PlaceholderData.values[0].symbol
                viewModel.postDataPoints(Pair(sym, map[sym]) as Pair<String, List<Value>>)

                // fetch the graph data from network
                val queue = Volley.newRequestQueue(this)
                for (i in PlaceholderData.values) {
                    Repository.netInfo(i.symbol, viewModel, queue)
                }

            } else {
                // Sign in failed.
                Log.d("HERE", "Sign-in failed")
            }
        }
    }

}
