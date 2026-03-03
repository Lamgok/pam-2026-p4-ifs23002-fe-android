package org.delcom.pam_p4_ifs23002.network.sukus.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23002.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSuku
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukuAdd
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukus
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseProfile
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface SukusApiService {
    @GET ("profile")
    suspend fun getProfile (): ResponseMessage<ResponseProfile?>

    @GET("sukus")
    suspend fun getAllNovels(
        @Query("search") search: String? = null
    ): ResponseMessage<ResponseSukus?>

    @Multipart
    @POST ("/sukus")
    suspend fun postSukus (
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("makanan") makanan: RequestBody,
        @Part("rumahadat") rumahadat: RequestBody,
        @Part file: MultipartBody.Part
    ): ResponseMessage<ResponseSukuAdd?>

    @GET("sukus/{sukusId}")
    suspend fun getNovelById(
        @Path("sukusId") sukusId: String
    ): ResponseMessage<ResponseSuku?>

    @Multipart
    @PUT("sukus/{sukusId}")
    suspend fun putNovel(
        @Path("sukusId") novelId: String,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("makanan") makanan: RequestBody,
        @Part("rumahadat") rumahadat: RequestBody,
        @Part file: MultipartBody.Part? = null // Pastikan nama parameter 'file' sesuai dengan BE
    ): ResponseMessage<String?>

    @DELETE("sukus/{sukusId}")
    suspend fun deleteNovel(
        @Path("sukusId") sukusId: String
    ): ResponseMessage<String?>
}
