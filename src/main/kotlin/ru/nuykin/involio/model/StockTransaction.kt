package ru.nuykin.involio.model

import java.io.Serializable
import java.util.*
import javax.persistence.*


@Entity
@IdClass(StockTransactionId::class)
@Table(name = "table_stock_transaction")
class StockTransaction {
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_investment_portfolio")
    var investment_portfolio_in_stock_transaction: InvestmentPortfolio? = null
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumns(
        JoinColumn(name="ticker", referencedColumnName="ticker"),
        JoinColumn(name="id_exchange", referencedColumnName="id_exchange")
    )
    var stock_in_transaction: Stock? = null

    @Id
    var date_transaction: Date? = null

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
        stock: Stock,
        item_count: Int,
        unit_cost: Double,
        sale_or_purchase: String,
        date_transaction: Date,
        broker_commission: Double
    ) {
        this.investment_portfolio_in_stock_transaction = investment_portfolio
        this.stock_in_transaction = stock
        this.item_count = item_count
        this.unit_cost = unit_cost
        this.sale_or_purchase = sale_or_purchase
        this.date_transaction = date_transaction
        this.broker_commission = broker_commission
    }
}

class StockTransactionId: Serializable {
    var investment_portfolio_in_stock_transaction: InvestmentPortfolio? = null
    var stock_in_transaction: Stock? = null
    var date_transaction: Date? = null

    constructor(investment_portfolio: InvestmentPortfolio, stock: Stock, date: Date) {
        this.investment_portfolio_in_stock_transaction = investment_portfolio
        this.stock_in_transaction = stock
        this.date_transaction = date_transaction
    }
}