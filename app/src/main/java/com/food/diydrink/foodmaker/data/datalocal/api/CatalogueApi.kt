package com.food.diydrink.foodmaker.data.datalocal.api

import com.food.diydrink.foodmaker.data.model.api.CharacterResponse
import com.food.diydrink.foodmaker.data.model.api.PartAPI
import retrofit2.Response
import retrofit2.http.GET

interface CatalogueApi {
    @GET("api/ST183_PrincessAvatarMaker")
    suspend fun getData(): Response<CharacterResponse>
}