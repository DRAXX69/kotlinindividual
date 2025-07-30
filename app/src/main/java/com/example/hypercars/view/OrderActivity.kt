package com.example.hypercars.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.hypercars.model.OrderModel
import com.example.hypercars.repository.OrderRepositoryImpl
import com.example.hypercars.view.ui.theme.HypercarsTheme
import com.example.hypercars.viewmodel.OrderViewModel
import com.example.hypercars.viewmodel.OrderViewModelFactory

class OrderActivity : ComponentActivity() {

    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orderRepo = OrderRepositoryImpl()
        val orderFactory = OrderViewModelFactory(orderRepo)
        orderViewModel = ViewModelProvider(this, orderFactory)[OrderViewModel::class.java]

        orderViewModel.loadAllOrders()

        setContent {
            HypercarsTheme() {
                OrderScreen(orderViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(orderViewModel: OrderViewModel) {
    val orders by orderViewModel.allOrders.observeAsState(emptyList())
    val error by orderViewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (!error.isNullOrEmpty()) {
                Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
            }

            if (orders.isEmpty()) {
                Text("No orders found.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn {
                    items(orders) { order ->
                        OrderCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.orderId}", style = MaterialTheme.typography.titleMedium)
            Text("Status: ${order.orderStatus}")
            Text("Total: Rs. ${order.totalAmount}")
        }
    }
}
