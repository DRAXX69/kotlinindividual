package com.example.hypercars.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.hypercars.model.OrderModel
import com.example.hypercars.repository.OrderRepositoryImpl
import com.example.hypercars.view.ui.theme.HypercarsTheme
import com.example.hypercars.viewmodel.OrderViewModel
import com.example.hypercars.viewmodel.OrderViewModelFactory

class UserOrderActivity : ComponentActivity() {

    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get userId passed via Intent
        val userId = intent.getStringExtra("userId") ?: ""

        val orderRepo = OrderRepositoryImpl()
        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        setContent {
            HypercarsTheme {
                UserOrderScreen(userId, orderViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOrderScreen(userId: String, orderViewModel: OrderViewModel) {
    val orders by orderViewModel.userOrders.observeAsState(emptyList())
    val context = LocalContext.current

    LaunchedEffect(userId) {
        orderViewModel.loadOrdersByUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found.")
                }
            } else {
                LazyColumn {
                    items(orders.size) { index ->
                        OrderCardWithCancel(
                            order = orders[index],
                            onCancel = {
                                orderViewModel.cancelOrder(
                                    orders[index].orderId,
                                    userId
                                )
                                Toast.makeText(context, "Order cancelled", Toast.LENGTH_SHORT).show()
                                orderViewModel.loadOrdersByUser(userId) // refresh list
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCardWithCancel(order: OrderModel, onCancel: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.orderId}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${order.orderStatus}")
            Text("Total: Rs. ${order.totalAmount}")

            Spacer(modifier = Modifier.height(8.dp))

            if (order.orderStatus != "Cancelled") {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancel Order")
                }
            }
        }
    }
}
