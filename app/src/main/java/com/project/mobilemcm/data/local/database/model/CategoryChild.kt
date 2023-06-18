package com.project.mobilemcm.data.local.database.model

data class DomainCategoryChild(
    val parentName:String,
    val childList:List<DomainCategory>
)