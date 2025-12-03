package com.bih.applicationsmurfforyou.domain.model

data class Smurf(val name: String, val description: String, val image: String) {

   /* companion object {
        private const val BUCKET = "applicationsmurfforyou.firebasestorage.app"
    }

    val imageUrl: String
         get() = "https://firebasestorage.googleapis.com/v0/b/$BUCKET/o/smurfs%2F${image}?alt=media"*/

}