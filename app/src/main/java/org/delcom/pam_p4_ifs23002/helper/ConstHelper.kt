package org.delcom.pam_p4_ifs23002.helper

object ConstHelper {
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
        SukusDetail(path = "sukus/{sukuId}"),
        SukusEdit(path = "sukus/{sukuId}/edit")
    }
}