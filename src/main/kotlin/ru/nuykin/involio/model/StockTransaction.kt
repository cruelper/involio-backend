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
    var investmentPortfolioInStockTransaction: InvestmentPortfolio? = null
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumns(
        JoinColumn(name="ticker", referencedColumnName="ticker"),
        JoinColumn(name="id_exchange", referencedColumnName="id_exchange")
    )
    var stockInTransaction: Stock? = null

    @Id
    var dateTransaction: Date? = null

    @Column
    var itemCount: Int? = null

    @Column
    var unitCost: Double? = null

    @Column
    var saleOrPurchase: String? = null

    @Column
    var brokerCommission: Double? = null

    constructor(
        investment_portfolio: InvestmentPortfolio,
        stock: Stock,
        item_count: Int,
        unit_cost: Double,
        sale_or_purchase: String,
        date_transaction: Date,
        broker_commission: Double
    ) {
        this.investmentPortfolioInStockTransaction = investment_portfolio
        this.stockInTransaction = stock
        this.itemCount = item_count
        this.unitCost = unit_cost
        this.saleOrPurchase = sale_or_purchase
        this.dateTransaction = date_transaction
        this.brokerCommission = broker_commission
    }
}

class StockTransactionId: Serializable {
    var investmentPortfolioInStockTransaction: InvestmentPortfolio? = null
    var stockInTransaction: Stock? = null
    var dateTransaction: Date? = null

    constructor(investment_portfolio: InvestmentPortfolio, stock: Stock, date: Date) {
        this.investmentPortfolioInStockTransaction = investment_portfolio
        this.stockInTransaction = stock
        this.dateTransaction = date
    }
}