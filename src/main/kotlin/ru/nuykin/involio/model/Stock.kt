package ru.nuykin.involio.model

import java.io.Serializable
import javax.persistence.*

@Entity
@IdClass(StockId::class)
@Table(name = "table_stock")
class Stock {
    @Id
    var ticker: String? = null
    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_exchange")
    var exchange: Exchange? = null

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "isin")
    var stock_company: Company? = null

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_trading_currency")
    var trading_currency: Currency? = null

    @Column
    var is_available_to_unqualified_investors: Boolean? = null

    @Column
    var ticker_on_yahoo_api: String? = null

    @OneToMany(mappedBy = "stockInTransaction", fetch = FetchType.LAZY)
    var transaction_of_stock: Collection<StockTransaction>? = null


    constructor(ticker: String, exchange: Exchange, company: Company,
                currency: Currency, available: Boolean, ticker_on_yahoo_api: String){
        this.ticker = ticker
        this.exchange = exchange
        this.stock_company = company
        this.trading_currency = currency
        this.is_available_to_unqualified_investors = available
        this.ticker_on_yahoo_api = ticker_on_yahoo_api
    }
}

class StockId() : Serializable {
    var ticker: String? = null
    var exchange: Int? = null
    constructor(ticker: String, exchange: Int) : this() {
        this.ticker = ticker
        this.exchange = exchange
    }
}