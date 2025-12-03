package com.bih.applicationsmurfforyou.domain.repository

import com.bih.applicationsmurfforyou.domain.model.Smurf

/**
 * Domain repository interface.
 */
interface SmurfRepository {
    suspend fun getAllSmurfs(): List<Smurf>
    suspend fun smurfImage(imageUrl: String): String
}



