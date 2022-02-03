package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.WorldIndicesAndIndicators

@Repository
interface WorldIndicesAndIndicatorsRepository: CrudRepository<WorldIndicesAndIndicators, String> {
}
