package com.bih.applicationsmurfforyou.domain.repository

import com.bih.applicationsmurfforyou.domain.model.Smurf

/**
 * The single source of truth for all Smurf data.
 */
interface SmurfRepository {

    /**
     * Gets the list of all smurfs.
     * @param forceRefresh If true, the cache will be bypassed and data will be fetched from the network.
     * @return The list of Smurfs.
     */
    suspend fun getAllSmurfs(forceRefresh: Boolean = false): List<Smurf>
}
