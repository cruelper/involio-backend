package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_country")
class Country {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id_country: Int? = null

    @Column
    var name_country: String? = null

    @OneToMany(mappedBy = "country", fetch = FetchType.EAGER)
    var company: Collection<Company>? = null

    constructor(name: String){
        this.name_country = name
    }
}