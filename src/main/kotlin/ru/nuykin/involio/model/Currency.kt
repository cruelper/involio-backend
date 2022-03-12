package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_currency")
class Currency {
    @Id
    var idCurrency: String? = null

    @OneToMany(mappedBy = "id_currency", fetch = FetchType.EAGER)
    var company: Collection<Company>? = null

    @OneToMany(mappedBy = "trading_currency", fetch = FetchType.EAGER)
    var currency_to_stock: Collection<Stock>? = null

    @OneToMany(mappedBy = "currencyInTransaction", fetch = FetchType.LAZY)
    var currency_in_transaction: Collection<CurrencyTransaction>? = null

    @Column
    var name_currency: String? = null

    @Column
    var sign_currency: String? = null

    @Column
    var id_on_yahoo_api: String? = null

    constructor(id: String, name: String, sign: String, id_on_yahoo_api: String){
        this.idCurrency = id
        this.name_currency = name
        this.sign_currency = sign
        this.id_on_yahoo_api = id_on_yahoo_api
    }
}