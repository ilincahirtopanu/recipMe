package com.example.recipeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeme.ui.theme.RecipeMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeMeTheme() {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
                Navigate()
            }
        }
    }
}


@Composable
fun Navigate() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen2(
                onLoginSuccess = {
                    navController.navigate("home")
                },
                onSignUpClick = {
                    navController.navigate("signUp")
                }
            )
        }
        composable("signUp") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("home")
                }
            )
        }
        composable("home"){
            HomeScreen(
                onInputIngredientsClick = {
                    navController.navigate("inputIngredients")
                },
                onSavedRecipesClick = {
                    navController.navigate("savedRecipes")
                }
            )
        }
        composable("inputIngredients") {
            InputIngredientsScreen(
                onContinueClick = { ingredients ->
                    navController.navigate("searchRecipes/$ingredients")
                }
            )
        }
        composable("searchRecipes/{ingredients}", arguments = listOf(navArgument("ingredients") { type = NavType.StringType })) {
            data ->
            val ingredients = data.arguments?.getString("ingredients") ?: ""
            SearchRecipesScreen(
                searchIngredients = ingredients,
//                onRecipeClick = {
//                    recipe ->
//                    navController.navigate("recipeDetail/$recipe")
//                }
            )
        }
        composable("savedRecipes") {
            SavedRecipesScreen()
        }
        composable("addRecipe") {
//            AddRecipeScreen()
        }
        composable("random") {
//            RandomRecipeScreen()
        }
    }
}

@Composable
fun HomeScreen() {

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecipeMeTheme() {
        Greeting("Android")
    }
}