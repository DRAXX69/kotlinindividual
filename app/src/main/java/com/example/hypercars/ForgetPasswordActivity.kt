package com.example.hypercars

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hypercars.ui.theme.HypercarsTheme

class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                ForgetBody(innerPadding)
            }
            }
        }
    }


@Composable
fun ForgetBody(innerPaddingValues: PaddingValues) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column (modifier = Modifier
        .padding(innerPaddingValues).padding(horizontal = 10.dp)
        .fillMaxSize()
        .background(color = Color.White)){
        Row (verticalAlignment = Alignment.CenterVertically){

            Spacer(modifier = Modifier.height(100.dp))
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
            Spacer(modifier = Modifier.height(10.dp))

        }
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
        Button(onClick = {},
            modifier = Modifier.fillMaxWidth()) {
            Text("Reset")
        }

    }


}

@Preview(showBackground = true)
@Composable
fun ForgetPreview() {
    ForgetBody(innerPaddingValues = PaddingValues(0.dp))

}