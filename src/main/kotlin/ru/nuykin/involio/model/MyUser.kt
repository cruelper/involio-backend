package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_user")
class MyUser {
    @Id
    var email: String? = null

    @Column
    var login: String? = null

    @Column
    var password: String? = null

    @Column
    var role: String? = null

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    var portfolio_of_user: Collection<InvestmentPortfolio>? = null

    constructor(email: String, login: String, password: String, role: String){
        this.email = email
        this.login = login
        this.password = password
        this.role = role
    }
}