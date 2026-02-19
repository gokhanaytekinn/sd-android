package com.gokhanaytekinn.sdandroid.data.api

import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStats
import retrofit2.Response
import retrofit2.http.*

interface SubscriptionApiService {
    
    @GET("/api/subscriptions")
    suspend fun getSubscriptions(): Response<List<Subscription>>
    
    @GET("/api/subscriptions/{id}")
    suspend fun getSubscription(@Path("id") id: String): Response<Subscription>
    
    @POST("/api/subscriptions")
    suspend fun createSubscription(@Body subscription: com.gokhanaytekinn.sdandroid.data.model.request.SubscriptionRequest): Response<Subscription>
    
    @PUT("/api/subscriptions/{id}")
    suspend fun updateSubscription(
        @Path("id") id: String,
        @Body subscription: Subscription
    ): Response<Subscription>
    
    @DELETE("/api/subscriptions/{id}")
    suspend fun deleteSubscription(@Path("id") id: String): Response<Unit>
    
    @GET("/api/subscriptions/stats")
    suspend fun getSubscriptionStats(): Response<SubscriptionStats>
}
