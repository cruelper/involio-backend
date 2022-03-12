package ru.nuykin.involio.model

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@IdClass(PortfoliosValueHistoryId::class)
@Table(name = "table_portfolios_value_history")
class PortfoliosValueHistory {
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_portfolio")
    var portfolioToHistory: InvestmentPortfolio? = null
    @Id
    var datePortfoliosValue: Date? = null

    @Column
    var value_in_ruble: Double? = 0.0

    @Column
    var value_in_usd: Double? = 0.0

    @Column
    var value_in_euro: Double? = 0.0

    @Column
    var replenishmentAmountInRuble: Double? = 0.0

    @Column
    var replenishmentAmountInUSD: Double? = 0.0

    @Column
    var replenishmentAmountInEuro: Double? = 0.0

    @Column
    var receivedDividendsInRuble: Double? = 0.0

    @Column
    var receivedDividendsInUsd: Double? = 0.0

    @Column
    var receivedDividendsInEuro: Double? = 0.0

    @Column
    var brokerCommissionInRuble: Double? = 0.0

    @Column
    var brokerCommissionInUsd: Double? = 0.0

    @Column
    var brokerCommissionInEuro: Double? = 0.0

    constructor(portfolio_to_history: InvestmentPortfolio?, datePortfoliosValue: Date?) {
        this.portfolioToHistory = portfolio_to_history
        this.datePortfoliosValue = datePortfoliosValue
    }

    constructor(
        portfolio_to_history: InvestmentPortfolio?,
        datePortfoliosValue: Date?,
        value_in_ruble: Double?,
        value_in_usd: Double?,
        value_in_euro: Double?,
        replenishmentAmountInRuble: Double?,
        replenishmentAmountInUSD: Double?,
        replenishmentAmountInEuro: Double?,
        receivedDividendsInRuble: Double?,
        receivedDividendsInUsd: Double?,
        receivedDividendsInEuro: Double?,
        brokerCommissionInRuble: Double?,
        brokerCommissionInUsd: Double?,
        brokerCommissionInEuro: Double?
    ) {
        this.portfolioToHistory = portfolio_to_history
        this.datePortfoliosValue = datePortfoliosValue
        this.value_in_ruble = value_in_ruble
        this.value_in_usd = value_in_usd
        this.value_in_euro = value_in_euro
        this.replenishmentAmountInRuble = replenishmentAmountInRuble
        this.replenishmentAmountInUSD = replenishmentAmountInUSD
        this.replenishmentAmountInEuro = replenishmentAmountInEuro
        this.receivedDividendsInRuble = receivedDividendsInRuble
        this.receivedDividendsInUsd = receivedDividendsInUsd
        this.receivedDividendsInEuro = receivedDividendsInEuro
        this.brokerCommissionInRuble = brokerCommissionInRuble
        this.brokerCommissionInUsd = brokerCommissionInUsd
        this.brokerCommissionInEuro = brokerCommissionInEuro
    }


}

class PortfoliosValueHistoryId(): Serializable {
    var portfolioToHistory: Int? = null
    var datePortfoliosValue: Date? = null

    constructor(portfolio: Int, date_portfolios_value: Date): this() {
        this.portfolioToHistory = portfolio
        this.datePortfoliosValue = date_portfolios_value
    }
}