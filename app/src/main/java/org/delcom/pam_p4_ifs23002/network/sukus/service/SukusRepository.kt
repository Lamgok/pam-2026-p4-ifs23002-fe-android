package org.delcom.pam_p4_ifs23002.network.sukus.service


import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper
import org.delcom.pam_p4_ifs23002.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSuku
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukuAdd
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukus
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseProfile

class SukusRepository (private val sukusApiService: SukusApiService): ISukusRepository {
    override suspend fun getProfile(): ResponseMessage<ResponseProfile?> {
        return SuspendHelper.safeApiCall {
            sukusApiService.getProfile()
        }
    }

    override suspend fun getAllNovels(search: String?): ResponseMessage<ResponseSukus?> {
        return SuspendHelper.safeApiCall {
            sukusApiService.getAllNovels(search)
        }
    }

    override suspend fun postSukus(
        nama: RequestBody,
        deskripsi: RequestBody,
        makanan: RequestBody,
        rumahadat: RequestBody,
        image: MultipartBody.Part
    ): ResponseMessage<ResponseSukuAdd?> {
        return SuspendHelper.safeApiCall {
            sukusApiService.postSukus(
                nama = nama,
                deskripsi = deskripsi,
                rumahadat = rumahadat,
                makanan = makanan,
                file = image
            )
        }
    }

    override suspend fun getSukusById(sukuId: String): ResponseMessage<ResponseSuku?> {
        return SuspendHelper.safeApiCall {
            sukusApiService.getNovelById(sukuId)
        }
    }

    override suspend fun putSukusById(
        sukuId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        makanan: RequestBody,
        rumahadat: RequestBody,
        image: MultipartBody.Part?
    ): ResponseMessage<ResponseSuku?> {
        return SuspendHelper.safeApiCall {
            val response = sukusApiService.putNovel(
                novelId = sukuId,
                nama = nama,
                deskripsi = deskripsi,
                makanan = makanan,
                rumahadat = rumahadat,
                file = image
            )
            ResponseMessage(data = null, message = response.message, status = response.status)
        }
    }

    override suspend fun deleteSukusById(sukuId: String): ResponseMessage<ResponseSuku?> {
        return SuspendHelper.safeApiCall {
            val response = sukusApiService.deleteNovel(sukuId)
            ResponseMessage(data = null, message = response.message, status = response.status)
        }
    }
}