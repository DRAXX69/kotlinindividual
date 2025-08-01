package com.example.hypercars.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.hypercars.R
import com.example.hypercars.model.ProductModel
import com.example.hypercars.repository.ProductRepositoryImpl
import com.example.hypercars.viewmodel.ProductViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val activity = context as? Activity

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    val products by viewModel.allProducts.observeAsState(initial = emptyList())
    val loading by viewModel.loading.observeAsState(initial = true)

    LaunchedEffect(Unit) {
        viewModel.getAllProducts()
    }

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = Color.Black) },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val intent = Intent(context, AddProductActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Red) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        context.startActivity(Intent(context, OrderActivity::class.java))
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = "Orders") },
                    label = { Text("Orders") }
                )
            }
        }
    ) { innerPadding ->
        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black, Color.Red)
                        )
                    )
            ) {
                if (loading || products.isEmpty()) {
                    items(3) { index ->
                        val cars = listOf(
                            Triple("Bugatti Chiron", "Top Speed: 420 km/h", "$5,500,000"),
                            Triple("Konigsegg Jesko", "Top Speed: 483 km/h", "$5,000,000"),
                            Triple("Lamborghini Sian", "Hybrid V12, 819 HP", "$4,000,000")
                        )
                        val images = listOf(
                            R.drawable.chiron,
                            R.drawable.jesko,
                            R.drawable.sian
                        )
                        val (name, specs, price) = cars[index]
                        ProductCard(name, specs, price, {}, {}, false, images[index])
                    }
                } else {
                    items(products) { product ->
                        product?.let {
                            ProductCard(
                                title = it.productName,
                                subtitle = it.productDescription,
                                price = "Rs. ${it.productPrice}",
                                onEdit = {
                                    val intent = Intent(context, UpdateProductActivity::class.java)
                                    intent.putExtra("productId", it.productId)
                                    context.startActivity(intent)
                                },
                                onDelete = {
                                    viewModel.deleteProduct(it.productId) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                },
                                editable = true,
                                imageRes = R.drawable.retrocruglogo
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    title: String,
    subtitle: String,
    price: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    editable: Boolean,
    imageRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.Black)
                Text(text = subtitle, color = Color.DarkGray)
                Text(text = price, color = Color(0xFF1B5E20))

                if (editable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onEdit,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Product")
                        }

                        IconButton(
                            onClick = onDelete,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Product")
                        }
                    }
                }
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}