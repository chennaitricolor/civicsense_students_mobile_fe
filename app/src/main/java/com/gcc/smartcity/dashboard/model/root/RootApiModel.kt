package com.gcc.smartcity.dashboard.model.root

data class RegionModel(
    var termsAndConditions: String? = null,
    var minimumAndroidVersion: String? = null,
    var persona: ArrayList<String>? = null
)

data class Regions(var regionsMap: HashMap<String, RegionModel>? = null)

data class ContactUs(var email: String? = null, var phone: Int? = null)

data class Contributors(
    var name: String? = null,
    var email: String? = null,
    var meta: String? = null
)

class RootApiModel {
    var links: Links? = null
    var contactUs: ContactUs? = null
    var aboutus: String? = null
    var termsAndConditions: String? = null
    var contributors: List<Contributors>? = null
    var meta: String? = null
    var default: String? = null
    var region: Regions? = null
}