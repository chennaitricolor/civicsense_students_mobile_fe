package com.gcc.smartcity.rewards

data class RewardsModel(
    val _id: String,
    val imageData: String,
    val rewardTitle: String,
    val rewardDescription: String,
    val balanceStatus: String,
    val balanceRequired: String,
    val gemTarget: String,
    val validTill: String
)