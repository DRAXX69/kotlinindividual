package com.example.hypercars

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hypercars.model.ProductModel
import com.example.hypercars.repository.ProductRepository
import com.example.hypercars.repository.ProductRepositoryImpl
import com.example.hypercars.viewmodel.ProductViewModel

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AddProductBody()
        }
    }
}

@Composable
fun AddProductBody() {
    var pName by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity


    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                OutlinedTextField(
                    value = pName,
                    onValueChange = {
                        pName = it
                    },
                    placeholder = {
                        Text("Enter product name")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pPrice,
                    onValueChange = {
                        pPrice = it
                    },
                    placeholder = {
                        Text("Enter price")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pDesc,
                    onValueChange = {
                        pDesc = it
                    },
                    placeholder = {
                        Text("Enter Description")
                    },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val model = ProductModel("", pName,
                            pPrice.toDouble(),pDesc)
                        viewModel.addProduct (model){
                            success,msg->
                            if (success){
                                Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
                                activity?.finish()
                            }else{
                                Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
                            }
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Product")
                }
            }
        }

    }
}

@Preview
@Composable
fun previewAddProductBody() {
    AddProductBody()
}