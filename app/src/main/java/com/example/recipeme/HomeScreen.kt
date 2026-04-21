package com.example.recipeme

import androidx.compose.ui.res.stringResource
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

@Composable
fun HomeScreen(
    onInputIngredientsClick: () -> Unit = {},
    onSavedRecipesClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val context = LocalContext.current
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            content = { Text(stringResource(R.string.find_recipe)) },
            onClick = { onInputIngredientsClick() }
        )

        Button(
            content = { Text(stringResource(R.string.saved_recipes)) },
            onClick = { onSavedRecipesClick() }
        )


        var lastShakeTime by remember { mutableStateOf(0L) }
        val recipeManager = remember { RecipeManager() }

        val sensorEventListener = remember {
            object: SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]


                    val acceleration = sqrt((x * x + y * y + z * z).toDouble())

                    val currentTime = System.currentTimeMillis()

                    if (acceleration > 15) {
                        if (currentTime - lastShakeTime > 1000) {
                            lastShakeTime = currentTime

                            CoroutineScope(Dispatchers.IO).launch {
                                val queries = listOf("breakfast", "lunch", "dinner", "dessert")
                                val query = queries.random()

                                val recipes = recipeManager.getRecipes(
                                    ingredients = query,
                                    appID = context.getString(R.string.appID),
                                    appKey = context.getString(R.string.appKey),
                                    userID = context.getString(R.string.userID)
                                )

                                if (recipes.isNotEmpty()) {
                                    val randomRecipe = recipes.random()

                                    withContext(Dispatchers.Main) {
                                        openUrl(context, randomRecipe.url)
                                    }
                                }
                            }
                        }
                    }
                }
                fun openUrl(context: Context, url: String) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
        }

        DisposableEffect(Unit) {
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            onDispose {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.shake_for_recipe),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 15.dp)
        )
    }

}
