package com.gokhanaytekinn.sdandroid.data.repository

import android.content.Context
import com.gokhanaytekinn.sdandroid.data.api.ApiService
import com.gokhanaytekinn.sdandroid.data.api.RetrofitClient
import com.gokhanaytekinn.sdandroid.data.local.TokenManager
import com.gokhanaytekinn.sdandroid.data.model.Subscription
import com.gokhanaytekinn.sdandroid.data.model.BillingCycle
import com.gokhanaytekinn.sdandroid.data.model.request.FlagSuspiciousRequest
import com.gokhanaytekinn.sdandroid.data.model.request.SubscriptionRequest
import com.gokhanaytekinn.sdandroid.data.model.response.SubscriptionResponse

class SubscriptionRepository(context: Context? = null) {
    
    private val tokenManager = context?.let { TokenManager(it) }
    private val apiService: ApiService? = tokenManager?.let { RetrofitClient.createApiService(it) }
    
    suspend fun getSubscriptions(
        status: String? = null,
        isSuspicious: Boolean? = null
    ): Result<List<Subscription>> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API service not initialized. Please login first."))
            }
            val response = apiService.getSubscriptions(status, isSuspicious)
            if (response.isSuccessful && response.body() != null) {
                val subscriptions = response.body()!!.map { it.toSubscription() }
                Result.success(subscriptions)
            } else {
                Result.failure(Exception("Failed to fetch subscriptions: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
    
    suspend fun getSubscription(id: String): Result<Subscription> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API not available"))
            }
            val response = apiService.getSubscription(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toSubscription())
            } else {
                Result.failure(Exception("Failed to get subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSuspiciousSubscriptions(): Result<List<Subscription>> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API not available"))
            }
            val response = apiService.getSuspiciousSubscriptions()
            if (response.isSuccessful && response.body() != null) {
                val subscriptions = response.body()!!.map { it.toSubscription() }
                Result.success(subscriptions)
            } else {
                Result.failure(Exception("Failed to get suspicious subscriptions"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createSubscription(subscription: Subscription): Result<Subscription> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API not available"))
            }
            val request = SubscriptionRequest(
                name = subscription.name,
                description = subscription.description,
                cost = subscription.cost,
                currency = subscription.currency,
                billingCycle = subscription.billingCycle.name,
                nextBillingDate = subscription.nextBillingDate,
                category = subscription.category,
                icon = subscription.icon,
                backgroundColor = subscription.backgroundColor
            )
            val response = apiService.createSubscription(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toSubscription())
            } else {
                Result.failure(Exception("Failed to create subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun approveSubscription(id: String): Result<Subscription> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API not available"))
            }
            val response = apiService.approveSubscription(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toSubscription())
            } else {
                Result.failure(Exception("Failed to approve subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun flagAsSuspicious(id: String, isSuspicious: Boolean, reason: String? = null): Result<Subscription> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API not available"))
            }
            val request = FlagSuspiciousRequest(isSuspicious, reason)
            val response = apiService.flagAsSuspicious(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toSubscription())
            } else {
                Result.failure(Exception("Failed to flag subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelSubscription(id: String): Result<Unit> {
        return try {
            if (apiService == null) {
                return Result.failure(Exception("API not available"))
            }
            val response = apiService.cancelSubscription(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to cancel subscription"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Helper function to convert API response to domain model
    private fun SubscriptionResponse.toSubscription(): Subscription {
        return Subscription(
            id = id,
            name = name,
            description = description,
            cost = cost,
            currency = currency,
            billingCycle = try {
                BillingCycle.valueOf(billingCycle)
            } catch (e: Exception) {
                BillingCycle.MONTHLY
            },
            nextBillingDate = nextBillingDate,
            category = category,
            isActive = status == "ACTIVE",
            isSuspicious = isSuspicious,
            icon = icon,
            backgroundColor = backgroundColor
        )
    }
}
