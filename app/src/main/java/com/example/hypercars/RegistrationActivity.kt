package com.example.hypercars

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.hypercars.ui.theme.HypercarsTheme
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotApplyResult.Success
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.hypercars.model.UserModel
import com.example.hypercars.repository.UserRepositoryImpl
import com.example.hypercars.viewmodel.UserViewModel

class RegistrationActivity : ComponentActivity() {
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

    val repo = remember { UserRepositoryImpl() }
    val UserViewModel = remember { UserViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity



    var firstName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var selectedOptionText by remember { mutableStateOf("Select Option") }

    val options = listOf("Nepal", "India", "China")

    var textFieldSize by remember { mutableStateOf(Size.Zero) } // to capture textfield size
    Column(
        modifier = Modifier
            .padding(innerPaddingValues).padding(horizontal = 10.dp)
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    firstName = it
                },
                placeholder = {
                    Text("Firstname")
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = lastname,
                onValueChange = {
                    lastname = it
                },
                placeholder = {
                    Text("Lastname")
                },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            placeholder = {
                Text("abc@gmail.com")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()) {
            OutlinedTextField(
                value = selectedOptionText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        // capture the size of the TextField
                        textFieldSize = coordinates.size.toSize()
                    }
                    .clickable { expanded = true },
                placeholder = { Text("Select Country") },
                enabled = false, // prevent manual typing
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color.Gray,
                    disabledContainerColor = Color.White,
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOptionText = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            placeholder = {
                Text("*******")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            UserViewModel.register(email,password){
                success,message,userId ->
                if (success){
                    var userModel = UserModel(
                        userId,email,
                        firstName,lastname,
                        "Male",
                         selectedOptionText
                    )
                    UserViewModel.addUserToDatabase (userId,userModel){
                        success, message ->
                        if (success){
                            Toast.makeText(context,message, Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(context,message, Toast.LENGTH_LONG).show()

                }

            }
        },
            modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }

    }
}

@Preview
@Composable
fun RegPreview() {
    RegBody(innerPaddingValues = PaddingValues(0.dp))
}