package com.example.hypercars.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.hypercars.repository.UserRepositoryImpl
import com.example.hypercars.viewmodel.UserViewModel
import com.example.hypercars.R

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                RegBody(innerPadding)
            }
        }
    }
}

@Composable
fun RegBody(innerPaddingValues: PaddingValues) {
    val repo = UserRepositoryImpl()
    val userViewModel = remember { UserViewModel(repo) }
    val context = LocalContext.current
    val activity = context as? Activity

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Select Role") }
    val roleOptions = listOf("User", "Admin")

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = Modifier
            .padding(innerPaddingValues)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("First Name") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                placeholder = { Text("Last Name") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        textFieldSize = it.size.toSize()
                    }
                    .clickable { expanded = true },
                placeholder = { Text("Select Role") },
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color.Gray,
                    disabledContainerColor = Color.White,
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                roleOptions.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        if (passwordVisibility) R.drawable.baseline_remove_red_eye_24 else R.drawable.baseline_visibility_off_24
                    ),
                    contentDescription = null,
                    modifier = Modifier.clickable { passwordVisibility = !passwordVisibility }
                )
            },
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank() || selectedRole == "Select Role") {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.register(email.trim(), password.trim()) { success, message, userId ->
                        if (success) {
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            activity?.finish()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Register")
        }
    }
}
