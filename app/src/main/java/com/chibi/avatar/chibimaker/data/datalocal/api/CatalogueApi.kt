package com.chibi.avatar.chibimaker.data.datalocal.api

import com.chibi.avatar.chibimaker.data.model.api.CharacterResponse
import com.chibi.avatar.chibimaker.data.model.api.PartAPI
import retrofit2.Response
import retrofit2.http.GET

interface CatalogueApi {
    @GET("api/ST183_PrincessAvatarMaker")
    suspend fun getData(): Response<CharacterResponse>
}