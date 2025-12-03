package com.bih.applicationsmurfforyou.domain.usecase


import com.bih.applicationsmurfforyou.data.repository.SmurfImageRepository
import com.bih.applicationsmurfforyou.domain.model.SmurfImage
import com.bih.applicationsmurfforyou.domain.util.Result
import javax.inject.Inject

class GenerateSmurfImageUseCase @Inject constructor(
    private val repository: SmurfImageRepository
) {
    suspend operator fun invoke(description: String): Result<SmurfImage> {
        return repository.generateSmurfFromDescription(description)
    }
}