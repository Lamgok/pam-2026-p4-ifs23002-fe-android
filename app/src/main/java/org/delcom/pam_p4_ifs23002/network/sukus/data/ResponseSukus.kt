package org.delcom.pam_p4_ifs23002.network.sukus.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseSukus (
    val sukus: List<ResponseSukusData>,
)

@Serializable
data class ResponseSuku (
    val suku: ResponseSukusData
)

@Serializable
data class ResponseSukuAdd (
    val sukuId : String
)

@Serializable
data class ResponseSukusData (
    val id: String,
    val nama: String,
    val deskripsi: String,
    val makanan: String,
    val rumahadat: String,
    val createdAt: String,
    val updatedAt: String,
)
