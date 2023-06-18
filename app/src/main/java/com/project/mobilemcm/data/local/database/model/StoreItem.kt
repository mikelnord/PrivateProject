package com.project.mobilemcm.data.local.database.model

data class StoreItem(
    val id: String,
    val name: String
){
    override fun toString(): String {
        return name
    }
}
