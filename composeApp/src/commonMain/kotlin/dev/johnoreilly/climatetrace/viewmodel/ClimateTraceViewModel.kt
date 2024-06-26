package dev.johnoreilly.climatetrace.viewmodel

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import com.rickclephas.kmp.observableviewmodel.MutableStateFlow
import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import dev.johnoreilly.climatetrace.data.ClimateTraceRepository
import dev.johnoreilly.climatetrace.remote.ClimateTraceApi
import dev.johnoreilly.climatetrace.remote.Country
import dev.johnoreilly.climatetrace.remote.CountryAssetEmissionsInfo
import dev.johnoreilly.climatetrace.remote.CountryEmissionsInfo
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class ClimateTraceViewModel : ViewModel(), KoinComponent {
    private val climateTraceApi: ClimateTraceApi by inject()
    private val climateTraceRepository: ClimateTraceRepository by inject()

    var year: String = "2022" // TODO make this configurable

    private val _countryList = MutableStateFlow<List<Country>>(viewModelScope, emptyList())
    @NativeCoroutinesState
    val countryList = _countryList.asStateFlow()

    @NativeCoroutinesState
    val selectedCountry = MutableStateFlow<Country?>(viewModelScope, null)

    @NativeCoroutinesState
    val countryEmissionInfo = MutableStateFlow<CountryEmissionsInfo?>(viewModelScope, null)

    @NativeCoroutinesState
    val countryAssetEmissions = MutableStateFlow<List<CountryAssetEmissionsInfo>?>(viewModelScope, null)

    val isLoadingCountries = MutableStateFlow(viewModelScope, true)
    val isLoadingCountryDetails = MutableStateFlow(viewModelScope, true)

    init {
        isLoadingCountries.value = true
        viewModelScope.coroutineScope.launch {
            _countryList.value = climateTraceRepository.fetchCountries().sortedBy { it.name }
            isLoadingCountries.value = false
        }
    }

    fun fetchCountryDetails(country: Country) {
        selectedCountry.value = country
        isLoadingCountryDetails.value = true
        viewModelScope.coroutineScope.launch {
            countryEmissionInfo.value = climateTraceApi.fetchCountryEmissionsInfo(country.alpha3, year).firstOrNull()
            countryAssetEmissions.value = climateTraceApi.fetchCountryAssetEmissionsInfo(country.alpha3)
            isLoadingCountryDetails.value = false
        }
    }
}
