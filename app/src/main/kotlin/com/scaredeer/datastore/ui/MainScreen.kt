package com.scaredeer.datastore.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scaredeer.datastore.data.GameController.BUTTONS

/*
 * cf. Screen level composable
 * https://github.com/google-developer-training/basic-android-kotlin-compose-training-dessert-release/blob/main/app/src/main/java/com/example/dessertrelease/ui/DessertReleaseScreen.kt
 */

@Composable
fun MainScreen(
    assignee: String?,
    assignTo: (String?) -> Unit,
) {
    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        // cf. ラジオボタン
        // https://developer.android.com/develop/ui/compose/components/radio-button?hl=ja

        Column(
            Modifier
                .padding(innerPadding)
                .selectableGroup()
            // Note that Modifier.selectableGroup() is essential
            // to ensure correct accessibility behavior
        ) {
            Text("assigned to: $assignee", Modifier.padding(16.dp))

            BUTTONS.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .wrapContentHeight()
                        .selectable(
                            selected = (text == assignee),
                            onClick = {
                                assignTo(text)
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == assignee),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun MainAppPreview() {
    MaterialTheme {
        MainScreen(
            assignee = BUTTONS[5],
            assignTo = {}
        )
    }
}