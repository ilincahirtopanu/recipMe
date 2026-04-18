package com.example.recipeme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun SavedRecipesScreen() {
    val context = LocalContext.current
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("savedRecipes")

            ref.get().addOnSuccessListener { snapshot ->
                val tempList = mutableListOf<Recipe>()

                for (child in snapshot.children) {
                    val id = child.key ?: ""

                    val label = child.child("label").getValue(String::class.java) ?: ""
                    val image = child.child("image").getValue(String::class.java) ?: ""
                    val source = child.child("source").getValue(String::class.java) ?: ""
                    val url = child.child("url").getValue(String::class.java) ?: ""
                    val calories = child.child("calories").getValue(Double::class.java) ?: 0.0

                    val ingredients = mutableListOf<String>()
                    val ingredientsSnapshot = child.child("ingredients")
                    for (i in ingredientsSnapshot.children) {
                        ingredients.add(i.getValue(String::class.java) ?: "")
                    }

                    tempList.add(
                        Recipe(label, image, source, url, calories, ingredients, id) // ✅ pass id
                    )
                }

                recipes = tempList
                isLoading = false
            }.addOnFailureListener {
                isLoading = false
            }
        }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Saved Recipes", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            LinearProgressIndicator()
        } else if (recipes.isEmpty()) {
            Text("No saved recipes yet")
        } else {
            LazyColumn {
                items(recipes) { recipe ->
                    RecipeItem(recipe, isSavedScreen = true)
                }
            }
        }
    }
}