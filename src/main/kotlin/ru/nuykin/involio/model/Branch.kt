package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_branch")
class Branch {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var idBranch: Int? = null

    @Column
    var nameBranch: String? = null

    @ManyToMany(mappedBy = "branch")
    var company: Collection<Company>? = null

    constructor(nameBranch: String) {
        this.nameBranch = nameBranch
    }
}

