package com.bih.applicationsmurfforyou.data.repository

import android.util.Log
import com.bih.applicationsmurfforyou.domain.model.Smurf
import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmurfRepositoryImpl @Inject constructor() : SmurfRepository {

    private var cachedSmurfs: List<Smurf>? = null

    override suspend fun getAllSmurfs(forceRefresh: Boolean): List<Smurf> {
        // If a refresh is forced OR the cache is empty, fetch new data.
        if (forceRefresh || cachedSmurfs == null) {
            cachedSmurfs = fetchAndCacheSmurfs()
        }
        // Return the (potentially updated) cache.
        return cachedSmurfs ?: emptyList()
    }

    private suspend fun fetchAndCacheSmurfs(): List<Smurf> {
        return try {
            val snapshot = Firebase.database.reference.child("smurfs").get().await()
            val smurfs = snapshot.children.mapNotNull { it.getValue(Smurf::class.java) }

            if (smurfs.isEmpty()) {
                Log.w("SmurfRepository", "Firebase fetch succeeded but returned an empty list. Check that your 'Smurf' data class fields (name, description, imageUrl) EXACTLY match your Firebase Realtime Database JSON structure.")
            }
            smurfs
        } catch (e: Exception) {
            Log.e("SmurfRepository", "Firebase fetch failed: ${e.message}")
            // In case of an error, don't cache the failure. Let the next call try again.
            cachedSmurfs = null
            throw e // Re-throw the exception so the ViewModel can handle it.
        }
    }
}
