package com.example.hypercars.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlowimport kotlinx.coroutines.launch

class WishlistViewModel(private val repository: WishlistRepository) : ViewModel() {

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