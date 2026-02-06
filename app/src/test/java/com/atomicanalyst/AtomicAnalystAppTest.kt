package com.atomicanalyst

import org.junit.Assert.assertEquals
import org.junit.Test

class AtomicAnalystAppTest {
    @Test
    fun testQualifiedName_isStable() {
        assertEquals("com.atomicanalyst.AtomicAnalystApp", AtomicAnalystApp::class.qualifiedName)
    }
}
