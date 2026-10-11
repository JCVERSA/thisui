package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.NavTab
import com.example.model.ServerCategory
import com.example.model.TelemetryData
import com.example.model.VpnServer
import com.example.model.VpnSettings
import com.example.model.VpnStatus
import com.example.repository.VpnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnViewModel(
    private val repository: VpnRepository = VpnRepository()
) : ViewModel() {

    val status: StateFlow<VpnStatus> = repository.status
    val selectedServer: StateFlow<VpnServer> = repository.selectedServer
    val telemetry: StateFlow<TelemetryData> = repository.telemetry
    val settings: StateFlow<VpnSettings> = repository.settings

    private val _activeTab = MutableStateFlow(NavTab.HOME)
    val activeTab: StateFlow<NavTab> = _activeTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ServerCategory.ALL)
    val selectedCategory: StateFlow<ServerCategory> = _selectedCategory.asStateFlow()

    private val _showServerPickerSheet = MutableStateFlow(false)
    val showServerPickerSheet: StateFlow<Boolean> = _showServerPickerSheet.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val filteredServers: StateFlow<List<VpnServer>> = combine(
        repository.servers,
        _searchQuery,
        _selectedCategory
    ) { allServers, query, category ->
        allServers.filter { server ->
            val matchesQuery = query.isBlank() ||
                    server.city.contains(query, ignoreCase = true) ||
                    server.country.contains(query, ignoreCase = true) ||
                    server.name.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                ServerCategory.ALL -> true
                ServerCategory.GAMING -> server.pingMs <= 35
                ServerCategory.STREAMING -> server.category == ServerCategory.STREAMING || server.loadPercent < 60
                else -> server.category == category
            }

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setActiveTab(tab: NavTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: ServerCategory) {
        _selectedCategory.value = category
    }

    fun setShowServerPickerSheet(show: Boolean) {
        _showServerPickerSheet.value = show
    }

    fun toggleConnection() {
        when (status.value) {
            VpnStatus.DISCONNECTED -> repository.connect()
            VpnStatus.CONNECTED -> repository.disconnect()
            VpnStatus.CONNECTING -> {
                // If connecting, cancel
                repository.disconnect()
            }
            VpnStatus.DISCONNECTING -> {}
        }
    }

    fun selectServer(server: VpnServer) {
        repository.selectServer(server)
        _showServerPickerSheet.value = false
        showToast("Connected location: ${server.city}, ${server.country}")
    }

    fun toggleFavorite(server: VpnServer) {
        repository.toggleFavorite(server.id)
    }

    fun updateKillSwitch(enabled: Boolean) {
        repository.updateKillSwitch(enabled)
        showToast(if (enabled) "Kill Switch Enabled" else "Kill Switch Disabled")
    }

    fun updateSplitTunneling(enabled: Boolean) {
        repository.updateSplitTunneling(enabled)
    }

    fun updateProtocol(protocol: String) {
        repository.updateProtocol(protocol)
        showToast("Switched protocol to $protocol")
    }

    fun updateAutoConnect(enabled: Boolean) {
        repository.updateAutoConnect(enabled)
    }

    fun updateDnsProtection(enabled: Boolean) {
        repository.updateDnsProtection(enabled)
    }

    fun updateSelectedDns(dns: String) {
        repository.updateSelectedDns(dns)
        showToast("DNS set to $dns")
    }

    fun toggleAppBypass(packageName: String) {
        repository.toggleAppBypass(packageName)
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun selectFastestServer() {
        val fastest = repository.servers.value.minByOrNull { it.pingMs }
        fastest?.let {
            selectServer(it)
        }
    }
}
