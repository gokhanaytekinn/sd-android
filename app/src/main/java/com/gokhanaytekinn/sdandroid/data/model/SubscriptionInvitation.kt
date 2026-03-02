package com.gokhanaytekinn.sdandroid.data.model

data class SubscriptionInvitation(
    val id: String,
    val subscriptionId: String,
    val subscriptionName: String?,
    val inviterId: String,
    val inviterName: String?,
    val inviteeEmail: String,
    val status: String,
    val createdAt: String
)
