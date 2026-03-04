package com.gokhanaytekinn.sdandroid.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gokhanaytekinn.sdandroid.R

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.error)) },
        text = { Text(text = errorMessage) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
