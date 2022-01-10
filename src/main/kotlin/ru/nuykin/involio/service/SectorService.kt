package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.SectorDto
import ru.nuykin.involio.model.Sector
import ru.nuykin.involio.repository.SectorRepository

@Service
class SectorService{
    @Autowired
    private val sectorDao: SectorRepository? = null

    fun getAllSector(): List<SectorDto> =
        sectorDao!!.findAll().toList().map { SectorDto(it.id_sector!!, it.name_sector!!) }

    fun getSectorById(id: Int): Sector? =
        sectorDao!!.findByIdOrNull(id)

    fun getDtoSectorById(id: Int): SectorDto? {
        val sector: Sector? = sectorDao!!.findByIdOrNull(id)
        return if(sector == null) null else SectorDto(sector.id_sector!!, sector.name_sector!!)
    }

    fun addSector(sectorDto: SectorDto){
        val newSector: Sector = Sector(name_sector = sectorDto.name)
        sectorDao!!.save(newSector)
    }

    fun updateSector(sectorDto: SectorDto){
        val sector = sectorDao!!.findByIdOrNull(sectorDto.id)
        if(sector != null){
            sector.name_sector = sectorDto.name
            sectorDao.save(sector)
        }
    }

    fun deleteSectorById(id: Int){
        sectorDao!!.deleteById(id)
    }
}
