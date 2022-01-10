package ru.nuykin.involio.model

import java.io.Serializable
import java.sql.*
import javax.persistence.*

@Entity
@IdClass(DividendsId::class)
@Table(name = "table_dividends")
class Dividends {
    @Id
    var date_dividends: Date? = null

    @Id
    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "isin")
    var dividends_company: Company? = null

    @Column
    var absolut_size: Double? = null

    @Column
    var relative_size: Double? = null

    constructor(company: Company, date_dividends: Date,
                absolut_size: Double, relative_size: Double)
    {
        this.dividends_company = company
        this.date_dividends = date_dividends
        this.absolut_size = absolut_size
        this.relative_size = relative_size
    }
}

class DividendsId: Serializable{
    var dividends_company: Company? = null
    var date_dividends: Date? = null

    constructor(company: Company, date_dividends: Date){
        this.dividends_company = company
        this.date_dividends = date_dividends
    }
}