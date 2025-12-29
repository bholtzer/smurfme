package com.bih.applicationsmurfforyou.domain.usecase

import com.bih.applicationsmurfforyou.domain.repository.SmurfRepository

class GetAllSmurfsUseCase(private val repo: SmurfRepository) {
     suspend operator fun invoke() = repo.getAllSmurfsCached()
}


