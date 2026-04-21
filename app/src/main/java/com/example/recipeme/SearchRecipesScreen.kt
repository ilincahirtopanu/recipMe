package com.example.recipeme

import androidx.compose.ui.res.stringResource
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

fun saveRecipeToFirebase(recipe: Recipe) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("users")
        .child(userId)
        .child("savedRecipes")
        .child(recipe.label.replace(" ", "_"))

    val recipeMap = mapOf(
        "label" to recipe.label,
        "image" to recipe.image,
        "source" to recipe.source,
        "url" to recipe.url,
        "calories" to recipe.calories,
        "ingredients" to recipe.ingredientLines
    )

    ref.setValue(recipeMap)
}

fun deleteRecipeFromFirebase(recipeId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val ref = FirebaseDatabase.getInstance()
        .getReference("users")
        .child(userId)
        .child("savedRecipes")
        .child(recipeId)

    ref.removeValue()
}

@Composable
fun SearchRecipesScreen(
    searchIngredients: String
) {
    val ingredientsList = searchIngredients.split(",")

    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val recipeManager = remember { RecipeManager() }

    val context = LocalContext.current
    val appKey = context.getString(R.string.appKey)
    val appID = context.getString(R.string.appID)
    val userID = context.getString(R.string.userID)

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null

        try {
            val result = withContext(Dispatchers.IO) {
                recipeManager.getRecipes(searchIngredients, appID, appKey, userID)
            }
            recipes = result
        } catch (e: Exception) {
            errorMessage = e.message ?: context.getString(R.string.error_loading)
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.recipes_with),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        ingredientsList.forEach {
            Text(stringResource(R.string.ingredient_item, it))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                LinearProgressIndicator()
            }

            errorMessage != null -> {
                Text(errorMessage!!)
            }

            else -> {
                LazyColumn {
                    items(recipes) { recipe ->
                        RecipeItem(recipe, isSavedScreen = false)
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, isSavedScreen: Boolean = false) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, recipe.url.toUri())
            context.startActivity(intent)
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.label,
                style = MaterialTheme.typography.titleMedium
            )


            Text(
                text = stringResource(R.string.source, recipe.source),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            var isSaved by remember { mutableStateOf(isSavedScreen) }

            IconButton(
                onClick = {
                    if (isSaved) {
                        deleteRecipeFromFirebase(recipe.id)
                    } else {
                        saveRecipeToFirebase(recipe)
                    }
                    isSaved = !isSaved
                }
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.save_recipe)
                )
            }

        }
    }
}