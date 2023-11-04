package com.example.androidapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText;
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )

    private fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonClickLogin = findViewById<Button>(R.id.loginButton)
        buttonClickLogin.setOnClickListener {
            val loginText = findViewById<EditText>(R.id.login)
            val loginValue = loginText.text.toString()
            val passwordText = findViewById<EditText>(R.id.password)
            val passwordValue = passwordText.text.toString()
            if(passwordValue != "" && loginValue != ""){
                if(isValidString(loginValue)) {
                    val intent = Intent(this, AllNotes::class.java)
                    startActivity(intent)
                }
            }
        }

        val buttonClickSignUp = findViewById<Button>(R.id.signUpButton)
        buttonClickSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}