package edu.utap.stocknotes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.android.volley.toolbox.Volley
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest


object PlaceholderData {
    val values = listOf(SymbolNote("AAPL", "Good stock to own... Maybe!"),
            SymbolNote("SBUX", "Delicious stock to own... Fingers coffee!"),
            SymbolNote("NKE", "Not sure about buying. Maybe I should go for a run instead."))
}

class MainActivity : AppCompatActivity() {

    private val myView = R.layout.graph_note
    private val viewModel: MyViewModel by viewModels()
    private val map = mutableMapOf<String, List<Value>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fireAuth()
        val viewPager: ViewPager = findViewById(R.id.viewPager)

        for (value in PlaceholderData.values) {
            map[value.symbol] = listOf()
        }

        // just to get the adapter going, without waiting for the network
        val sym = PlaceholderData.values[0].symbol
        viewModel.postDataPoints(Pair(sym, map[sym]) as Pair<String, List<Value>>)

        val queue = Volley.newRequestQueue(this)
        for (i in PlaceholderData.values) {
            Repository.netInfo(i.symbol, viewModel, queue)
        }

        viewModel.observeStockValues().observe(this, Observer {
            map[it.first] = it.second
            viewPager.adapter = MyPageAdapter(myView, this, PlaceholderData.values, map)
        })
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
            } else {
                // Sign in failed.
                Log.d("HERE", "Sign-in failed")
            }
        }
    }
}
