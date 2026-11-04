package com.example

import com.example.data.model.Categories
import com.example.data.model.PeriodFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PeriodFilterTest {

    @Test
    fun testPeriodFilterRanges() {
        val todayRange = PeriodFilter.TODAY.getTimeRange()
        assertTrue(todayRange.second >= todayRange.first)

        val monthRange = PeriodFilter.MONTH.getTimeRange()
        assertTrue(monthRange.second >= monthRange.first)

        val allRange = PeriodFilter.ALL.getTimeRange()
        assertEquals(0L, allRange.first)
        assertEquals(Long.MAX_VALUE, allRange.second)
    }

    @Test
    fun testCategoriesResolution() {
        val alimentos = Categories.getCategory("Alimentos", isExpense = true)
        assertEquals("Alimentos", alimentos.name)
        assertNotNull(alimentos.icon)

        val salario = Categories.getCategory("Salario", isExpense = false)
        assertEquals("Salario", salario.name)
        assertNotNull(salario.icon)
    }
}
