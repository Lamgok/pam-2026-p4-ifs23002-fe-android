package org.delcom.pam_p4_ifs23002.helper

class ConstHelper {
    // Route Names
    enum class RouteNames(val path: String) {
        Home(path = "home"),
        Profile(path = "profile"),
        Plants(path = "plants"),
        PlantsAdd(path = "plants/add"),

        PlantsDetail(path = "plants/{plantId}"),
        PlantsEdit(path = "plants/{plantId}/edit"),

        Sukus(path = "sukus"),
        SukusDetail(path = "sukus/{sukuId}"),
        SukusAdd(path = "sukus/add"),
        SukusEdit(path = "sukus/{sukuId}/edit"),



    }
}