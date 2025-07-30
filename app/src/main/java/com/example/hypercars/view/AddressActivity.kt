package com.example.hypercars.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hypercars.model.AddressModel
import com.example.hypercars.repository.AddressRepositoryImpl
import com.example.hypercars.viewmodel.AddressViewModel
import java.util.UUID

class AddressActivity : ComponentActivity() {

    private val greenColor = Color(0xFF4CAF50)  // Same green as UserDashboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(greenColor)
            ) {
                AddressScreen(userId = "demo_user_id") // Replace with real userId
            }
        }
    }
}

@Composable
fun AddressScreen(userId: String) {
    val greenColor = Color(0xFF4CAF50)  // Same green as UserDashboard
    val viewModel = remember { AddressViewModel(AddressRepositoryImpl()) }
    val context = LocalContext.current

    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var addresses by remember { mutableStateOf(emptyList<AddressModel>()) }

    fun loadAddresses() {
        viewModel.getAddresses(userId) {
            addresses = it
        }
    }

    LaunchedEffect(Unit) {
        loadAddresses()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(greenColor)
    ) {

        Text(
            text = if (selectedId == null) "Add Address" else "Update Address",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = addressLine,
            onValueChange = { addressLine = it },
            label = { Text("Address Line") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("District") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("Postal Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val id = selectedId ?: UUID.randomUUID().toString()
                val address = AddressModel(
                    id = id,
                    userId = userId,
                    street = addressLine,
                    city = city,
                    district = state,
                    postalCode = postalCode
                )

                val action = if (selectedId == null) viewModel::addAddress else viewModel::updateAddress

                action(address) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        addressLine = ""
                        city = ""
                        state = ""
                        postalCode = ""
                        selectedId = null
                        loadAddresses()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedId == null) "Save Address" else "Update Address")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Saved Addresses", style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))

        addresses.forEach { address ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Address: ${address.street}")
                    Text("City: ${address.city}")
                    Text("District: ${address.district}")
                    Text("Postal Code: ${address.postalCode}")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            "Edit",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                selectedId = address.id
                                addressLine = address.street
                                city = address.city
                                state = address.district
                                postalCode = address.postalCode
                            }
                        )
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.clickable {
                                viewModel.deleteAddress(address.id) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) loadAddresses()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
