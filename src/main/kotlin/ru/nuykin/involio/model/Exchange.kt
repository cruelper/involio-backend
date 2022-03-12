package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_exchange")
class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id_exchange: Int = 0

    @Column
    var name_exchange: String? = null

    @OneToMany(mappedBy = "exchange", fetch = FetchType.EAGER)
    var stock_on_exchange: Collection<Stock>? = null

    @ManyToMany(mappedBy = "brokers_exchange")
    var broker: Collection<Broker>? = null

    constructor(name: String){
        this.name_exchange = name
    }
}