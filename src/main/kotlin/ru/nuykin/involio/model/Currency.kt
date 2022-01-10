package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_currency")
class Currency {
    @Id
    var id_currency: String? = null

    @OneToMany(mappedBy = "id_currency", fetch = FetchType.EAGER)
    var company: Collection<Company>? = null

    @OneToMany(mappedBy = "trading_currency", fetch = FetchType.EAGER)
    var currency_to_stock: Collection<Stock>? = null

    @OneToMany(mappedBy = "currency_in_transaction", fetch = FetchType.LAZY)
    var currency_in_transaction: Collection<CurrencyTransaction>? = null

    @Column
    var name_currency: String? = null

    constructor(id: String, name: String){
        this.id_currency = id
        this.name_currency = name
    }
}