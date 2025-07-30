package com.example.hypercars.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hypercars.repository.ProductRepositoryImpl
import com.example.hypercars.viewmodel.ProductViewModel

class UpdateProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UpdateProductBody()
        }
    }
}

@Composable
fun UpdateProductBody() {
    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val context = LocalContext.current
    val activity = context as? Activity

    val productId: String? = activity?.intent?.getStringExtra("productId")

    // Observe product LiveData as Compose state
    val product by viewModel.product.observeAsState()

    // Local editable states for form fields
    var pName by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }

    // Load product details once when productId is available
    LaunchedEffect(productId) {
        productId?.let {
            viewModel.getProductById(it)
        }
    }

    // Update local states when product is loaded
    LaunchedEffect(product) {
        product?.let {
            pName = it.productName ?: ""
            pPrice = it.productPrice?.toString() ?: ""
            pDesc = it.productDescription ?: ""
        }
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Green)
        ) {
            item {
                OutlinedTextField(
                    value = pName,
                    onValueChange = { pName = it },
                    placeholder = { Text("Update product name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pPrice,
                    onValueChange = { pPrice = it },
                    placeholder = { Text("Update price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pDesc,
                    onValueChange = { pDesc = it },
                    placeholder = { Text("Update Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val priceDouble = pPrice.toDoubleOrNull() ?: 0.0
                        if (productId == null) {
                            Toast.makeText(context, "Invalid product ID", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        val updateData = mutableMapOf<String, Any?>(
                            "productName" to pName,
                            "productPrice" to priceDouble,
                            "productDescription" to pDesc
                        )
                        viewModel.updateProduct(productId, updateData) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) {
                                activity?.finish()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Product")
                }
            }
        }
    }
}
