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
    private val exchangeService: ExchangeService? = null
    @Autowired
    private val jwtUtil: JWTUtil? = null

    // Вспомогательные функции
    fun getStock(ticker: String, idExchange: Int): Stock {
        val stockId: StockId = StockId(ticker, exchangeService!!.getExchangeById(idExchange)!!)
        return stockDao!!.findByIdOrNull(stockId)!!
    }
    fun getPortfolios(token: String): List<InvestmentPortfolio> =
        portfolioDao!!.findAllByOwner(
            myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        )!!

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
                portfolio.composition_of_portfolio!!.filter { it.tiker == stock.ticker && it.id_exchange == stock.exchange!!.id_exchange }

            val count: Int = purchases.sumOf { it.count!! }
            var totalPrice: Double = purchases.sumOf { it.count!! * it.priceOfUnit!! }
            if (stock.trading_currency!!.idCurrency!! != "rub") totalPrice *= getPrice(stock.tiker_on_yahoo_api!!)

            listStockInPortfolio.add(
                ItemInPortfolio(
                namePortfolio = portfolio.name_portfolio!!,
                partOfPortfolio = totalPrice / portfolioService!!.getPriceInRuble(portfolio),
                currencySign = stock.trading_currency!!.sign_currency!!,
                countInPortfolio = count,
                averagePurchasePrice = totalPrice / count,
                purchases = purchases.map { Triple(it.date!!, it.count!!, it.priceOfUnit!!) },
            )
            )
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
        val currentPrice: Double = getPrice(curStock.tiker_on_yahoo_api!!)
        val currencySign: Char = curStock.trading_currency!!.sign_currency!!

        //Вкладка "В портфелях"
        val portfolioOwner: List<InvestmentPortfolio> = getPortfolios(token)
        val inPortfolio: List<ItemInPortfolio> = getStockOfOneCompanyInPortfolio(curStock, portfolioOwner)

        //Вкладка "Динамика цены"
        val dayInterval: List<Double> = getInterval(curStock.tiker_on_yahoo_api!!, "day")
        val weekInterval: List<Double> = getInterval(curStock.tiker_on_yahoo_api!!, "week")
        val monthInterval: List<Double> = getInterval(curStock.tiker_on_yahoo_api!!, "month")
        val yearInterval: List<Double> = getInterval(curStock.tiker_on_yahoo_api!!, "year")
        val fullInterval: List<Double> = getInterval(curStock.tiker_on_yahoo_api!!, "full")

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

    fun searchStock(searchString: String, numPage: Int): List<SearchedElement>{
        val page: Pageable = PageRequest.of(numPage, 10)
        val companies: List<Company> = companyDao!!.findByNameCompanyContainingIgnoreCase(searchString, page)
        val stocks: List<Stock> = stockDao!!.findByTickerContainingIgnoreCase(searchString, page)

        val mergeStockList: ArrayList<Stock> = ArrayList<Stock>()
        mergeStockList.addAll(stocks)
        for (company in companies) mergeStockList.addAll(company.stock!!)

        return mergeStockList.toSet().map {
            SearchedElement(
                it.stock_company!!.nameCompany!!,
                it.ticker!!,
                it.exchange!!.id_exchange!!,
                it.exchange!!.name_exchange!!
            ) }.toList()
    }
}