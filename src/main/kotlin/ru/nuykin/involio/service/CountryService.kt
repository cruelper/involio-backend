package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.CountryDto
import ru.nuykin.involio.model.Country
import ru.nuykin.involio.repository.CountryRepository


@Service
class CountryService{
    @Autowired
    private val countryDao: CountryRepository? = null

    fun getAllCountry(): List<CountryDto> =
        countryDao!!.findAll().toList().map { CountryDto(it.id_country!!, it.name_country!!) }

    fun getCountryById(id: Int): Country? =
        countryDao!!.findByIdOrNull(id)

    fun getDtoCountryById(id: Int): CountryDto? {
        val country: Country? = countryDao!!.findByIdOrNull(id)
        return if(country == null) null else CountryDto(country.id_country!!, country.name_country!!)
    }

    fun addCountry(countryDto: CountryDto): Country{
        val newCountry: Country = Country(name = countryDto.name)
        return countryDao!!.save(newCountry)
    }

    fun updateCountry(countryDto: CountryDto){
        val country = countryDao!!.findByIdOrNull(countryDto.id)
        if(country != null){
            country.name_country = countryDto.name
            countryDao.save(country)
        }
    }

    fun deleteCountryById(id: Int){
        countryDao!!.deleteById(id)
    }
}