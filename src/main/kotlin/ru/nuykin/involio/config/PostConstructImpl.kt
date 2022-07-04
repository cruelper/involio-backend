package ru.nuykin.involio.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.*
import ru.nuykin.involio.model.WorldIndicesAndIndicators
import ru.nuykin.involio.repository.WorldIndicesAndIndicatorsRepository
import ru.nuykin.involio.service.*
import javax.annotation.PostConstruct

@Component
class PostConstructImpl {

    @Autowired
    private val myUserService: CustomUserDetailsService? = null
    @Autowired
    private val currencyService: CurrencyService? = null
    @Autowired
    private val branchService: BranchService? = null
    @Autowired
    private val sectorService: SectorService? = null
    @Autowired
    private val countryService: CountryService? = null
    @Autowired
    private val brokerService: BrokerService? = null
    @Autowired
    private val exchangeService: ExchangeService? = null
    @Autowired
    private val indicesAndIndicators: WorldIndicesAndIndicatorsRepository? = null
    @Autowired
    private val companyService: CompanyService? = null
    @Autowired
    private val stockService: StockService? = null
    @Autowired
    private val typeOfBrokerAccount: TypeOfBrokerAccountService? = null


    @PostConstruct
    fun runDatabaseInitialization(){
        println("Запущен метод инициализации базы данных.")
        if(false){
            addTypeOfBrokerAccount()
            addAdmins()
            addCurrencies()
            addBranchesAndSectors()
            addCountries()
            addBrokersAndTypeAccountAndExchange()
            addWorldIndicesAndIndicators()
            addCompaniesAndStocks()
        }
        println("Инициализация базы данных завершена.")
    }

    private fun addTypeOfBrokerAccount(){
        typeOfBrokerAccount!!.addTypeOfBrokerAccount(TypeOfBrokerAccountDto(0, "Обычный брокерский счет"))
        typeOfBrokerAccount!!.addTypeOfBrokerAccount(TypeOfBrokerAccountDto(0, "ИИС типа А"))
        typeOfBrokerAccount!!.addTypeOfBrokerAccount(TypeOfBrokerAccountDto(0, "ИИС типа Б"))

    }

    private fun addAdmins(){
        myUserService!!.registration(MyUserDto("cruelper@yandex.ru", "MainAdmin", "lelnfene32ds"), true)
    }

    private fun addCurrencies(){
        currencyService!!.addCurrency(CurrencyDto("rub","Российский рубль", "₽", "-"))
        currencyService.addCurrency(CurrencyDto("usd","Доллар США", "$", "RUB=X"))
        currencyService.addCurrency(CurrencyDto("eur","Евро", "€", "EURRUB=X"))
        currencyService.addCurrency(CurrencyDto("gbp","Фунт стерлингов", "£", "GBPRUB=X"))
        currencyService.addCurrency(CurrencyDto("chf","Швейцарский франк", "₣", "CHFRUB=X"))
        currencyService.addCurrency(CurrencyDto("cny","Юань", "¥", "CNYRUB=X"))
        currencyService.addCurrency(CurrencyDto("jpy","Иена", "¥", "JPYRUB=X"))
        currencyService.addCurrency(CurrencyDto("hkd","Гонконгский доллар", "HK$", "HKDRUB.ME"))
        currencyService.addCurrency(CurrencyDto("try","Турецкая лира", "₺", "TRYRUB.ME"))
    }

    private fun addBranchesAndSectors(){
        val sectors: List<String> = listOf("Энергетика", "Материалы", "Промышленность", "Потребительские товары повседневного спроса",
            "Потребительские товары выборочного спроса", "Здравоохранение", "Финансы", "Информационные технологии", "Коммуникационные услуги",
            "Коммунальные услуги", "Недвижимость")
        val branches: List<String> = listOf()
        for (sector in sectors) sectorService!!.addSector(SectorDto(0, sector))
        for (branch in branches) branchService!!.addBranch(BranchDto(0, branch))
    }

    private fun addCountries(){
        val countries: List<String> = listOf("США", "Китай", "Япония", "Германия", "Франция", "Великобритания", "Италия", "Индия", "Канада", "Россия")
        for (country in countries) countryService!!.addCountry(CountryDto(0, country))
    }

    private fun addBrokersAndTypeAccountAndExchange(){
        val exchanges: List<String> = listOf("СПБ биржа", "Московская биржа", "NASDAQ", "NYSE", "Франкфуртская фондовая биржа", "Австралийская Фондовая биржа")
        for (exchange in exchanges) exchangeService!!.addExchange(ExchangeDto(0, exchange))

        val exchangesDto: Map<String, Int> = (exchangeService!!.getAllExchange()).associateBy( {it.name}, {it.id})
        val exchangeEntities: Map<String, Exchange> = exchangesDto.map { Pair(it.key, exchangeService!!.getExchangeById(it.value)!!) }.toMap()

        val brokers: List<String> = listOf("Тинькофф", "ВТБ", "Сбер", "БКС", "Interactive Brokers")

        brokerService!!.addBroker("Тинькофф", listOf(exchangeEntities["СПБ биржа"]!!, exchangeEntities["Московская биржа"]!!))
        brokerService!!.addBroker("ВТБ", listOf(exchangeEntities["Московская биржа"]!!))
        brokerService!!.addBroker("БКС", listOf(exchangeEntities["СПБ биржа"]!!, exchangeEntities["Московская биржа"]!!, exchangeEntities["NASDAQ"]!!, exchangeEntities["NYSE"]!!))
        brokerService!!.addBroker("Сбер", listOf(exchangeEntities["Московская биржа"]!!))
        brokerService!!.addBroker("Interactive Brokers", listOf(exchangeEntities["NASDAQ"]!!, exchangeEntities["NYSE"]!!, exchangeEntities["Франкфуртская фондовая биржа"]!!))
    }

    private fun addWorldIndicesAndIndicators(){
        indicesAndIndicators!!.save(WorldIndicesAndIndicators("s&p500","S&P 500", "^GSPC", "usd"))
        indicesAndIndicators!!.save(WorldIndicesAndIndicators("nasdaq", "Nasdaq", "^IXIC", "usd"))
        indicesAndIndicators!!.save(WorldIndicesAndIndicators("nikkey","Nikkei 225", "^N225", "jpy"))
        indicesAndIndicators!!.save(WorldIndicesAndIndicators("imoex", "Индекс московской биржи", "IMOEX.ME", "rub"))
        indicesAndIndicators!!.save(WorldIndicesAndIndicators("vix", "Индекс волатильности VIX", "^VIX", "п."))
    }

    private fun addCompaniesAndStocks(){
        val rub: Currency = currencyService!!.getCurrencyById("rub")!!
        val usd: Currency = currencyService!!.getCurrencyById("usd")!!
        val eur: Currency = currencyService!!.getCurrencyById("eur")!!

        val countryDto: Map<String, Int> = (countryService!!.getAllCountry()).associateBy( {it.name}, {it.id})
        val countryEntities: Map<String, Country> = countryDto.map { Pair(it.key, countryService!!.getCountryById(it.value)!!) }.toMap()

        val sectorDto: Map<String, Int> = (sectorService!!.getAllSector()).associateBy( {it.name}, {it.id})
        val sectorEntities: Map<String, Sector> = sectorDto.map { Pair(it.key, sectorService!!.getSectorById(it.value)!!) }.toMap()

        val exchangeDto: Map<String, Int> = (exchangeService!!.getAllExchange()).associateBy( {it.name}, {it.id})
        val exchangeEntities: Map<String, Exchange> = exchangeDto.map { Pair(it.key, exchangeService!!.getExchangeById(it.value)!!) }.toMap()


        companyService!!.addOrUpdateCompany(Company("RU0009029540", "Сбер", "Сбербанк – крупнейший банк в России и СНГ с самой широкой сетью подразделений, предлагающий весь спектр инвестиционно-банковских услуг.", rub,
            countryEntities["Россия"]!!, listOf(), listOf(sectorEntities["Финансы"]!!, sectorEntities["Информационные технологии"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("SBER", exchangeEntities["Московская биржа"]!!.id_exchange, "RU0009029540",
            rub.idCurrency!!, true, "SBER.ME"))

        companyService!!.addOrUpdateCompany(Company("RU0007661625", "Газпром", "«Газпром» — глобальная энергетическая компания. Основные направления деятельности — геологоразведка, добыча, транспортировка, хранени и переработка газа.", rub,
            countryEntities["Россия"]!!, listOf(), listOf(sectorEntities["Финансы"]!!, sectorEntities["Коммунальные услуги"]!!, sectorEntities["Промышленность"]!!, sectorEntities["Энергетика"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("GAZP", exchangeEntities["Московская биржа"]!!.id_exchange, "RU0007661625",
            rub.idCurrency!!, true, "GAZP.ME"))

        companyService!!.addOrUpdateCompany(Company("US69608A1088", "Palantir Technologies Inc", "Palantir Technologies - разрабатывает программное обеспечение для анализа всех типов информации информации.", rub,
            countryEntities["США"]!!, listOf(), listOf(sectorEntities["Информационные технологии"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("PLTR", exchangeEntities["NYSE"]!!.id_exchange, "US69608A1088",
            usd.idCurrency!!, true, "PLTR"))

        companyService!!.addOrUpdateCompany(Company("DE000A1CSBR6", "Softline", "Softline — международная ИТ-компания, поставщик ИТ-решений и сервисов, работающий на рынках России, Европы, Азии и Америки.", rub,
            countryEntities["Германия"]!!, listOf(), listOf(sectorEntities["Информационные технологии"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("SFTL", exchangeEntities["Московская биржа"]!!.id_exchange, "DE000A1CSBR6",
            rub.idCurrency!!, true, "SFTL.ME"))
        stockService!!.addOrUpdateStock(StockDto("SFD1", exchangeEntities["Франкфуртская фондовая биржа"]!!.id_exchange, "DE000A1CSBR6",
            eur.idCurrency!!, true, "SFD1.DE"))

        companyService!!.addOrUpdateCompany(Company("NL0009805522", "Яндекс", "«Яндекс» - российская ИТ-компания, владеющая одноименной системой поиска в Сети и интернет-порталом. Является лидером в России.", rub,
            countryEntities["Россия"]!!, listOf(), listOf(sectorEntities["Финансы"]!!, sectorEntities["Информационные технологии"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("YNDX", exchangeEntities["Московская биржа"]!!.id_exchange, "NL0009805522",
            rub.idCurrency!!, true, "YNDX.ME"))
        stockService!!.addOrUpdateStock(StockDto("YDX", exchangeEntities["Франкфуртская фондовая биржа"]!!.id_exchange, "NL0009805522",
            eur.idCurrency!!, true, "YDX.F"))

        companyService!!.addOrUpdateCompany(Company("US0378331005", "Apple", "Apple Inc. - американская корпорация, производитель персональных и планшетных компьютеров, аудиоплееров, телефонов, программного обеспечения.", usd,
            countryEntities["США"]!!, listOf(), listOf(sectorEntities["Финансы"]!!, sectorEntities["Информационные технологии"]!!,
                sectorEntities["Коммуникационные услуги"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("APC", exchangeEntities["Франкфуртская фондовая биржа"]!!.id_exchange, "US0378331005",
            eur.idCurrency!!, true, "APC.F"))
        stockService!!.addOrUpdateStock(StockDto("AAPL", exchangeEntities["СПБ биржа"]!!.id_exchange, "US0378331005",
            usd.idCurrency!!, true, "AAPL"))

        companyService!!.addOrUpdateCompany(Company("US5949181045", "Microsoft", "Microsoft Corporation разрабатывает, производит, лицензирует, продает и поддерживает программные продукты, а также игровые приставки.", usd,
            countryEntities["США"]!!, listOf(), listOf(sectorEntities["Информационные технологии"]!!, sectorEntities["Коммуникационные услуги"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("MSFT", exchangeEntities["NASDAQ"]!!.id_exchange, "US5949181045",
            usd.idCurrency!!, true, "MSFT"))
        stockService!!.addOrUpdateStock(StockDto("MSFT", exchangeEntities["СПБ биржа"]!!.id_exchange, "US5949181045",
            usd.idCurrency!!, true, "MSFT"))

        companyService!!.addOrUpdateCompany(Company("DE0007664005", "Volkswagen", "Volkswagen AG производит бюджетные, элитные и спортивные автомобили, а также грузовики и коммерческий автотранспорт. ", eur,
            countryEntities["Германия"]!!, listOf(), listOf(sectorEntities["Промышленность"]!!))
        )
        stockService!!.addOrUpdateStock(StockDto("VOW3", exchangeEntities["Франкфуртская фондовая биржа"]!!.id_exchange, "DE0007664005",
            eur.idCurrency!!, true, "VOW3.F"))
        stockService!!.addOrUpdateStock(StockDto("VOW3", exchangeEntities["СПБ биржа"]!!.id_exchange, "DE0007664005",
            eur.idCurrency!!, true, "VOW3.F"))

    }
}