package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_company")
class Company {
    @Id
    var isin: String? = null

    @OneToMany(mappedBy = "dividends_company", fetch = FetchType.LAZY)
    var dividends: Collection<Dividends>? = null

    @Column
    var nameCompany: String? = null

    @Column
    var description: String? = null

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_currency")
    var id_currency: Currency? = null

    @ManyToOne(optional = true, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_country")
    var country: Country? = null

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "table_branch_company",
        joinColumns = [JoinColumn(name = "isin")],
        inverseJoinColumns = [JoinColumn(name = "id_branch")]
    )
    var branch: Collection<Branch>? = null

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "table_sector_company",
        joinColumns = [JoinColumn(name = "isin")],
        inverseJoinColumns = [JoinColumn(name = "id_sector")]
    )
    var sector: Collection<Sector>? = null


    @OneToMany(mappedBy = "stock_company", fetch = FetchType.EAGER)
    var stock: Collection<Stock>? = null

    constructor(
        isin: String?,
        nameCompany: String?,
        description: String?,
        id_currency: Currency?,
        country: Country?,
        branch: Collection<Branch>?,
        sector: Collection<Sector>?,
    ) {
        this.isin = isin
        this.nameCompany = nameCompany
        this.description = description
        this.id_currency = id_currency
        this.country = country
        this.branch = branch
        this.sector = sector
    }
}