package com.sectra.noiseshield.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.sectra.noiseshield.R

@Composable
fun NoConfigurationFileDialog(
    showDialog: MutableState<Boolean>,
    title: String,
    message: String,
    onConfirm: (Boolean) -> Unit,
) {
    if (showDialog.value) {
        var doNotRemind by remember {
            mutableStateOf(false)
        }

        AlertDialog(onDismissRequest = { showDialog.value = false },
            title = { Text(text = title) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = message)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.do_not_remind_me_again))
                        Checkbox(
                            checked = doNotRemind,
                            onCheckedChange = { doNotRemind = !doNotRemind })
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onConfirm(doNotRemind)
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}