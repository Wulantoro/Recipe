package com.example.latihanmvvm

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.latihanmvvm.Adapter.RecipeAdapter
import com.example.latihanmvvm.Utils.Constant
import com.example.latihanmvvm.ViewModel.RecipeState
import com.example.latihanmvvm.ViewModel.RecipeViewModel
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        isLoggedIn()
        setupRecycler()

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel.getRecipes().observe(this, Observer {
            rvrecipe.adapter?.let { adapter ->
                if (adapter is RecipeAdapter) {
                    adapter.setRecipes(it)
                }
            }
        })

        recipeViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun setupRecycler() {
        rvrecipe.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecipeAdapter(mutableListOf(), this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        recipeViewModel.fetchAllPost(Constant.getToken(this))
    }

    private fun handleUIState(it : RecipeState) {
        when(it) {
            is RecipeState.IsLoading -> isLoading(it.state)
            is RecipeState.Error -> {
                toast(it.err)
                isLoading(false)
            }
        }
    }

    private fun toast(message : String?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun isLoading(state : Boolean) {
        if (state) {
            pbloading.visibility = View.VISIBLE
        } else {
            pbloading.visibility = View.GONE
        }
    }

    private fun isLoggedIn() {
        if (Constant.getToken(this).equals("undefined")) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }).also { finish() }
        }
    }
}