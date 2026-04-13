package com.example.recipeme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class RecipeManager {
    val okHttpClient: OkHttpClient
    init{
        val builder= OkHttpClient.Builder()
        val loggingInterceptor= HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor ( loggingInterceptor )

        okHttpClient=builder.build()
    }

    fun getIngredients() : List<Ingredient> {
        val request = Request.Builder()
            .url("https://www.themealdb.com/api/json/v1/1/list.php?i=list")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val json = JSONObject(responseBody)
            val ingredientArr = json.getJSONArray("meals")
            val ingredients = mutableListOf<Ingredient>()

            for (i in 0 until ingredientArr.length()) {
                val ingredient = ingredientArr.getJSONObject(i)
                val ingredientName = ingredient.getString("strIngredient")
                val ingredientDescription = ingredient.getString("strDescription")
                val ingredientImage = ingredient.getString("strThumb")
                ingredients.add(Ingredient(ingredientName, ingredientDescription, ingredientImage))
            }
            return ingredients
        } else{
            return listOf()
        }

    }

    fun getRecipes(ingredients: String, appID: String, appKey: String, userID: String): List<Recipe> {


        val request = Request.Builder()
            .url("https://api.edamam.com/api/recipes/v2?type=public&q=$ingredients&app_id=$appID&app_key=$appKey")
            .get()
            .addHeader("Edamam-Account-User", userID)
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val recipes = mutableListOf<Recipe>()

            val json = JSONObject(responseBody)
            val hitsArray = json.getJSONArray("hits")

            for (i in 0 until hitsArray.length()) {
                val hit = hitsArray.getJSONObject(i)
                val recipeObj = hit.getJSONObject("recipe")

                val label = recipeObj.getString("label")
                val image = recipeObj.getString("image")
                val source = recipeObj.getString("source")
                val url = recipeObj.getString("url")
                val calories = recipeObj.getDouble("calories")

                val ingredientLinesArray = recipeObj.getJSONArray("ingredientLines")
                val ingredientLines = mutableListOf<String>()
                for (j in 0 until ingredientLinesArray.length()) {
                    ingredientLines.add(ingredientLinesArray.getString(j))
                }

                recipes.add(
                    Recipe(
                        label,
                        image,
                        source,
                        url,
                        calories,
                        ingredientLines
                    )
                )
            }

            return recipes
        }
        else {
            return listOf()
        }
    }

}