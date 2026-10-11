package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.NavTab
import com.example.model.VpnStatus
import com.example.repository.VpnRepository
import com.example.viewmodel.VpnViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("VPNSXB", appName)
    }

    @Test
    fun `test repository servers initialization`() {
        val repo = VpnRepository()
        val servers = repo.servers.value
        assertTrue(servers.isNotEmpty())
        assertEquals("Frankfurt 01", repo.selectedServer.value.name)
        assertEquals(VpnStatus.DISCONNECTED, repo.status.value)
    }

    @Test
    fun `test viewmodel state defaults and server switching`() {
        val viewModel = VpnViewModel()
        assertEquals(NavTab.HOME, viewModel.activeTab.value)
        viewModel.setActiveTab(NavTab.SERVERS)
        assertEquals(NavTab.SERVERS, viewModel.activeTab.value)

        // Select fastest
        viewModel.selectFastestServer()
        assertNotNull(viewModel.selectedServer.value)
        assertTrue(viewModel.selectedServer.value.pingMs > 0)
    }
}
