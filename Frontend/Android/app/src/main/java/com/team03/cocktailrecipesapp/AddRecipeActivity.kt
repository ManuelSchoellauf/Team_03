package com.team03.cocktailrecipesapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team03.cocktailrecipesapp.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_add_recipe.*


class AddRecipeActivity : AppCompatActivity() {

    var ingredients_pub: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        timer_picker_minutes.minValue = 1
        timer_picker_minutes.maxValue = 59
        difficulty_picker.minValue = 1
        difficulty_picker.maxValue = 5
    }

    fun onSuccessfullAddRecipe() {

        //this.finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onUnsuccessfullAddRecipe() {
        Toast.makeText(applicationContext, resources.getString(R.string.failed_to_add_recipe), Toast.LENGTH_LONG).show()
    }

    fun onClickManageIngredients(view: View) {

        val intent = Intent(this, AddIngredientsActivity::class.java)
        startActivity(intent)
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null)
        {
            ingredients_pub = data.getStringArrayExtra("Pickedingredients")?.toList()
        }
        ingredients_pub?.forEach { println(it) }
    }
    */

    fun onClickAddRecipe(view: View) {
        val server = serverAPI(applicationContext)
        val listener = AddRecipeListener(::onSuccessfullAddRecipe)
        val errorListener = AddRecipeErrorListener(::onUnsuccessfullAddRecipe)

        val shared: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)

        ingredients_pub = listOf("Rum", "Vodka")

        val recipe_name: String = etRecipeName.text.toString()
        val recipe_description: String = etRecipeDescription.text.toString()
        val preperation_time: Int = timer_picker_minutes.value
        val difficulty: Int = difficulty_picker.value


        server.addRecipe(shared.getInt("userId", 0), recipe_name, preperation_time, difficulty, recipe_description, ingredients_pub.orEmpty(), listener, errorListener)
    }
}
