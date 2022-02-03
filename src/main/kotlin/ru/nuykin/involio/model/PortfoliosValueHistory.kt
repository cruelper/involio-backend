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
    var datePortfoliosValue: Date? = null

    @Column
    var value_in_ruble: Double? = null

    @Column
    var value_in_usd: Double? = null

    @Column
    var value_in_euro: Double? = null

    @Column
    var replenishmentAmountInRuble: Double? = null

    @Column
    var replenishmentAmountInUSD: Double? = null

    @Column
    var replenishmentAmountInEuro: Double? = null

    @Column
    var receivedDividendsInRuble: Double? = null

    @Column
    var receivedDividendsInUsd: Double? = null

    @Column
    var receivedDividendsInEuro: Double? = null

    @Column
    var brokerCommissionInRuble: Double? = null

    @Column
    var brokerCommissionInUsd: Double? = null

    @Column
    var brokerCommissionInEuro: Double? = null

    constructor(portfolio: InvestmentPortfolio, date_portfolios_value: Date, value_in_ruble: Double) {
        this.portfolio_to_history = portfolio
        this.datePortfoliosValue = date_portfolios_value
        this.value_in_ruble = value_in_ruble
    }
}

class PortfoliosValueHistoryId: Serializable {
    var portfolio_to_history: InvestmentPortfolio? = null
    var datePortfoliosValue: Date? = null

    constructor(portfolio: InvestmentPortfolio, date_portfolios_value: Date) {
        this.portfolio_to_history = portfolio
        this.datePortfoliosValue = date_portfolios_value
    }
}