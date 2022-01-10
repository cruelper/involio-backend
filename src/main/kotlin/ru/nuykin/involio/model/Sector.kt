package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_sector")
class Sector {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id_sector: Int? = null

    var name_sector: String? = null

    @ManyToMany(mappedBy = "sector")
    var company: Collection<Company>? = null

    constructor(name_sector: String) {
        this.name_sector = name_sector
    }
}