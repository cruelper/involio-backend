package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_type_of_broker_account")
class TypeOfBrokerAccount {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id_type_of_broker_account: Int = 0

    @Column
    var name_type_of_broker_account: String? = null

    @OneToMany(mappedBy = "type_broker_account", fetch = FetchType.EAGER)
    var investment_portfolio: Collection<InvestmentPortfolio>? = null

    constructor(name_type_of_broker_account: String) {
        this.name_type_of_broker_account = name_type_of_broker_account
    }
}