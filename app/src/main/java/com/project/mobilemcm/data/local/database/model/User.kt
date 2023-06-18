package com.project.mobilemcm.data.local.database.model

data class User(
    val id:String,
    val login:String,
    val password:String,
    val name:String,
    val deletionmark:Boolean,
    val division_id:String
)


//"id": "ef1ebcbd-d7bd-11e6-937f-001517ecc118",
//"login": "ivanova",
//"password": "333",
//"name": "Иванова Яна Валерьевна",
//"deletionmark": false,
//"store_id": "a4786126-c672-11e6-80fe-001e67921ce8"