package ru.nuykin.involio.controller

import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.*
import ru.nuykin.involio.service.*

@RestController
@Api(description = "Контроллер для получения данных из таблиц-справочников и" +
        "управления этими данными администратором")
class DirectoryController {
    @Autowired private val branchService: BranchService? = null
    @Autowired private val brokerService: BrokerService? = null
    @Autowired private val companyService: CompanyService? = null
    @Autowired private val countryService: CountryService? = null
    @Autowired private val currencyService: CurrencyService? = null
    @Autowired private val dividendsService: DividendsService? = null
    @Autowired private val exchangeService: ExchangeService? = null
    @Autowired private val sectorService: SectorService? = null
//    @Autowired private val stockService: StockService? = null
    @Autowired private val typeOfBrokerAccountService: TypeOfBrokerAccountService? = null

    //BRANCH
    @GetMapping("/user/branch")
    fun getAllBranch(): List<BranchDto> = branchService!!.getAllBranch()

    @GetMapping("/user/branch/{id}")
    fun getBranchById(@PathVariable id: Int): BranchDto? = branchService!!.getDtoBranchById(id)

    @PostMapping("/admin/branch")
    fun addBranch(@RequestBody dto: BranchDto){
        branchService!!.addBranch(dto)
    }

    @PutMapping("/admin/branch")
    fun updateBranch(@RequestBody dto: BranchDto){
        branchService!!.updateBranch(dto)
    }

    @DeleteMapping("/admin/branch/{id}")
    fun deleteBranch(@PathVariable id: Int){
        branchService!!.deleteBranchById(id)
    }

    //BROKER
    @GetMapping("/user/broker")
    fun getAllBroker(): List<BrokerDto> = brokerService!!.getAllBroker()

    @GetMapping("/user/broker/{id}")
    fun getBrokerById(@PathVariable id: Int): BrokerDto? = brokerService!!.getBrokerById(id)

    @PostMapping("/admin/broker")
    fun addBroker(@RequestBody dto: BrokerDto){
        val listExchange: List<Exchange> = dto.listExchange.map { exchangeService!!.getExchangeById(it.id)!! }
        brokerService!!.addBroker(dto.name, listExchange)
    }

    @PutMapping("/admin/broker")
    fun updateBroker(@RequestBody dto: BrokerDto){
        val listExchange: List<Exchange> = dto.listExchange.map { exchangeService!!.getExchangeById(it.id)!! }
        brokerService!!.updateBroker(dto.id, dto.name, listExchange)
    }

    @DeleteMapping("/admin/broker/{id}")
    fun deleteBroker(@PathVariable id: Int){
        brokerService!!.deleteBrokerById(id)
    }

    //EXCHANGE
    @GetMapping("/user/exchange")
    fun getAllExchange(): List<ExchangeDto> = exchangeService!!.getAllExchange()

    @GetMapping("/user/exchange/{id}")
    fun getExchangeById(@PathVariable id: Int): ExchangeDto? = exchangeService!!.getDtoExchangeById(id)

    @PostMapping("/admin/exchange")
    fun addExchange(@RequestBody dto: ExchangeDto){
        exchangeService!!.addExchange(dto)
    }

    @PutMapping("/admin/exchange")
    fun updateExchange(@RequestBody dto: ExchangeDto){
        exchangeService!!.updateExchange(dto)
    }

    @DeleteMapping("/admin/exchange/{id}")
    fun deleteExchange(@PathVariable id: Int){
        exchangeService!!.deleteExchangeById(id)
    }

    //COUNTRY
    @GetMapping("/user/country")
    fun getAllCountry(): List<CountryDto> = countryService!!.getAllCountry()

    @GetMapping("/user/country/{id}")
    fun getCountryById(@PathVariable id: Int): CountryDto? = countryService!!.getDtoCountryById(id)

    @PostMapping("/admin/country")
    fun addCountry(@RequestBody dto: CountryDto){
        countryService!!.addCountry(dto)
    }

    @PutMapping("/admin/country")
    fun updateCountry(@RequestBody dto: CountryDto){
        countryService!!.updateCountry(dto)
    }

    @DeleteMapping("/admin/country/{id}")
    fun deleteCountry(@PathVariable id: Int){
        countryService!!.deleteCountryById(id)
    }

    //TYPEOFBROKERACCOUNT
    @GetMapping("/user/type-of-broker-account")
    fun getAllTypeOfBrokerAccount(): List<TypeOfBrokerAccountDto> = typeOfBrokerAccountService!!.getAllTypeOfBrokerAccount()

    @GetMapping("/user/type-of-broker-account/{id}")
    fun getTypeOfBrokerAccountById(@PathVariable id: Int): TypeOfBrokerAccountDto? = typeOfBrokerAccountService!!.getTypeOfBrokerAccountById(id)

    @PostMapping("/admin/type-of-broker-account")
    fun addTypeOfBrokerAccount(@RequestBody dto: TypeOfBrokerAccountDto){
        typeOfBrokerAccountService!!.addTypeOfBrokerAccount(dto)
    }

    @PutMapping("/admin/type-of-broker-account")
    fun updateTypeOfBrokerAccount(@RequestBody dto: TypeOfBrokerAccountDto){
        typeOfBrokerAccountService!!.updateTypeOfBrokerAccount(dto)
    }

    @DeleteMapping("/admin/type-of-broker-account/{id}")
    fun deleteTypeOfBrokerAccount(@PathVariable id: Int){
        typeOfBrokerAccountService!!.deleteTypeOfBrokerAccountById(id)
    }

    //SECTOR
    @GetMapping("/user/sector")
    fun getAllSector(): List<SectorDto> = sectorService!!.getAllSector()

    @GetMapping("/user/sector/{id}")
    fun getSectorById(@PathVariable id: Int): SectorDto? = sectorService!!.getDtoSectorById(id)

    @PostMapping("/admin/sector")
    fun addSector(@RequestBody dto: SectorDto){
        sectorService!!.addSector(dto)
    }

    @PutMapping("/admin/sector")
    fun updateSector(@RequestBody dto: SectorDto){
        sectorService!!.updateSector(dto)
    }

    @DeleteMapping("/admin/sector/{id}")
    fun deleteSector(@PathVariable id: Int){
        sectorService!!.deleteSectorById(id)
    }

    //CURRENCY
    @PostMapping("/admin/currency")
    fun addCurrency(@RequestBody dto: CurrencyDto){
        currencyService!!.addCurrency(dto)
    }

    @PutMapping("/admin/currency")
    fun updateCurrency(@RequestBody dto: CurrencyDto){
        currencyService!!.updateCurrency(dto)
    }

    @DeleteMapping("/admin/currency/{id}")
    fun deleteCurrency(@PathVariable id: String){
        currencyService!!.deleteCurrencyById(id)
    }

    //COMPANY
    @GetMapping("/user/company")
    fun getAllCompany(): List<CompanyDto> = companyService!!.getAllCompany()

    @GetMapping("/user/company/{id}")
    fun getCompanyById(@PathVariable id: String): CompanyDto? = companyService!!.getDtoCompanyById(id)

    fun getCompanyFromCompanyDto(dto: CompanyDto): Company{
        val currency: Currency = currencyService!!.getCurrencyById(dto.currency.id)!!
        val country: Country = countryService!!.getCountryById(dto.country.id)!!
        val branch: List<Branch> = dto.branch.map { branchService!!.getBranchById(it.id)!! }
        val sector: List<Sector> = dto.sector.map {  sectorService!!.getSectorById(it.id)!! }
        return Company(
            dto.isin,
            dto.nameCompany,
            dto.description,
            currency,
            country,
            branch,
            sector
        )
    }

    @PostMapping("/admin/company")
    fun addCompany(@RequestBody dto: CompanyDto){
        companyService!!.addOrUpdateCompany(getCompanyFromCompanyDto(dto))
    }

    @PutMapping("/admin/company")
    fun updateCompany(@RequestBody dto: CompanyDto){
        companyService!!.addOrUpdateCompany(getCompanyFromCompanyDto(dto))
    }

    @DeleteMapping("/admin/company/{id}")
    fun deleteCompany(@PathVariable id: String){
        companyService!!.deleteCompanyById(id)
    }

    //DIVIDENDS
    @GetMapping("/user/dividends/{isin}")
    fun getDividendsByCompanyIsin(@PathVariable isin: String): List<DividendsDto>{
        val company: Company = companyService!!.getCompanyById(isin)!!
        return dividendsService!!.getDividendsByCompanyIsin(company)
    }

    @PostMapping("/admin/dividends/{isin}")
    fun addDividendsToCompany(@PathVariable isin: String, @RequestBody dto: DividendsDto){
        dividendsService!!.addDividendsToCompany(companyService!!.getCompanyById(isin)!!, dto)
    }
}