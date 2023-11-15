package com.example.androidapp.authorizationscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidapp.MainActivity
import com.example.androidapp.ui.theme.AndroidAppTheme

const val SEND_CODE_BUTTON_TEXT : String = "SEND CODE"
const val EMAIL_VALUE_TEXT : String = "ENTER EMAIL"

class RestorePassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAppTheme {
                RestorePasswordScreen()
            }
        }
    }
}

@Composable
fun RestorePasswordScreen() {
    val context = LocalContext.current

    var emailValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = emailValue,
            onValueChange = { emailValue = it },
            label = { Text(EMAIL_VALUE_TEXT) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(SEND_CODE_BUTTON_TEXT)
        }
    }
}

private fun isValidString(str: String): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
}

@Preview(showBackground = true)
@Composable
fun RestorePasswordScreenPreview() {
    AndroidAppTheme {
        RestorePasswordScreen()
    }
}