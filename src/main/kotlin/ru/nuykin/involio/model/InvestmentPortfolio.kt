package ru.nuykin.involio.model

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "table_investment_portfolio")
class InvestmentPortfolio {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id_investment_portfolio: Int = 0

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "email_owner")
    var owner: MyUser? = null

    @Column
    var name_portfolio: String? = null

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_type_of_broker_account")
    var type_broker_account: TypeOfBrokerAccount? = null

    @Column
    var date_of_creation: Date? = null

    @Column
    var last_modified_date: Date? = null

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_broker")
    var broker_of_portfolio: Broker? = null

    @OneToMany(mappedBy = "portfolio_to_history", fetch = FetchType.LAZY)
    var history_of_portfolio: Collection<PortfoliosValueHistory>? = null

    @OneToMany(mappedBy = "portfolio_to_composition", fetch = FetchType.LAZY)
    var composition_of_portfolio: Collection<CurrentPortfolioComposition>? = null

    @OneToMany(mappedBy = "investment_portfolio_in_stock_transaction", fetch = FetchType.LAZY)
    var stock_transaction_of_portfolio: Collection<StockTransaction>? = null

    @OneToMany(mappedBy = "investment_portfolio_in_currency_transaction", fetch = FetchType.LAZY)
    var currency_transaction_of_portfolio: Collection<CurrencyTransaction>? = null


    constructor(owner: MyUser, name_portfolio: String,
                type_broker_account: TypeOfBrokerAccount, date_of_creation: Date,
                broker: Broker){
        this.owner = owner
        this.name_portfolio = name_portfolio
        this.type_broker_account = type_broker_account
        this.date_of_creation = date_of_creation
        this.broker_of_portfolio = broker
    }
}