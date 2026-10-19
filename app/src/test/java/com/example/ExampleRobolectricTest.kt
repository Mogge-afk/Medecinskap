package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.DateUtils
import com.example.util.ExpiryStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Medicinskåpet", appName)
    }

    @Test
    fun `test expiry calculation`() {
        val today = LocalDate.now()
        val expiredDate = today.minusDays(5).toString()
        val expiry30Date = today.plusDays(10).toString()
        val expiry90Date = today.plusDays(60).toString()
        val validDate = today.plusDays(120).toString()

        assertEquals(ExpiryStatus.EXPIRED, DateUtils.getExpiryInfo(expiredDate).status)
        assertEquals(ExpiryStatus.WITHIN_30_DAYS, DateUtils.getExpiryInfo(expiry30Date).status)
        assertEquals(ExpiryStatus.WITHIN_90_DAYS, DateUtils.getExpiryInfo(expiry90Date).status)
        assertEquals(ExpiryStatus.VALID, DateUtils.getExpiryInfo(validDate).status)
        assertEquals(ExpiryStatus.NONE, DateUtils.getExpiryInfo(null).status)
    }

    @Test
    fun `test swedish medicine catalog offline lookup`() {
        val alvedon = com.example.data.catalog.SwedishMedicineCatalog.findByBarcode("7300009012345")
        org.junit.Assert.assertNotNull(alvedon)
        assertEquals("Alvedon", alvedon?.name)
        assertEquals("500 mg", alvedon?.strength)
        assertEquals("Filmdragerad tablett", alvedon?.form)
        assertEquals("20 st", alvedon?.packageSize)
        assertEquals("Paracetamol", alvedon?.activeSubstance)

        val ipren = com.example.data.catalog.SwedishMedicineCatalog.findByBarcode("7350035021008")
        org.junit.Assert.assertNotNull(ipren)
        assertEquals("Ipren", ipren?.name)
        assertEquals("400 mg", ipren?.strength)
        assertEquals("Ibuprofen", ipren?.activeSubstance)
    }

    @Test
    fun `test schedule database operations`() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = com.example.data.local.AppDatabase.getDatabase(context)
        val scheduleDao = db.scheduleDao()

        val entry = com.example.data.local.ScheduleEntryEntity(
            medicineName = "Alvedon 500 mg",
            timeOfDay = "MORNING",
            dose = "1 tablett",
            notes = "Tas med frukost"
        )
        val id = scheduleDao.insertScheduleEntry(entry)
        org.junit.Assert.assertTrue(id > 0)

        scheduleDao.deleteScheduleEntryById(id)
    }
}
