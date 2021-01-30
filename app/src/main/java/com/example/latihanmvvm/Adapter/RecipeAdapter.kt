package com.example.latihanmvvm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.latihanmvvm.Model.Recipe
import com.example.latihanmvvm.R
import kotlinx.android.synthetic.main.list_item_recipe.view.*

class RecipeAdapter(private var recipes : MutableList<Recipe>, private var context: Context) : RecyclerView.Adapter<RecipeAdapter.ViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_recipe, parent,false))
    }

    fun setRecipes(r : List<Recipe>) {
        recipes.clear()
        recipes.addAll(r)
        notifyDataSetChanged()

    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecipeAdapter.ViewHolder, position: Int) = holder.bind(recipes[position], context)
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipe : Recipe, context: Context) {
            itemView.tvtitle.text = recipe.title
            itemView.tvcontent.text = recipe.content
            itemView.setOnClickListener {
                Toast.makeText(context, recipe.title.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}