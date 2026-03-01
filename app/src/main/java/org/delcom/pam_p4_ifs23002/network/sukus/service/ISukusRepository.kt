package org.delcom.pam_p4_ifs23002.network.sukus.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23002.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSuku
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukuAdd
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukus
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseProfile

interface ISukusRepository {
    suspend fun getProfile(): ResponseMessage<ResponseProfile?>

    // Ambil semua data tumbuhan
    suspend fun getAllNovels(
        search: String? = null
    ): ResponseMessage<ResponseSukus?>

    suspend fun postSukus (
        nama: RequestBody,
        deskripsi: RequestBody,
        makanan: RequestBody,
        rumahadat: RequestBody,
        image: MultipartBody.Part,
    ): ResponseMessage<ResponseSukuAdd?>


    suspend fun getSukusById(
        sukuId: String
    ): ResponseMessage<ResponseSuku?>

    suspend fun putSukusById(
        sukuId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        makanan: RequestBody,
        rumahadat: RequestBody,
        image: MultipartBody.Part,
    ): ResponseMessage<ResponseSuku?>

    suspend fun deleteSukusById(
        sukuId: String
    ): ResponseMessage<ResponseSuku?>


}