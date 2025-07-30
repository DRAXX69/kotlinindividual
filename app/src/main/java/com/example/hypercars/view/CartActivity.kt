package com.example.hypercars.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import coil.compose.rememberAsyncImagePainter

class CartActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartRepo = CartRepositoryImpl()
        val orderRepo = OrderRepositoryImpl()

        val cartFactory = CartViewModelFactory(cartRepo)
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        cartViewModel.loadCartItems()

        setContent {
            // Wrap with your app theme if you have one e.g. SportsEquipmentStoreTheme
            CartScreen(cartViewModel = cartViewModel, orderViewModel = orderViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(cartViewModel: CartViewModel, orderViewModel: OrderViewModel) {
    val cartItems by cartViewModel.cartItems.observeAsState(emptyList())
    val errorMessage by cartViewModel.error.observeAsState()
    val orderError by orderViewModel.error.observeAsState()
    val context = LocalContext.current

    val totalPrice = cartItems.sumOf { it.productPrice * it.quantity }

    LaunchedEffect(orderError) {
        orderError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            orderViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (!errorMessage.isNullOrEmpty()) {
                    Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = {
                                cartViewModel.updateQuantity(item.id, item.quantity + 1)
                            },
                            onDecrease = {
                                if (item.quantity > 1) {
                                    cartViewModel.updateQuantity(item.id, item.quantity - 1)
                                }
                            },
                            onRemove = {
                                cartViewModel.removeCartItem(item.id)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total:", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
                    Text("Rs. ${"%.2f".format(totalPrice)}", fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    onClick = {
                        if (cartItems.isNotEmpty()) {
                            val userId = "USR001" // replace with actual user ID
                            val order = OrderModel(
                                orderId = "",
                                userId = userId,
                                items = cartItems,
                                totalAmount = totalPrice,
                                orderStatus = "Pending"
                            )
                            orderViewModel.placeOrder(order)
                            Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                            // cartViewModel.clearCart() // Enable if you want to clear after order
                        } else {
                            Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Proceed to Checkout")
                }
            }
        }
    )
}

@Composable
fun CartItemCard(
    item: CartItemModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.image),
                contentDescription = item.productName,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.productName, fontSize = 16.sp, style = MaterialTheme.typography.titleMedium)
                Text(text = "Rs. ${item.productPrice}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(onClick = onDecrease, contentPadding = PaddingValues(4.dp)) {
                        Text("-")
                    }
                    Text(
                        text = "${item.quantity}",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 16.sp
                    )
                    Button(onClick = onIncrease, contentPadding = PaddingValues(4.dp)) {
                        Text("+")
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}
