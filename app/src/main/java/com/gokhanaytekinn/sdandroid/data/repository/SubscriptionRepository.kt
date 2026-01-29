package com.gokhanaytekinn.sdandroid.data.repository

import com.gokhanaytekinn.sdandroid.data.api.ApiClient
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.SubscriptionStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubscriptionRepository {
    
    private val api = ApiClient.subscriptionApi
    
    suspend fun getSubscriptions(): Result<List<Subscription>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSubscriptions()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch subscriptions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSubscription(id: String): Result<Subscription> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSubscription(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createSubscription(subscription: Subscription): Result<Subscription> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.createSubscription(subscription)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to create subscription"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun updateSubscription(id: String, subscription: Subscription): Result<Subscription> = 
        withContext(Dispatchers.IO) {
            try {
                val response = api.updateSubscription(id, subscription)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to update subscription"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun deleteSubscription(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteSubscription(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSubscriptionStats(): Result<SubscriptionStats> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSubscriptionStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch subscription stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
