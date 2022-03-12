package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.*
import ru.nuykin.involio.repository.*
import ru.nuykin.involio.util.security.JWTUtil
import ru.nuykin.involio.util.*

@Service
@Transactional
class StockService{
    @Autowired
    private val stockDao: StockRepository? = null
    @Autowired
    private val myUserDao: MyUserRepository? = null
    @Autowired
    private val portfolioDao: InvestmentPortfolioRepository? = null
    @Autowired
    private val portfolioService: InvestmentPortfolioService? = null
    @Autowired
    private val stockTransactionDao: StockTransactionRepository? = null
    @Autowired
    private val companyDao: CompanyRepository? = null
    @Autowired
    private val currencyDao: CurrencyRepository? = null
    @Autowired
    private val exchangeDao: ExchangeRepository? = null
    @Autowired
    private val jwtUtil: JWTUtil? = null

    // Вспомогательные функции
    fun getStock(ticker: String, idExchange: Int): Stock {
        val stockId: StockId = StockId(ticker, idExchange)
        return stockDao!!.findByIdOrNull(stockId)!!
    }
    fun getPortfolios(token: String): List<InvestmentPortfolio> =
        portfolioDao!!.findAllByOwner(
            myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        )!!

    fun addOrUpdateStock(stockDto: StockDto){

        val newStock: Stock = Stock(
            ticker = stockDto.ticker,
            exchange = exchangeDao!!.findByIdOrNull(stockDto.idExchange)!!,
            company = companyDao!!.findByIdOrNull(stockDto.CompanyISIN)!!,
            currency = currencyDao!!.findByIdOrNull(stockDto.idCurrency)!!,
            available = stockDto.isAvailableToUnqualifiedInvestors,
            ticker_on_yahoo_api = stockDto.tickerOnYahooApi
        )
        stockDao!!.save(newStock)
    }

    //Основные функции

    fun getTransactions(portfolio: InvestmentPortfolio, stock: Stock): List<Transaction>{
        val transactions: List<StockTransaction> = stockTransactionDao!!
            .getAllByInvestmentPortfolioInStockTransactionAndStockInTransaction(
                portfolio, stock
            )!!

        return transactions.map {
            Transaction(
                portfolio.name_portfolio!!,
                it.dateTransaction!!,
                it.itemCount!!,
                it.unitCost!!,
                it.saleOrPurchase!!
            ) }.sortedBy { it.date }
    }

    fun getStockOfOneCompanyInPortfolio(stock: Stock, portfolios: List<InvestmentPortfolio>): List<ItemInPortfolio>{
        val company: Company = stock.stock_company!!
        val listStockInPortfolio: ArrayList<ItemInPortfolio> = ArrayList<ItemInPortfolio>()
        for (curStock in company.stock!!)
            listStockInPortfolio.addAll(getStockInPortfolioInfo(curStock, portfolios))

        return listStockInPortfolio
    }

    fun getStockInPortfolioInfo(stock: Stock, portfolios: List<InvestmentPortfolio>): List<ItemInPortfolio>{
        val listStockInPortfolio: MutableList<ItemInPortfolio> = mutableListOf()
        for (portfolio in portfolios){
            val purchases:  List<CurrentPortfolioComposition> =
                portfolio.composition_of_portfolio!!.filter { it.ticker == stock.ticker && it.idExchange == stock.exchange!!.id_exchange }
            if (purchases.isNotEmpty()){
                val count: Int = purchases.sumOf { it.count!! }
                val totalPrice = getPrice(stock.ticker_on_yahoo_api!!) * count
                listStockInPortfolio.add(
                    ItemInPortfolio(
                        namePortfolio = portfolio.name_portfolio!!,
                        partOfPortfolio =
                            if (stock.trading_currency!!.idCurrency!! == "rub") totalPrice / portfolioService!!.getPriceInRuble(portfolio)
                            else totalPrice * getPrice(stock.trading_currency!!.id_on_yahoo_api!!) / portfolioService!!.getPriceInRuble(portfolio),
                        currencySign = stock.trading_currency!!.sign_currency!!,
                        countInPortfolio = count,
                        averagePurchasePrice = purchases.sumOf { it.count!! * it.priceOfUnit!! } / count,
                        purchases = purchases.map { Triple(it.date!!, it.count!!, it.priceOfUnit!!) },
                    )
                )
            }
        }

        return listStockInPortfolio
//        var listStockInPortfolio: ArrayList<ItemInPortfolio> = ArrayList<ItemInPortfolio>()
//
//        for(portfolio in portfolios){
//            val transactions: List<Transaction> = getTransactions(portfolio, stock)
//            var sale: Int = 0
//            var purchase: Int = 0
//            for (transaction in transactions){
//                if (transaction.saleOrPurchase == "SALE") sale+=1
//                else purchase+=1
//            }
//
//            if(sale == purchase) continue
//            else{
//                var numInPortfolio: Int = purchase - sale
//                val purchaseInPortfolio: ArrayList<Transaction> = ArrayList<Transaction>()
//                for (i in (transactions.size - 1)..0){
//                    if (transactions[i].saleOrPurchase == "PURCHASE" && transactions[i].count <= numInPortfolio){
//                        numInPortfolio-=transactions[i].count
//                        purchaseInPortfolio.add(transactions[i])
//                    }
//                    else if (transactions[i].saleOrPurchase == "PURCHASE" && transactions[i].count > numInPortfolio){
//                        transactions[i].count = numInPortfolio
//                        numInPortfolio = 0
//                        purchaseInPortfolio.add(transactions[i])
//                    }
//                    if (numInPortfolio == 0) break
//                }
//                var price: Double = purchaseInPortfolio.sumOf { it.count * it.price }
//                var count: Int = purchaseInPortfolio.sumOf { it.count }
//                val purchases: List<Triple<Date, Int, Double>> =purchaseInPortfolio.map { Triple(it.date, it.count, it.price) }
//                val currentPortfolioValue: Double = portfolioValueDao!!.findByDatePortfoliosValue(Date(System.currentTimeMillis()))!!.value_in_ruble!!
//
//                listStockInPortfolio.add(
//                    ItemInPortfolio(
//                        namePortfolio = portfolio.name_portfolio!!,
//                        partOfPortfolio = price / currentPortfolioValue,
//                        countInPortfolio = count,
//                        averagePurchasePrice = price / count,
//                        purchases = purchases,
//                        currencySign = stock.trading_currency!!.sign_currency!!
//                    )
//                )
//            }
//        }
//
//        return listStockInPortfolio
    }

    fun getStockInfo(ticker: String, idExchange: Int, token: String): StockInfoDto{
        val curStock: Stock = getStock(ticker, idExchange)
        val company: Company = curStock.stock_company!!

        val nameExchangeSource: String = curStock.exchange!!.name_exchange!!
        val nameOtherExchanges: List<String> = company.stock!!.map { it.exchange!!.name_exchange!! }
        val currentPrice: Double = getPrice(curStock.ticker_on_yahoo_api!!)
        val currencySign: String = curStock.trading_currency!!.sign_currency!!

        //Вкладка "В портфелях"
        val portfolioOwner: List<InvestmentPortfolio> = getPortfolios(token)
        val inPortfolio: List<ItemInPortfolio> = getStockOfOneCompanyInPortfolio(curStock, portfolioOwner)

        //Вкладка "Динамика цены"
        val dayInterval: List<Pair<Long, Double>> = getInterval(curStock.ticker_on_yahoo_api!!, "day")
        val weekInterval: List<Pair<Long, Double>> = getInterval(curStock.ticker_on_yahoo_api!!, "week")
        val monthInterval: List<Pair<Long, Double>> = getInterval(curStock.ticker_on_yahoo_api!!, "month")
        val yearInterval: List<Pair<Long, Double>> = getInterval(curStock.ticker_on_yahoo_api!!, "year")
        val fullInterval: List<Pair<Long, Double>> = getInterval(curStock.ticker_on_yahoo_api!!, "full")

        //Вкладка "О компании"
        val nameCompany: String = company.nameCompany!!
        val nameCountry: String = company.country!!.name_country!!
        val descriptionCompany: String = company.description!!
        val branch: List<String> = company.branch!!.map { it.nameBranch!! }
        val sector: List<String> = company.sector!!.map { it.name_sector!! }

        //Вкладка "Сделки"
        val transactions: ArrayList<Transaction> = ArrayList<Transaction>()
        for (i in portfolioOwner.map { getTransactions(it, curStock) }) transactions.addAll(i)


        //Вкладка "Дивиденды"
        val dividends: List<DividendsDto> = company.dividends!!.map { DividendsDto(it.date_dividends!!, it.absolut_size!!, it.relative_size!!) }


        return StockInfoDto(
            nameExchangeSource = nameExchangeSource,
            nameOtherExchanges = nameOtherExchanges,
            currentPrice = currentPrice,
            currencySign = currencySign,
            inPortfolio = inPortfolio,
            dayInterval = dayInterval,
            weekInterval = weekInterval,
            monthInterval = monthInterval,
            yearInterval = yearInterval,
            fullInterval = fullInterval,
            nameCompany = nameCompany,
            nameCountry = nameCountry,
            descriptionCompany = descriptionCompany,
            branch = branch,
            sector = sector,
            transactions = transactions,
            dividends = dividends,
        )
    }

    fun searchStock(searchString: String, numPage: Int): Map<String, List<SearchedElement>>{
        val page: Pageable = PageRequest.of(numPage, 5)
        val companies: List<Company> = companyDao!!.findByNameCompanyContainingIgnoreCase(searchString, page)
        val stocks: List<Stock> = stockDao!!.findByTickerContainingIgnoreCase(searchString, page)

        if (companies.isEmpty()) return emptyMap()
        else {
            val mapSearchedElement: MutableMap<String, List<Stock>> =
                (companies.map {
                    Pair(
                        it.nameCompany!!,
                        it.stock!!.toList()
                    )
                }).toMap() as MutableMap<String, List<Stock>>

            for (stock in stocks)
                if (stock.stock_company!!.nameCompany!! !in mapSearchedElement)
                    mapSearchedElement[stock.stock_company!!.nameCompany!!] = stock.stock_company!!.stock!!.toList()

            return mapSearchedElement.mapValues { mapPair ->
                mapPair.value.map { stock ->
                    SearchedElement(
                        stock.ticker!!,
                        stock.exchange!!.id_exchange,
                        stock.exchange!!.name_exchange!!,
                        getPrice(stock.ticker_on_yahoo_api!!),
                        stock.trading_currency!!.sign_currency!!
                    )
                }
            }
        }
    }

    fun getAllStock(numPage: Int): Map<String, List<SearchedElement>>{
        val page: Pageable = PageRequest.of(numPage, 5)
        val companies: List<Company> = companyDao!!.findAll(page).toList()

        if (companies.isEmpty()) return emptyMap()
        else{
            val mapSearchedElement: MutableMap<String, List<Stock>> =
                (companies.map { Pair(it.nameCompany!!, it.stock!!.toList()) }).toMap() as MutableMap<String, List<Stock>>

            return mapSearchedElement.mapValues { mapPair ->
                mapPair.value.map {  stock ->
                    SearchedElement(
                        stock.ticker!!,
                        stock.exchange!!.id_exchange,
                        stock.exchange!!.name_exchange!!,
                        getPrice(stock.ticker_on_yahoo_api!!),
                        stock.trading_currency!!.sign_currency!!
                    )
                }
            }
        }
    }
}