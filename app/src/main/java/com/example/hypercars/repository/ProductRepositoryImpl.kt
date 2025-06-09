package com.example.hypercars.repository

import com.example.hypercars.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductRepositoryImpl : ProductRepository {

    val database = FirebaseDatabase.getInstance()
    val ref = database.reference.child("products")

    override fun addProduct(
        model: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        val id = ref.push().key.toString()
        model.productId = id
        ref.child(model.productId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"product added successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).updateChildren(data).addOnCompleteListener {

            if (it.isSuccessful){
                callback(true,"product updated successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"product deleted successfully")
            }else{
                callback(false,"${it.exception?.message}")
            }
        }
    }

    override fun getProductById(
        productId: String,
        callback: (Boolean, String, ProductModel?) -> Unit
    ) {
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val product = snapshot.getValue(ProductModel::class.java)
                    if (product != null){
                        callback(true,"product fetched",product)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,null)
            }
        })
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModel?>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var allProducts = mutableListOf<ProductModel>()
                    for (eachProduct in snapshot.children){
                        var products = eachProduct.getValue(ProductModel::class.java)
                        if (products !=null){
                            allProducts.add(products)
                        }
                    }
                    callback(true,"product fetched", allProducts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,emptyList())
            }
        })
    }

}