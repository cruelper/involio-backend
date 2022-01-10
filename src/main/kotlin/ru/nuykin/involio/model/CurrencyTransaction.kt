package ru.nuykin.involio.model

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@IdClass(CurrencyTransactionId::class)
@Table(name = "table_currency_transaction")
class CurrencyTransaction {
    @Id
    var date_transaction: Date? = null
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_investment_portfolio")
    var investment_portfolio_in_currency_transaction: InvestmentPortfolio? = null
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_currency")
    var currency_in_transaction: Currency? = null

    @Column
    var item_count: Int? = null

    @Column
    var unit_cost: Double? = null

    @Column
    var sale_or_purchase: String? = null

    @Column
    var broker_commission: Double? = null

    constructor(
        investment_portfolio: InvestmentPortfolio,
        currency: Currency,
        transaction_exchange: Exchange,
        item_count: Int,
        unit_cost: Double,
        ticker_currency_transaction: String,
        sale_or_purchase: String,
        date_transaction: Date,
        broker_commission: Double
    ) {
        this.investment_portfolio_in_currency_transaction = investment_portfolio
        this.currency_in_transaction = currency
        this.item_count = item_count
        this.unit_cost = unit_cost
        this.sale_or_purchase = sale_or_purchase
        this.date_transaction = date_transaction
        this.broker_commission = broker_commission
    }
}

class CurrencyTransactionId: Serializable {
    var date_transaction: Date? = null
    var investment_portfolio_in_currency_transaction: InvestmentPortfolio? = null
    var currency_in_transaction: Currency? = null

    constructor(investment_portfolio: InvestmentPortfolio, currency: Currency, date_transaction: Date) {
        this.date_transaction = date_transaction
        this.investment_portfolio_in_currency_transaction = investment_portfolio
        this.currency_in_transaction = currency
    }
}