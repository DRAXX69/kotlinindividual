package com.example.hypercars

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hypercars.R
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()

        }
    }
}

@Composable
fun LoginBody() {
//    var counter : Int = 0

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val activity = context as Activity


    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    val localEmail : String = sharedPreferences.getString("email","").toString()
    val localPassword : String = sharedPreferences.getString("password","").toString()

    email = localEmail
    password = localPassword


    val couroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDialog by remember { mutableStateOf(false) }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Trigger to show the dialog
            Button(onClick = { showDialog = true }) {
                Text("Show AlertDialog")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    }, // dismiss when clicked outside
                    confirmButton = {
                        Button(onClick = {
                            // Confirm action
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = {
                            // Cancel action
                            showDialog = false
                        }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text(text = "Alert Title") },
                    text = { Text("This is an alert dialog message.") }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(R.drawable.porsche),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                placeholder = {
                    Text(text = "Enter email")
                },
                //            minLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                    focusedIndicatorColor = Color.Green,
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                    unfocusedIndicatorColor = Color.Blue
                ),
                shape = RoundedCornerShape(12.dp),
                prefix = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                placeholder = {
                    Text(text = "Enter password")
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                    focusedIndicatorColor = Color.Green,
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                    unfocusedIndicatorColor = Color.Blue
                ),
                shape = RoundedCornerShape(12.dp),
                prefix = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },

                suffix = {
                    Icon(
                        painterResource(
                            if (passwordVisibility) R.drawable.baseline_remove_red_eye_24 else
                                R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            //2
                            passwordVisibility = !passwordVisibility

                        }
                    )
                },

                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green,
                            checkmarkColor = Color.White
                        ),
                        checked = rememberMe,
                        onCheckedChange = {
                            rememberMe = it
                        }
                    )
                    Text("Remember me")
                }

                Text("Forget Password?")
            }

            Button(
                onClick = {
                    if (email == "ram@gmail.com"
                        && password == "password"
                    ) {

                        if(rememberMe){
                            editor.putString("email",email)
                            editor.putString("password",password)
                            editor.apply()
                        }
                        val intent = Intent(context, RegistrationActivity::class.java)

                        //to pass data to another activity
                        intent.putExtra("email",email)
                        intent.putExtra("password",password)

                        context.startActivity(intent)

                        activity.finish()

                        Toast.makeText(
                            context, "Login success",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //snackbar
                        couroutineScope.launch {
                            snackbarHostState.showSnackbar("Invalid login")
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Login")
            }


            Text(
                "Don't have an account, Signup",
                modifier = Modifier.clickable {
                    val intent = Intent(context, RegistrationActivity::class.java)
                    context.startActivity(intent)

                    //to destroy activity
                    activity.finish()
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewLogin() {
    LoginBody()
}