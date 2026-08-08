package com.food.diydrink.foodmaker.data.repository

import com.food.diydrink.foodmaker.data.datalocal.api.CatalogueApi
import com.food.diydrink.foodmaker.data.model.custom.CustomModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 * Repository chịu trách nhiệm gọi API liên quan đến CustomModel (characters)
 */
@Singleton
class ApiRepository @Inject constructor(
    private val catalogueApi: CatalogueApi) {
    suspend fun getCatalogue() = withContext(Dispatchers.IO) {
        catalogueApi.getData()
    }
}
