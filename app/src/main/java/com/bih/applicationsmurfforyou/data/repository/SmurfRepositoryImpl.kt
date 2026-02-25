package com.bih.applicationsmurfforyou.data.repository

import android.util.Log
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.google.firebase.database.database
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmurfRepositoryImpl @Inject constructor() : SmurfRepository {

    private var cachedSmurfs: List<Smurf>? = null

    override suspend fun getAllSmurfs(forceRefresh: Boolean): List<Smurf> {
        if (forceRefresh) {
            cachedSmurfs = null
        }
        return cachedSmurfs ?: fetchAndCacheSmurfs()
    }

    override fun getSmurfByName(name: String): Smurf? {
        return cachedSmurfs?.firstOrNull { it.name == name }
    }

    private suspend fun fetchAndCacheSmurfs(): List<Smurf> {
        return try {
            val snapshot = Firebase.database.reference.child("smurfs").get().await()
            val smurfs = snapshot.children.mapNotNull { it.getValue(Smurf::class.java) }

            if (smurfs.isEmpty() && snapshot.hasChildren()) {
                val errorMessage = "Data mapping failed. Check that your 'Smurf' data class fields (name, description, imageUrl) EXACTLY match your Firebase Realtime Database JSON structure."
                Log.e("SmurfRepository", errorMessage)
                throw IllegalStateException(errorMessage)
            }
            
            cachedSmurfs = smurfs
            smurfs
        } catch (e: Exception) {
            Log.e("SmurfRepository", "Firebase fetch failed: ${e.message}")
            cachedSmurfs = null 
            throw e
        }
    }
}
