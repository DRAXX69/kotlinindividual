package com.example.hypercars.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hypercars.model.WishlistItemModel
import com.example.hypercars.repository.WishlistRepository
import com.example.hypercars.repository.WishlistRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WishlistViewModel(private val repository: WishlistRepositoryImpl) : ViewModel() {

    private val _wishlistItems = MutableStateFlow<List<WishlistItemModel>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItemModel>> = _wishlistItems

    init {
        viewModelScope.launch {
            repository.getWishlistItems().collect {
                _wishlistItems.value = it
            }
        }
    }

    fun addToWishlist(item: WishlistItemModel) {
        viewModelScope.launch {
            repository.addToWishlist(item)
        }
    }

    fun removeFromWishlist(item: WishlistItemModel) {
        viewModelScope.launch {
            repository.removeFromWishlist(item)
        }
    }
}