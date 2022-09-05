package ru.cherepanov.apps.dictionary.data.network

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryWebService {
    @GET("/findTitlesHavingPrefix/{prefix}")
    fun findTitlesHavingPrefix(
        @Path("prefix")
        prefix: String
    ): Single<List<String>>

    @GET("/getShortGlossList/{title}")
    fun getShortGlossList(
        @Path("title")
        title: String
    ): Single<List<WordDefDto>>

    @GET("/getFullGloss/{title}/{langNum}/{senseNum}/{glossNum}")
    fun getFullGloss(
        @Path("title")
        title: String,
        @Path("langNum")
        langNum: Int,
        @Path("senseNum")
        senseNum: Int,
        @Path("glossNum")
        glossNum: Int
    ): Single<WordDefDto>

    @GET("/getShortGlossListByRandomTitle")
    fun getShortGlossListByRandomTitle(
    ): Single<List<WordDefDto>>
}