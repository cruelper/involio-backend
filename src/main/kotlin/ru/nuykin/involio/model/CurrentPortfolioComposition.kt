package ru.nuykin.involio.model

import java.io.Serializable
import java.sql.Date
import javax.persistence.*

@Entity
@IdClass(CurrentPortfolioCompositionId::class)
@Table(name = "table_current_portfolio_composition")
class CurrentPortfolioComposition {
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_portfolio")
    var portfolio_to_composition: InvestmentPortfolio? = null
    @Id
    var tiker: String? = null
    @Id
    var date: Date? = null
    @Id
    var id_exchange: Int? = null

    @Column
    var count: Int? = null
    @Column
    var priceOfUnit: Double? = null

    constructor(portfolio: InvestmentPortfolio, ticker: String, date: Date,
                id_exchange: Int, count: Int, price: Double){
        this.portfolio_to_composition = portfolio
        this.tiker = tiker
        this.date = date
        this.id_exchange = id_exchange
        this.count = count
        this.priceOfUnit = price
    }
}

class CurrentPortfolioCompositionId: Serializable {
    var portfolio_to_composition: InvestmentPortfolio? = null
    var tiker: String? = null
    var date: Date? = null
    var id_exchange: Int? = null

    constructor(portfolio: InvestmentPortfolio, ticker: String, date: Date,
                id_exchange: Int){
        this.portfolio_to_composition = portfolio
        this.tiker = tiker
        this.date = date
        this.id_exchange = id_exchange
    }
}