package org.delcom.pam_p4_ifs23002.helper

object ConstHelper { // Gunakan 'object' agar bisa diakses langsung tanpa instansiasi
    // Route Names
    enum class RouteNames(val path: String) {
        Home(path = "home"),
        Profile(path = "profile"),

        // Plants
        Plants(path = "plants"),
        PlantsAdd(path = "plants/add"),
        PlantsDetail(path = "plants/{plantId}"),
        PlantsEdit(path = "plants/{plantId}/edit"),

        // Sukus
        Sukus(path = "sukus"),
        SukusAdd(path = "sukus/add"),
        // Pastikan placeholder ini {sukuId} sesuai dengan yang dipanggil di SukusScreen.kt
        SukusDetail(path = "sukus/detail/{sukuId}"),
        SukusEdit(path = "sukus/edit/{sukuId}")
    }
}