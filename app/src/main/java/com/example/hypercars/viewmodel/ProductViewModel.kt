package com.example.hypercars.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hypercars.model.ProductModel
import com.example.hypercars.repository.ProductRepository

class ProductViewModel(val repo : ProductRepository): ViewModel() {

    fun addProduct(model: ProductModel,
                   callback: (Boolean, String)-> Unit){
        repo.addProduct(model,callback)
    }

    fun updateProduct(
        productId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ){
        repo.updateProduct(productId,data,callback)
    }


    fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ){
        repo.deleteProduct(productId,callback)
    }

    private val _products = MutableLiveData<ProductModel?>()
    val products : LiveData<ProductModel?>get() = _products

    private val _allProducts = MutableLiveData<List<ProductModel?>>()
    val allProducts: LiveData<List<ProductModel?>> get() = _allProducts


    fun getProductById(
        productId: String,
    ){
        repo.getProductById (productId){ success, msg, data->
            if (success){
                _products.postValue(data)
            }else{
                _products.postValue(null)
            }
        }
    }

    fun getAllProduct(){}

}
