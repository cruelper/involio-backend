package ru.nuykin.involio.model

import java.io.Serializable
import java.util.Date
import javax.persistence.*

@Entity
@IdClass(CurrentPortfolioCompositionId::class)
@Table(name = "table_current_portfolio_composition")
class CurrentPortfolioComposition {
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_portfolio")
    var portfolioToComposition: InvestmentPortfolio? = null
    @Id
    var ticker: String? = null
    @Id
    var date: Date? = null
    @Id
    var idExchange: Int? = null

    @Column
    var count: Int? = null
    @Column
    var priceOfUnit: Double? = null

    constructor(
        portfolioToComposition: InvestmentPortfolio,
        ticker: String,
        date: Date,
        idExchange: Int,
        count: Int,
        priceOfUnit: Double
    ) {
        this.portfolioToComposition = portfolioToComposition
        this.ticker = ticker
        this.date = date
        this.idExchange = idExchange
        this.count = count
        this.priceOfUnit = priceOfUnit
    }
}

class CurrentPortfolioCompositionId(): Serializable {
    var portfolioToComposition: Int? = null
    var ticker: String? = null
    var date: Date? = null
    var idExchange: Int? = null

    constructor(portfolioToComposition: Int, ticker: String, date: Date, idExchange: Int) : this() {
        this.portfolioToComposition = portfolioToComposition
        this.ticker = ticker
        this.date = date
        this.idExchange = idExchange
    }
}