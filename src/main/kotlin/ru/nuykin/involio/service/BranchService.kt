package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.nuykin.involio.dto.BranchDto
import ru.nuykin.involio.model.*
import ru.nuykin.involio.repository.*

@Service
class BranchService{
    @Autowired private val branchDao: BranchRepository? = null

    fun getAllBranch(): List<BranchDto> =
        branchDao!!.findAll().toList().map { BranchDto(it.idBranch!!, it.nameBranch!!) }

    fun getBranchById(id: Int): Branch? =
        branchDao!!.findByIdOrNull(id)

    fun getDtoBranchById(id: Int): BranchDto? {
        val branch: Branch? = branchDao!!.findByIdOrNull(id)
        return if(branch == null) null else BranchDto(branch.idBranch!!, branch.nameBranch!!)
    }

    fun addBranch(branchDto: BranchDto){
        val newBranch: Branch = Branch(nameBranch = branchDto.name)
        branchDao!!.save(newBranch)
    }

    fun updateBranch(branchDto: BranchDto){
        val branch = branchDao!!.findByIdOrNull(branchDto.id)
        if(branch != null){
            branch.nameBranch = branchDto.name
            branchDao.save(branch)
        }
    }

    fun deleteBranchById(id: Int){
        branchDao!!.deleteById(id)
    }
}