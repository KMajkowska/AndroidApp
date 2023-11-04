package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*;
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.androidapp.ui.theme.AndroidAppTheme
import java.util.regex.Pattern

const val LOGIN_BUTTON_TEXT: String = "LOGIN"
const val SIGN_UP_BUTTON_TEXT: String = "SIGN UP"
const val PASSWORD_TEXT: String = "PASSWORD"
val EMAIL_ADDRESS_PATTERN: Pattern =
    Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+")

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAppTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current

    // Define variables to hold the input values
    var loginValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = loginValue,
            onValueChange = { loginValue = it },
            label = { Text(LOGIN_BUTTON_TEXT) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            label = { Text(PASSWORD_TEXT) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )
        Button(
            onClick = {
                if (passwordValue.isNotEmpty() && loginValue.isNotEmpty() && isValidString(
                        loginValue
                    )
                ) {
                    // Add logic for successful login
                    val intent = Intent(context, AllNotes::class.java)
                    context.startActivity(intent)
                }
            }
        ) {
            Text(LOGIN_BUTTON_TEXT)
        }
        Button(
            onClick = {
                // Add logic for button click
                val intent = Intent(context, SignUp::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(SIGN_UP_BUTTON_TEXT)
        }
    }
}

private fun isValidString(str: String): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AndroidAppTheme {
        LoginScreen()
    }
}
