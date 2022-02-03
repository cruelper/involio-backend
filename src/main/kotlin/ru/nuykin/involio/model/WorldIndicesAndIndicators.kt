package ru.nuykin.involio.model

import javax.persistence.*

@Entity
@Table(name = "table_world_indeces_and_indicators")
class WorldIndicesAndIndicators {
    @Id
    var id: String? = null

    @Column
    var name: String? = null

    @Column
    var tickerOnYahooApi: String? = null

    // тут или валюта, которая под капотом, или, например, пункты
    @Column
    var measure: String? = null
}