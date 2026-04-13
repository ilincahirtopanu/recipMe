package com.example.recipeme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onInputIngredientsClick: () -> Unit = {},
    onSavedRecipesClick: () -> Unit = {},
    onAddRecipeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "RecipMe",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(content = { Text("Find a recipe for you!") }, onClick = {onInputIngredientsClick()})
        Button(content = { Text("Your saved recipes") }, onClick = {onSavedRecipesClick()})
        Button(content = { Text("Add your own recipe") }, onClick = {onAddRecipeClick()})

    }
}
