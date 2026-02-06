package com.atomicanalyst.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {
    @Test
    fun testIdle_IsSingleton() {
        val resource: Resource<Int> = Resource.Idle

        assertTrue(resource is Resource.Idle)
    }

    @Test
    fun testLoading_IsSingleton() {
        val resource: Resource<Int> = Resource.Loading

        assertTrue(resource is Resource.Loading)
    }

    @Test
    fun testSuccess_HoldsData() {
        val resource: Resource<Int> = Resource.Success(7)

        assertTrue(resource is Resource.Success)
        assertEquals(7, (resource as Resource.Success).data)
    }

    @Test
    fun testError_HoldsMessage() {
        val resource: Resource<Int> = Resource.Error("Failure")

        assertTrue(resource is Resource.Error)
        assertEquals("Failure", (resource as Resource.Error).message)
    }
}
