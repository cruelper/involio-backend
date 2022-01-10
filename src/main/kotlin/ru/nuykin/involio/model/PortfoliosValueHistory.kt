package ru.nuykin.involio.model

import java.io.Serializable
import java.sql.*
import javax.persistence.*

@Entity
@IdClass(PortfoliosValueHistoryId::class)
@Table(name = "table_portfolios_value_history")
class PortfoliosValueHistory {
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_portfolio")
    var portfolio_to_history: InvestmentPortfolio? = null
    @Id
    var date_portfolios_value: Date? = null

    @Column
    var value_in_ruble: Double? = null

    constructor(portfolio: InvestmentPortfolio, date_portfolios_value: Date, value_in_ruble: Double) {
        this.portfolio_to_history = portfolio
        this.date_portfolios_value = date_portfolios_value
        this.value_in_ruble = value_in_ruble
    }
}

class PortfoliosValueHistoryId: Serializable {
    var portfolio_to_history: InvestmentPortfolio? = null
    var date_portfolios_value: Date? = null

    constructor(portfolio: InvestmentPortfolio, date_portfolios_value: Date) {
        this.portfolio_to_history = portfolio
        this.date_portfolios_value = date_portfolios_value
    }
}