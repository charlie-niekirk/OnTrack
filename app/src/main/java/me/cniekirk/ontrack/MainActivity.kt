package me.cniekirk.ontrack

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import me.cniekirk.ontrack.core.compose.di.LocalViewModelGraphFactory
import me.cniekirk.ontrack.core.compose.theme.OnTrackTheme
import me.cniekirk.ontrack.core.di.components.ActivityKey
import me.cniekirk.ontrack.navigation.OnTrackNavigation

@Inject
@ActivityKey(MainActivity::class)
@ContributesIntoMap(AppScope::class, binding<Activity>())
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalViewModelGraphFactory provides (application as OnTrackApp).appGraph::createViewModelGraph
            ) {
                OnTrackTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        OnTrackNavigation(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}