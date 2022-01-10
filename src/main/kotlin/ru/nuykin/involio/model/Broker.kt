package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_broker")
class Broker {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id_broker: Int = 0

    @Column
    var name_broker: String? = null

    @OneToMany(mappedBy = "broker_of_portfolio", fetch = FetchType.EAGER)
    var investment_portfolio: Collection<InvestmentPortfolio>? = null

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.MERGE])
    @JoinTable(
        name = "table_exchange_broker",
        joinColumns = [JoinColumn(name = "id_broker")],
        inverseJoinColumns = [JoinColumn(name = "id_exchange")]
    )
    var brokers_exchange: Collection<Exchange>? = null

    constructor(name: String, exchange: Collection<Exchange>){
        this.name_broker = name
        this.brokers_exchange = exchange
    }
}