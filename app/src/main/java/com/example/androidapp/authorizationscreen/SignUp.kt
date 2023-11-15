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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidapp.ui.theme.AndroidAppTheme

const val USERNAME_TEXT: String = "USERNAME"
const val RETYPE_PASSWORD_TEXT : String = "RETYPE PASSWORD"

class SignUp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAppTheme {
                SignUpScreen()
            }
        }
    }
}

@Composable
fun SignUpScreen() {
    val context = LocalContext.current

    var loginValue by remember { mutableStateOf("") }
    var usernameValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var repeatedPasswordValue by remember { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isRepeatedPasswordVisible by rememberSaveable { mutableStateOf(false) }

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
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = usernameValue,
            onValueChange = { usernameValue = it },
            label = { Text(USERNAME_TEXT) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )
        OutlinedTextField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            label = { Text(PASSWORD_TEXT) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            placeholder = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isPasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (isPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = {isPasswordVisible = !isPasswordVisible}){
                    Icon(imageVector  = image, description)
                }
            }
        )
        OutlinedTextField(
            value = repeatedPasswordValue,
            onValueChange = { repeatedPasswordValue = it },
            label = { Text(RETYPE_PASSWORD_TEXT) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
            placeholder = { Text("Password") },
            visualTransformation = if (isRepeatedPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isRepeatedPasswordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (isRepeatedPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = {isRepeatedPasswordVisible = !isRepeatedPasswordVisible}){
                    Icon(imageVector  = image, description)
                }
            }
        )
        Button(
            onClick = {
                if (passwordValue.isNotEmpty() && passwordValue.equals(repeatedPasswordValue) && loginValue.isNotEmpty() && isValidString(
                        loginValue
                    )
                ) {
                    val intent = Intent(context, Login::class.java)
                    context.startActivity(intent)
                }
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
fun SignUpScreenPreview() {
    AndroidAppTheme {
        SignUpScreen()
    }
}