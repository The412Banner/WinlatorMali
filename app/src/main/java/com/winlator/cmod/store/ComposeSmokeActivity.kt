package com.winlator.cmod.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Phase-0 smoke test for the store port. Proves the Kotlin 2.0 + Jetpack Compose toolchain compiles
 * inside WinlatorMali before any real store code lands. Deliberately NOT registered in the manifest
 * and never launched — delete once the first real store screen (GOG) is in. This is also the first
 * file of the self-contained store "annex" package.
 */
class ComposeSmokeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    SmokeScreen()
                }
            }
        }
    }
}

@Composable
private fun SmokeScreen() {
    Text(
        text = "Store-port toolchain OK",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(24.dp),
    )
}
