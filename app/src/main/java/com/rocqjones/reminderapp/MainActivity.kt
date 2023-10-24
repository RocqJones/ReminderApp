package com.rocqjones.reminderapp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.rocqjones.reminderapp.data.DataSource
import com.rocqjones.reminderapp.models.ComposeRandomItem
import com.rocqjones.reminderapp.ui.dialogs.ReminderDialog
import com.rocqjones.reminderapp.ui.theme.ReminderAppTheme

class MainActivity : ComponentActivity() {

    companion object {
        private var TAG = "MainActivity"
        const val REQUEST_CODE_NOTIFICATION_PERMISSIONS = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getNotificationPermissions()

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

    private fun getNotificationPermissions() {
        try {
            // Check if the app already has the permissions.
            val hasAccessNotificationPolicyPermission =
                checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED
            val hasPostNotificationsPermission =
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

            // If the app doesn't have the permissions, request them.
            when {
                !hasAccessNotificationPolicyPermission || !hasPostNotificationsPermission -> {
                    // Request the permissions.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                            Manifest.permission.POST_NOTIFICATIONS
                        ),
                        REQUEST_CODE_NOTIFICATION_PERMISSIONS
                    )
                }
                else -> {
                    // proceed
                    Log.d(TAG, "Notification Permissions : previously granted successfully")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            // Check if the user granted the permissions.
            REQUEST_CODE_NOTIFICATION_PERMISSIONS -> {
                val hasAccessNotificationPolicyPermission =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val hasPostNotificationsPermission =
                    grantResults[1] == PackageManager.PERMISSION_GRANTED

                // If the user denied the permissions, show a check.
                when {
                    !hasAccessNotificationPolicyPermission || !hasPostNotificationsPermission -> {
                        getNotificationPermissions()
                    }
                    else -> {
                        Log.d(TAG, "Notification Permissions : Granted successfully")
                    }
                }
            }
        }
    }
}

@Composable
fun ListItems(
    modifier: Modifier = Modifier,
    data: List<ComposeRandomItem> = DataSource.plants.map { it },  // names: List<String> = List(30) { "$it" }
) {
    /**
     * Let's create a performant lazy list.
     * Note that these are the equivalent component to RecyclerView or ListView from Views in Compose.
     */
    LazyColumn(
        modifier = modifier
            .padding(vertical = 4.dp)
            .testTag("list_items")
    ) {
        itemsIndexed(items = data.toMutableList()) { index, n ->
            ComposeCard(
                name = n.name,
                type = n.type,
                description = n.description,
                modifier = Modifier.testTag("composeCard_$index")
            )
        }
    }
}

@Composable
fun ComposeCard(name: String, type: String, description: String, modifier: Modifier) {
    val dialogState = remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable {
                dialogState.value = true
            }
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
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            Text(text = type)
            Text(
                text = "$name.",
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
                painter = if (expanded.value) painterResource(id = R.drawable.baseline_expand_less_24) else painterResource(
                    id = R.drawable.baseline_expand_more_24
                ),
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