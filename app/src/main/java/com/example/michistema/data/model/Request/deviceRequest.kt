package com.example.michistema.data.model.Request

data class deviceRequest (
   val name: String,
   val description: String?,
   val code: String?,
   val constant: Int?,
   val active: Boolean
)