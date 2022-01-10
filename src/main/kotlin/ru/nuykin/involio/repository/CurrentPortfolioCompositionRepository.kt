package ru.nuykin.involio.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.nuykin.involio.model.CurrentPortfolioComposition
import ru.nuykin.involio.model.CurrentPortfolioCompositionId


@Repository
interface CurrentPortfolioCompositionRepository :
    CrudRepository<CurrentPortfolioComposition, CurrentPortfolioCompositionId> {
}