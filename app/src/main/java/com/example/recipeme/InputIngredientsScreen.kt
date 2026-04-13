package com.example.recipeme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InputIngredientsScreen(
    onContinueClick: (String) -> Unit
) {
    var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val selectedIngredients = remember { mutableStateListOf<Ingredient>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val recipemanager by remember { mutableStateOf(RecipeManager())}


    // Loading ingredients
    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = null

        try {
            val result = withContext(Dispatchers.IO) {
                ingredients = recipemanager.getIngredients()
            }

        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load ingredients"
        } finally {
            isLoading = false
        }
        
        
    }

    val filteredIngredients = ingredients.filter {
        it.ingredientName.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Select Ingredients", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        
        //search
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search ingredients") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ingredient list
        when {
            isLoading -> {
                Column(modifier = Modifier.fillMaxSize()) {

                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            errorMessage != null -> {
                Text(errorMessage!!)
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredIngredients) { ingredient ->
                        IngredientItem(
                            ingredient = ingredient,
                            isSelected = selectedIngredients.contains(ingredient),
                            onClick = {
                                if (selectedIngredients.contains(ingredient)) {
                                    selectedIngredients.remove(ingredient)
                                } else {
                                    selectedIngredients.add(ingredient)
                                }
                            }
                        )
                    }
                }

                // continue button
                Button(
                    onClick = {
                        val selectedNames =
                            selectedIngredients.joinToString(" ") { it.ingredientName }
                        onContinueClick(selectedNames)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedIngredients.isNotEmpty()
                ) {
                    Text("Search Recipes (${selectedIngredients.size})")
                }
            }
        }
    }
}

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ingredient.ingredientImage,
                contentDescription = ingredient.ingredientName,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                ingredient.ingredientName,
                modifier = Modifier.weight(1f),
            )

            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() }
            )
        }
    }
}