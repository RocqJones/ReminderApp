package com.rocqjones.reminderapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rocqjones.reminderapp.data.DataSource
import com.rocqjones.reminderapp.models.ComposeRandomItem
import com.rocqjones.reminderapp.ui.dialogs.ReminderDialog
import com.rocqjones.reminderapp.ui.theme.ReminderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReminderAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListItems()
                }
            }
        }
    }
}

@Composable
fun ListItems(
    modifier: Modifier = Modifier,
    data: List<ComposeRandomItem> = DataSource.plants.map { it }  // names: List<String> = List(30) { "$it" }
) {
    /**
     * Let's create a performant lazy list.
     * Note that these are the equivalent component to RecyclerView or ListView from Views in Compose.
     */
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = data.toMutableList()) { n ->
            ComposeCard(
                name = n.name,
                type = n.type,
                description = n.description
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeCard(name: String, type: String, description: String) {
    val dialogState = remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = { dialogState.value = true }
    ) {
        CardContent(name, type, description)
    }

    if (dialogState.value) {
        ReminderDialog(name = name, onDismiss = { dialogState.value = false })
    }
}

@Composable
fun CardContent(name: String, type: String, description: String) {
    /**
     * @remember is a composable function that can be used to cache expensive operations.
     *  Since we want to change state and also update the UI, we can use a MutableState.
     */
    val expanded = remember { mutableStateOf(false) }

    /**
     * Animate collapsing.
     * - Let's do something more fun like adding a spring-based animation
     * - The spring spec does not take any time-related parameters. Instead it relies on physical
     * properties (damping and stiffness) to make animations more natural.
     */
    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(12.dp)
        ) {
            Text(text = type)
            Text(text = "$name.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            if (expanded.value) {
                // Some random text here.
                Text(
                    text = (description)
                )
            }
        }

        IconButton(onClick = { expanded.value = !expanded.value }) {
            Icon(
                painter = if (expanded.value) painterResource(id = R.drawable.baseline_expand_less_24) else painterResource(id = R.drawable.baseline_expand_more_24),
                contentDescription = if (expanded.value) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun DefaultPreview() {
    ReminderAppTheme {
        ListItems()
    }
}