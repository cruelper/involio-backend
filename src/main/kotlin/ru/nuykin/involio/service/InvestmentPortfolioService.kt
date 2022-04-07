package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.*
import ru.nuykin.involio.model.Currency
import ru.nuykin.involio.repository.*
import ru.nuykin.involio.util.*
import ru.nuykin.involio.util.security.JWTUtil
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


@Service
@Transactional
class InvestmentPortfolioService {

    @Autowired
    private val portfolioDao: InvestmentPortfolioRepository? = null
    @Autowired
    private val myUserDao: MyUserRepository? = null
    @Autowired
    private val typeOfBrokerAccountDao: TypeOfBrokerAccountRepository? = null
    @Autowired
    private val brokerDao: BrokerRepository? = null
    @Autowired
    private val portfoliosValueHistory: PortfoliosValueHistoryRepository? = null
    @Autowired
    private val exchangeDao: ExchangeRepository? = null
    @Autowired
    private val currencyDao: CurrencyRepository? = null
    @Autowired
    private val indicesAndIndicatorsDao: WorldIndicesAndIndicatorsRepository? = null
    @Autowired
    private val compositionService: CurrentPortfolioCompositionService? = null
    @Autowired
    private val stockDao: StockRepository? = null
    @Autowired
    private val jwtUtil: JWTUtil? = null

    fun getStock(ticker: String, idExchange: Int): Stock {
        val stockId: StockId = StockId(ticker, idExchange)
        return stockDao!!.findByIdOrNull(stockId)!!
    }

    fun getListPortfolio(token: String): List<PortfolioDto>{
        val portfolios: List<InvestmentPortfolio> = portfolioDao!!.findAllByOwner(
            myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        )!!
        return portfolios.map { PortfolioDto(
            id = it.id_investment_portfolio,
            idBroker = it.broker_of_portfolio!!.id_broker,
            idTypeBrokerAccount = it.type_broker_account!!.id_type_of_broker_account,
            name = it.name_portfolio!!,
            dataOfCreation = it.date_of_creation!!,
        ) }
    }

    fun createPortfolio(portfolio: PortfolioDto, token: String): Boolean{
        val owner: MyUser = myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        val typeOfBrokerAccount: TypeOfBrokerAccount = typeOfBrokerAccountDao!!.findByIdOrNull(portfolio.idTypeBrokerAccount)!!
        val broker: Broker = brokerDao!!.findByIdOrNull(portfolio.idBroker)!!

        var newPortfolio: InvestmentPortfolio = InvestmentPortfolio(
            owner = owner,
            name_portfolio = portfolio.name,
            type_broker_account = typeOfBrokerAccount,
            date_of_creation = getDateInRightFormat(portfolio.dataOfCreation),
            broker = broker,
        )
        newPortfolio = portfolioDao!!.save(newPortfolio)

        // заполнение данных в истории стоимости портфеля. По следующему принципу:
        // создаем запись на каждый день текущего месяца (до момента открытия счета) (в конце каждого месяца все записи кроме последней удаляются)
        // если счет был открыт более месяца назад, создаем запись за каждый месяц до текущего
        val curDateInCalendar: Calendar = GregorianCalendar()
        curDateInCalendar.time = getDateInRightFormat(curDateInCalendar.time)
        var isMonthStep: Boolean = false
        while (curDateInCalendar.time.time >= portfolio.dataOfCreation.time){
            val datInHistory: PortfoliosValueHistory = PortfoliosValueHistory(
                newPortfolio, Date(curDateInCalendar.time.time)
            )
            portfoliosValueHistory!!.save(datInHistory)
            if (isMonthStep) curDateInCalendar.add(Calendar.MONTH, -1)
            else{
                if (curDateInCalendar.get(Calendar.DAY_OF_MONTH) == 1) isMonthStep = true
                curDateInCalendar.add(Calendar.DAY_OF_YEAR, -1)
            }
        }

        return true
    }

    fun deletePortfolio(idPortfolio: Int, token: String): HttpStatus{
        val user: MyUser = myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        val owner: MyUser = portfolioDao!!.findByIdOrNull(idPortfolio)!!.owner!!
        if (user == owner) portfolioDao.delete(portfolioDao.findByIdOrNull(idPortfolio)!!)
        else return HttpStatus.NOT_ACCEPTABLE
        return HttpStatus.OK
    }

    fun getPriceInRuble(portfolio: InvestmentPortfolio): Double{
        val currencyPart: MutableMap<Currency, Double> = mutableMapOf()
        for (composition in portfolio.composition_of_portfolio!!){
            val stock: Stock = getStock(composition.ticker!!, composition.idExchange!!)
            val price: Double = getPrice(stock.ticker_on_yahoo_api!!)
            val currency: Currency = stock.trading_currency!!
            if (currency !in currencyPart) currencyPart[currency] = 0.0
            currencyPart[currency] = currencyPart[currency]!! + price * composition.count!!
        }

        var totalPrice: Double = 0.0
        for (i in currencyPart)  totalPrice +=
            if (i.key.idCurrency != "rub") getPrice(i.key.id_on_yahoo_api!!) * i.value
            else i.value

        return totalPrice
    }

    fun getChangePrices(portfolio: InvestmentPortfolio): Triple<ChangePrice, ChangePrice, ChangePrice>{

        val curDate: Date = getRightCurDate()
        val yearAgoDay: Date = getRightSomeTimeWithStepDate(curDate, Calendar.YEAR, -1)
        val dayAgoDay: Date = getRightSomeTimeWithStepDate(curDate, Calendar.DAY_OF_YEAR, -1)

        //В рублях
        val curDayInHistory: PortfoliosValueHistory = portfolio.history_of_portfolio!!.find { it.datePortfoliosValue!!.time == curDate.time }!!
        curDayInHistory.value_in_ruble = getPriceInRuble(portfolio)
        val yearAgoDayInHistory: PortfoliosValueHistory? =
            portfolio.history_of_portfolio!!.find { it.datePortfoliosValue!!.time ==  yearAgoDay.time}
        val dayAgoDayInHistory: PortfoliosValueHistory? =
            portfolio.history_of_portfolio!!.find { it.datePortfoliosValue!!.time ==  dayAgoDay.time}
        val changeReplenishmentAndPriceInRuble: Double = curDayInHistory.value_in_ruble!! - curDayInHistory.replenishmentAmountInRuble!!

        var priceChangeOnYearInRub: Double =
            if (yearAgoDayInHistory == null) changeReplenishmentAndPriceInRuble
            else changeReplenishmentAndPriceInRuble - (yearAgoDayInHistory.value_in_ruble!! - yearAgoDayInHistory.replenishmentAmountInRuble!!)

        val priceChangeOnDayInRub: Double =
            if (dayAgoDayInHistory == null) changeReplenishmentAndPriceInRuble
            else changeReplenishmentAndPriceInRuble - (dayAgoDayInHistory.value_in_ruble!! - dayAgoDayInHistory.replenishmentAmountInRuble!!)

        val changePriceInRuble: ChangePrice = ChangePrice(priceChangeOnDayInRub, priceChangeOnYearInRub, changeReplenishmentAndPriceInRuble)

        // в долларах
        val usdIdOnYahooApi: String = currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!
        val changeReplenishmentAndPriceInUSD: Double =
            curDayInHistory.value_in_ruble!! / getPrice(usdIdOnYahooApi) - curDayInHistory.replenishmentAmountInUSD!!

        val priceChangeOnYearInUSD: Double =
            if (yearAgoDayInHistory == null) changeReplenishmentAndPriceInUSD
            else changeReplenishmentAndPriceInUSD -
                    (yearAgoDayInHistory.value_in_ruble!! / getInterval(usdIdOnYahooApi, "year")[0].second - yearAgoDayInHistory.replenishmentAmountInUSD!!)

        val priceChangeOnDayInUSD: Double =
            if (dayAgoDayInHistory == null) changeReplenishmentAndPriceInUSD
            else changeReplenishmentAndPriceInUSD -
                    (dayAgoDayInHistory.value_in_ruble!! / getInterval(usdIdOnYahooApi, "day")[0].second - dayAgoDayInHistory.replenishmentAmountInUSD!!)

        val changePriceInUSD: ChangePrice = ChangePrice(priceChangeOnDayInUSD, priceChangeOnYearInUSD, changeReplenishmentAndPriceInUSD)

        // в евро
        val euroIdOnYahooApi: String = currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!
        val changeReplenishmentAndPriceInEURO: Double =
            curDayInHistory.value_in_ruble!! / getPrice(euroIdOnYahooApi) - curDayInHistory.replenishmentAmountInEuro!!

        val priceChangeOnYearInEURO: Double =
            if (yearAgoDayInHistory == null) changeReplenishmentAndPriceInEURO
            else changeReplenishmentAndPriceInEURO -
                    (yearAgoDayInHistory.value_in_ruble!! / getInterval(euroIdOnYahooApi, "year")[0].second - yearAgoDayInHistory.replenishmentAmountInEuro!!)

        val priceChangeOnDayInEURO: Double =
            if (dayAgoDayInHistory == null) changeReplenishmentAndPriceInEURO
            else changeReplenishmentAndPriceInEURO -
                    (dayAgoDayInHistory.value_in_ruble!! / getInterval(euroIdOnYahooApi, "day")[0].second - dayAgoDayInHistory.replenishmentAmountInEuro!!)

        val changePriceInEURO: ChangePrice = ChangePrice(priceChangeOnDayInEURO, priceChangeOnYearInEURO, changeReplenishmentAndPriceInEURO)

        return Triple(changePriceInRuble, changePriceInUSD, changePriceInEURO)
    }

    fun getBasicPortfolioInfo(idPortfolio: Int, token: String, needToGroupStockOfOneCompany: Boolean): BasicPortfolioInfoDto?{
        val user: MyUser = myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        val owner: MyUser = portfolioDao!!.findByIdOrNull(idPortfolio)!!.owner!!
        if (user == owner){
            val portfolio: InvestmentPortfolio = portfolioDao.findByIdOrNull(idPortfolio)!!

            val curPriceInRuble: Double = getPriceInRuble(portfolio)

            val changePrices: Triple<ChangePrice, ChangePrice, ChangePrice> = getChangePrices(portfolio)
            val inRublePortfolioPrice: PortfolioPrice = PortfolioPrice(
                currentPrice = curPriceInRuble,
                changePrice = changePrices.first,
                currencySign = '₽',
            )

            val curUSDPrice: Double = getPrice(currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!)
            val inUSDPortfolioPrice: PortfolioPrice = PortfolioPrice(
                currentPrice = curPriceInRuble / curUSDPrice,
                changePrice = changePrices.second,
                currencySign = '$',
            )

            val curEUROPrice: Double = getPrice(currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!)
            val inEUROPortfolioPrice: PortfolioPrice = PortfolioPrice(
                currentPrice = curPriceInRuble / curEUROPrice,
                changePrice = changePrices.third,
                currencySign = '€',
            )

            val SP500Change = getChangeIndexFromUSDOrEuro("s&p500", portfolio, "usd")

            val IMOEXChange = getChangeIndexFromRub("imoex", portfolio)

            return BasicPortfolioInfoDto(
                id = idPortfolio,
                name = portfolio.name_portfolio!!,
                nameBroker = portfolio.broker_of_portfolio!!.name_broker!!,
                nameTypeOfBrokerAccount = portfolio.type_broker_account!!.name_type_of_broker_account!!,

                InRuble = inRublePortfolioPrice,
                InUSD = inUSDPortfolioPrice,
                InEuro = inEUROPortfolioPrice,

                ChangeSP500InRuble = SP500Change["rub"]!!,
                ChangeSP500InUSD = SP500Change["usd"]!!,
                ChangeSP500InEURO = SP500Change["eur"]!!,

                ChangeIMOEXInRuble = IMOEXChange["rub"]!!,
                ChangeIMOEXInUSD = IMOEXChange["usd"]!!,
                ChangeIMOEXInEURO = IMOEXChange["eur"]!!,

                stocksInPortfolio = getStocksInPortfolio(portfolio, curPriceInRuble, needToGroupStockOfOneCompany),
            )
        }
        else return null
    }

    fun getStocksInPortfolio(portfolio: InvestmentPortfolio, portfolioPriceInRuble: Double, needToGroupStockOfOneCompany: Boolean): List<StockInPortfolio>{
        val rub: Currency = currencyDao!!.findByIdOrNull("rub")!!
        var stocksInPortfolio: MutableMap<Stock, Int> = mutableMapOf()
        for (i in portfolio.composition_of_portfolio!!){
            val curStock: Stock = getStock(i.ticker!!, i.idExchange!!)
            if (curStock in stocksInPortfolio) stocksInPortfolio[curStock] = stocksInPortfolio[curStock]!! + i.count!!
            else stocksInPortfolio[curStock] = i.count!!
        }

        if (needToGroupStockOfOneCompany){
            val countryMap: MutableMap<Company, Pair<Stock, Int>> = mutableMapOf()
            for (i in stocksInPortfolio){
                val company: Company = i.key.stock_company!!
                if (company in countryMap){
                    val p: Pair<Stock, Int> = countryMap[company]!!
                    countryMap[company] = p.copy(second = p.second + i.value)
                }
                else countryMap[company] = Pair(i.key, i.value)
            }

            stocksInPortfolio.clear()
            stocksInPortfolio.putAll(countryMap.map { it.value })
        }

        val listStocksInPortfolio: MutableList<StockInPortfolio> = mutableListOf()
        for(i in stocksInPortfolio){
            val tickerOnYahooApi: String = i.key.ticker_on_yahoo_api!!
            val price: Double = getPrice(tickerOnYahooApi)
            val dayInterval: List<Pair<Long, Double>> = getInterval(tickerOnYahooApi, "day")
            val yearInterval: List<Pair<Long, Double>> = getInterval(tickerOnYahooApi, "year")
            val arbitraryInterval: List<Pair<Long, Double>> = getIntervalInListDatePrice(tickerOnYahooApi, (portfolio.date_of_creation!!.time / 1000L).toString(), "1d")

            listStocksInPortfolio.add(
                StockInPortfolio(
                    name = i.key.stock_company!!.nameCompany!!,
                    ticker = if (needToGroupStockOfOneCompany) "" else i.key.ticker!!,
                    count = i.value,
                    currentUnitPrice = price,
                    partOfPortfolio =
                    if (i.key.trading_currency!! == rub) (price * i.value)  / portfolioPriceInRuble
                    else (price * i.value) * getPrice(i.key.trading_currency!!.id_on_yahoo_api!!)  / portfolioPriceInRuble,
                    changePrice = ChangePrice(
                        priceChangeOnDay = dayInterval[dayInterval.size - 1].second / dayInterval[0].second,
                        priceChangeOnYear = yearInterval[yearInterval.size - 1].second / yearInterval[0].second,
                        priceChangeOnAllTime = arbitraryInterval[arbitraryInterval.size - 1].second / arbitraryInterval[0].second,
                    ),
                    idExchange = i.key.exchange!!.id_exchange!!,
                    nameExchange = i.key.exchange!!.name_exchange!!,
                )
            )
        }

        return  listStocksInPortfolio
    }

    fun getChangeIndexFromUSDOrEuro(indexTicker: String, portfolio: InvestmentPortfolio, tickerFromCurrency: String): Map<String, ChangePrice>{
        val dateOfCreation: Long = portfolio.date_of_creation!!.time / 1000L
        val calendar = GregorianCalendar()
        val dayAgo = getRightSomeTimeWithStepDate(calendar.time, Calendar.DAY_OF_YEAR, -1).time / 1000L
        val yearAgo = getRightSomeTimeWithStepDate(calendar.time, Calendar.YEAR, -1).time / 1000L

        val USDYahooTicker: String
        val EuroYahooTicker: String
        if (tickerFromCurrency == "eur"){
            USDYahooTicker = currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!
            EuroYahooTicker = currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!
        }
        else{
            EuroYahooTicker = currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!
            USDYahooTicker = currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!
        }

        val change: ChangePrice =
            getChangeIndicesOrIndicators(indicesAndIndicatorsDao!!
                .findByIdOrNull(indexTicker)!!.tickerOnYahooApi!!, dateOfCreation)

        val changeInRub: ChangePrice = ChangePrice(
            change.priceChangeOnDay * currencyRateChangeInRuble(USDYahooTicker, dayAgo),
            change.priceChangeOnYear * currencyRateChangeInRuble(USDYahooTicker, yearAgo),
            change.priceChangeOnAllTime * currencyRateChangeInRuble(USDYahooTicker, dateOfCreation)
        )

        val changeInEuro: ChangePrice = ChangePrice(
            changeInRub.priceChangeOnDay / currencyRateChangeInRuble(EuroYahooTicker, dayAgo),
            changeInRub.priceChangeOnYear / currencyRateChangeInRuble(EuroYahooTicker, yearAgo),
            changeInRub.priceChangeOnAllTime / currencyRateChangeInRuble(EuroYahooTicker, dateOfCreation)
        )

        val map: MutableMap<String, ChangePrice> = mutableMapOf()
        map["rub"] = changeInRub
        if (tickerFromCurrency == "eur"){
            map["eur"] = change
            map["usd"] = changeInEuro
        }
        else{
            map["eur"] = changeInEuro
            map["usd"] = change
        }
        return map
    }

    fun getChangeIndexFromRub(indexTicker: String, portfolio: InvestmentPortfolio): Map<String, ChangePrice>{
        val dateOfCreation: Long = portfolio.date_of_creation!!.time / 1000L
        val unixTime: Long = System.currentTimeMillis() / 1000L

        val map: MutableMap<String, ChangePrice> = mutableMapOf()
        val IndexInRub: ChangePrice = getChangeIndicesOrIndicators(
                indicesAndIndicatorsDao!!.findByIdOrNull("imoex")!!.tickerOnYahooApi!!,
                portfolio.date_of_creation!!.time / 1000L
        )
        map["rub"] = IndexInRub
        val USDYahooTicker = currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!
        val EuroYahooTicker = currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!
        map["usd"] = ChangePrice(
            IndexInRub.priceChangeOnDay / currencyRateChangeInRuble(USDYahooTicker, unixTime - 86400),
            IndexInRub.priceChangeOnYear / currencyRateChangeInRuble(USDYahooTicker, unixTime - 86400 * 365),
            IndexInRub.priceChangeOnAllTime / currencyRateChangeInRuble(EuroYahooTicker, dateOfCreation)
        )
        map["eur"] = ChangePrice(
            IndexInRub.priceChangeOnDay / currencyRateChangeInRuble(EuroYahooTicker, unixTime - 86400),
            IndexInRub.priceChangeOnYear / currencyRateChangeInRuble(EuroYahooTicker, unixTime - 86400 * 365),
            IndexInRub.priceChangeOnAllTime / currencyRateChangeInRuble(EuroYahooTicker, dateOfCreation)
        )

        return map
    }

    fun getExtendedPortfolioInfo(idPortfolio: Int, token: String): ExtendedPortfolioInfoDto?{
        val user: MyUser = myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        val owner: MyUser = portfolioDao!!.findByIdOrNull(idPortfolio)!!.owner!!
        if (user == owner){
            val portfolio: InvestmentPortfolio = portfolioDao!!.findByIdOrNull(idPortfolio)!!
            val portfolioPriceInRuble: Double = getPriceInRuble(portfolio)

            var stocksPrice: Double = 0.0
            val companiesMap: MutableMap<Company, Double> = mutableMapOf()
            val branchesMap: MutableMap<Branch, Double> = mutableMapOf()
            val sectorsMap: MutableMap<Sector, Double> = mutableMapOf()
            val currenciesMap: MutableMap<Currency, Double> = mutableMapOf()
            for (curComposition in portfolio.composition_of_portfolio!!){
                val curStock: Stock = getStock(curComposition.ticker!!, curComposition.idExchange!!)
                var stockPrice: Double = curComposition.count!! * getPrice(curStock.ticker_on_yahoo_api!!)
                if (curStock.trading_currency!!.idCurrency != "rub") stockPrice *= getPrice(curStock.trading_currency!!.id_on_yahoo_api!!)

                companiesMap[curStock.stock_company!!] =
                    if (curStock.stock_company in companiesMap) companiesMap[curStock.stock_company]!! + stockPrice
                    else stocksPrice

                stocksPrice += stockPrice

                for (curBranch in curStock.stock_company!!.branch!!)
                    branchesMap[curBranch] =
                        if (curBranch in branchesMap) branchesMap[curBranch]!! + stockPrice / curStock.stock_company!!.branch!!.size
                        else stocksPrice

                for (curSector in curStock.stock_company!!.sector!!)
                    sectorsMap[curSector] =
                        if (curSector in sectorsMap) sectorsMap[curSector]!! + stockPrice / curStock.stock_company!!.sector!!.size
                        else stocksPrice

                val stockCurrency: Currency = curStock.trading_currency!!
                currenciesMap[stockCurrency] =
                        if (stockCurrency in currenciesMap) currenciesMap[stockCurrency]!! + stockPrice
                        else stocksPrice
            }

            return ExtendedPortfolioInfoDto(
                id = idPortfolio,
                name = portfolio.name_portfolio!!,
                nameBroker = portfolio.broker_of_portfolio!!.name_broker!!,
                nameTypeOfBrokerAccount = portfolio.type_broker_account!!.name_type_of_broker_account!!,
                dateOfCreation = portfolio.date_of_creation!!,
                signs = mapOf("rub" to '₽', "usd" to '$', "eur" to '€'),
                curPriceInRubl = portfolioPriceInRuble,
                curPriceInUsd = portfolioPriceInRuble * getPrice(currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!),
                curPriceInEuro = portfolioPriceInRuble * getPrice(currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!),
                rubInterval = getPortfolioValueInterval(portfolio, "rub"),
                usdInterval = getPortfolioValueInterval(portfolio, "usd"),
                euroInterval = getPortfolioValueInterval(portfolio, "eur"),
                assets =
                if (portfolioPriceInRuble == 0.0) listOf(Pair("stocks", 0.0), Pair("currency", 0.0))
                else listOf(Pair("stocks", stocksPrice / portfolioPriceInRuble), Pair("currency", 100 - stocksPrice / portfolioPriceInRuble)),
                companies = companiesMap.toList().map { Pair(it.first.nameCompany!!, it.second / portfolioPriceInRuble) },
                branches = branchesMap.toList().map { Pair(it.first.nameBranch!!, it.second / portfolioPriceInRuble) },
                sectors = sectorsMap.toList().map { Pair(it.first.name_sector!!, it.second / portfolioPriceInRuble) },
                currencies = currenciesMap.toList().map { Pair(it.first.name_currency!!, it.second / portfolioPriceInRuble) },
                tax = getTaxInfo(portfolio),
            )
        }
        else return null
    }

    fun getPortfolioValueInterval(portfolio: InvestmentPortfolio, idCurrency: String): ValuesInInterval{
        fun getDayOfHistory(month: Int, year: Int): PortfoliosValueHistory? {
            val myCal = GregorianCalendar(year, month, 1)
            val daysInMonth = myCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val s = "$daysInMonth.$month.$year"
            val format = SimpleDateFormat()
            format.applyPattern("dd.MM.yyyy")
            val docDate: Date = Date(format.parse(s).time)

            return portfolio.history_of_portfolio!!.find { it.datePortfoliosValue!! ==  docDate}
        }
        fun getBasicValues(interval: List<PortfoliosValueHistory>, idCurrency: String):BasicValues{
            return when(idCurrency){
                "usd" -> BasicValues(
                    income = interval[interval.size - 1].value_in_usd!! - interval[0].value_in_usd!!,
                    dividends = interval[interval.size - 1].receivedDividendsInUsd!! - interval[0].receivedDividendsInUsd!!,
                    brokerCommission = interval[interval.size - 1].brokerCommissionInUsd!! - interval[0].brokerCommissionInUsd!!,
                    depositsAndWithdrawalsDiff = interval[interval.size - 1].replenishmentAmountInUSD!! - interval[0].replenishmentAmountInUSD!!,
                )
                "eur" -> BasicValues(
                    income = interval[interval.size - 1].value_in_euro!! - interval[0].value_in_euro!!,
                    dividends = interval[interval.size - 1].receivedDividendsInEuro!! - interval[0].receivedDividendsInEuro!!,
                    brokerCommission = interval[interval.size - 1].brokerCommissionInEuro!! - interval[0].brokerCommissionInEuro!!,
                    depositsAndWithdrawalsDiff = interval[interval.size - 1].replenishmentAmountInEuro!! - interval[0].replenishmentAmountInEuro!!,
                )
                else -> BasicValues(
                    income = interval[interval.size - 1].value_in_ruble!! - interval[0].value_in_ruble!!,
                    dividends = interval[interval.size - 1].receivedDividendsInRuble!! - interval[0].receivedDividendsInRuble!!,
                    brokerCommission = interval[interval.size - 1].brokerCommissionInRuble!! - interval[0].brokerCommissionInRuble!!,
                    depositsAndWithdrawalsDiff = interval[interval.size - 1].replenishmentAmountInRuble!! - interval[0].replenishmentAmountInRuble!!,
                )
            }
        }

//        var curTime: Long = System.currentTimeMillis()
//        curTime -= curTime % (86400 * 1000)

        var curTime = getRightCurDate()

        val monthInterval: List<PortfoliosValueHistory> = portfolio.history_of_portfolio!!.filter {
            it.datePortfoliosValue!! >= getRightSomeTimeWithStepDate(curTime, Calendar.MONTH, -1)
        }

        var curMonth: Int = Calendar.MONTH - 1
        var curYear: Int = Calendar.YEAR
        val yearInterval: MutableList<PortfoliosValueHistory> = mutableListOf()
        for (i in 1..12){
            if (curMonth == 0) {
                curMonth = 12
                curYear -= 1
            }
            val dayOfHistory: PortfoliosValueHistory? = getDayOfHistory(curMonth, curYear)
            if (dayOfHistory == null) break
            else{
                yearInterval.add(0, dayOfHistory)
                curMonth -=1
            }
        }

        val lastDay = getRightSomeTimeWithStepDate(curTime, Calendar.DAY_OF_YEAR, -1).time

        yearInterval.add(portfolio.history_of_portfolio!!.find { it.datePortfoliosValue!!.time ==  lastDay}!!)

        val allInterval: MutableList<PortfoliosValueHistory> = mutableListOf()
        val isEnd: Boolean = true
        while (isEnd){
            curYear -= 1
            val dayOfHistory: PortfoliosValueHistory? = getDayOfHistory(12, curYear)
            if (dayOfHistory == null) break
            else allInterval.add(0, dayOfHistory)
        }
        allInterval.add(portfolio.history_of_portfolio!!.find { it.datePortfoliosValue!!.time ==  lastDay}!!)

        return ValuesInInterval(
            monthInterval = when(idCurrency){
                "usd" -> monthInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_usd!!) }
                "eur" -> monthInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_euro!!) }
                else -> monthInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_ruble!!) } },
            monthData = getBasicValues(monthInterval, idCurrency),
            yearInterval = when(idCurrency){
                "usd" -> yearInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_usd!!) }
                "eur" -> yearInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_euro!!) }
                else -> yearInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_ruble!!) } },
            yearData = getBasicValues(yearInterval, idCurrency),
            allInterval = when(idCurrency){
                "usd" -> allInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_usd!!) }
                "eur" -> allInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_euro!!) }
                else -> allInterval.map { Pair(it.datePortfoliosValue!!.time / 1000, it.value_in_ruble!!) } },
            allData = getBasicValues(allInterval, idCurrency),
        )
    }

    fun getIndicesInterval(portfolio: InvestmentPortfolio, idIndices: String): Triple<List<Double>, List<Double>, List<Double>>{
        val curIndices: WorldIndicesAndIndicators = indicesAndIndicatorsDao!!.findByIdOrNull(idIndices)!!
        val indicesInterval: List<Pair<Long, Double>> = getIntervalInListDatePrice(curIndices.tickerOnYahooApi!!, (portfolio.date_of_creation!!.time / 1000L).toString(), "1d")

        val indicesIntervalInDouble: MutableList<Double> = mutableListOf()
        for (element in indicesInterval) indicesIntervalInDouble.add(element.toString().toDouble())

        val measure: Currency? = currencyDao!!.findByIdOrNull(curIndices.measure!!)

        return if (measure == null) Triple(indicesIntervalInDouble, indicesIntervalInDouble, indicesIntervalInDouble)
        else{
            val rubPriceInUsd: Double = getPrice(currencyDao!!.findByIdOrNull("usd")!!.id_on_yahoo_api!!)
            val rubPriceInEuro: Double = getPrice(currencyDao!!.findByIdOrNull("eur")!!.id_on_yahoo_api!!)
            val rubPriceInIndicesMeasure: Double = getPrice(measure.id_on_yahoo_api!!)

            if(measure.idCurrency!! == "rub")
                Triple(indicesIntervalInDouble, indicesIntervalInDouble.map { it / rubPriceInUsd }, indicesIntervalInDouble.map { it / rubPriceInEuro })
            else{
                val indicesIntervalInRuble: List<Double> = indicesIntervalInDouble.map { it / rubPriceInIndicesMeasure }
                Triple(indicesIntervalInRuble, indicesIntervalInRuble.map { it / rubPriceInUsd }, indicesIntervalInRuble.map { it / rubPriceInEuro })
            }
        }
    }

    fun getTaxInfo(portfolio: InvestmentPortfolio): String{
        fun getStocksIncome(): Double{
            val stocksQueue: MutableMap<Stock, Queue<StockTransaction>> = mutableMapOf()
            var totalIncome: Double = 0.0

            for (curTransaction in portfolio.stock_transaction_of_portfolio!!){
                if (curTransaction.saleOrPurchase!! == "purchase"){
                    if (curTransaction.stockInTransaction!! !in stocksQueue)
                        stocksQueue[curTransaction.stockInTransaction!!] = LinkedList<StockTransaction>()
                    stocksQueue[curTransaction.stockInTransaction!!]!!.add(curTransaction)
                }
                else{
                    var countUnitOnTransaction: Int = curTransaction.itemCount!!
                    while (countUnitOnTransaction > 0){
                        val firstInQueue: StockTransaction = stocksQueue[curTransaction.stockInTransaction!!]!!.peek()
                        if (firstInQueue.itemCount!! <= countUnitOnTransaction) stocksQueue[curTransaction.stockInTransaction!!]!!.poll()
                        countUnitOnTransaction -= firstInQueue.itemCount!! % countUnitOnTransaction
                        val incomeInSomeCurrency: Double = (firstInQueue.itemCount!! % countUnitOnTransaction) *
                                (getPrice(curTransaction.stockInTransaction!!.ticker_on_yahoo_api!!) - firstInQueue.unitCost!!)

                        val curCurrency: Currency = curTransaction.stockInTransaction!!.trading_currency!!
                        if (incomeInSomeCurrency > 0)
                            totalIncome += if (curCurrency.idCurrency!! == "rub") incomeInSomeCurrency
                            else incomeInSomeCurrency * getPrice(curCurrency.id_on_yahoo_api!!) *
                                    currencyRateChangeInRuble(curCurrency.id_on_yahoo_api!!, firstInQueue.dateTransaction!!.time / 1000L)
                    }
                }
            }

            return totalIncome
        }
        fun getCurrencyIncome(): Double{
            val currenciesQueue: MutableMap<Currency, Queue<CurrencyTransaction>> = mutableMapOf()
            var totalIncome: Double = 0.0

            for (curTransaction in portfolio.currency_transaction_of_portfolio!!){
                if (curTransaction.sale_or_purchase!! == "purchase"){
                    if (curTransaction.currencyInTransaction!! !in currenciesQueue)
                        currenciesQueue[curTransaction.currencyInTransaction!!] = LinkedList<CurrencyTransaction>()
                    currenciesQueue[curTransaction.currencyInTransaction!!]!!.add(curTransaction)
                }
                else{
                    var countUnitOnTransaction: Int = curTransaction.item_count!!
                    while (countUnitOnTransaction > 0){
                        val firstInQueue: CurrencyTransaction = currenciesQueue[curTransaction.currencyInTransaction!!]!!.peek()
                        if (firstInQueue.item_count!! <= countUnitOnTransaction) currenciesQueue[curTransaction.currencyInTransaction!!]!!.poll()
                        countUnitOnTransaction -= firstInQueue.item_count!! % countUnitOnTransaction

                        val curCurrency: Currency = firstInQueue.currencyInTransaction!!
                        val rateChange: Double = currencyRateChangeInRuble(curCurrency.id_on_yahoo_api!!, firstInQueue.date_transaction!!.time / 1000L)
                        val curPriceInRuble: Double = getPrice(curCurrency.id_on_yahoo_api!!)
                        if (rateChange > 1)
                            totalIncome += (firstInQueue.item_count!! % countUnitOnTransaction) * (curPriceInRuble - curPriceInRuble / rateChange)
                    }
                }
            }

            return totalIncome
        }
        fun getTaxDefaultBrokerAccount(): String{
            val receivedDividendsInRuble: Double = portfolio.history_of_portfolio!!.maxOf { it.receivedDividendsInRuble!! }
            val dividendsTax: Double =
                if (receivedDividendsInRuble > 5e6) (receivedDividendsInRuble - 5e6) * 0.15 + 5e6 * 0.13
                else receivedDividendsInRuble * 0.13

            val totalIncome: Double = getStocksIncome() + getCurrencyIncome()
            val totalTransactionTax: Double = if (totalIncome > 5e6) (totalIncome - 5e6) * 0.15 + 5e6 * 0.13
            else totalIncome * 0.13

            return "Налог за инвестирование на обычном брокерском счете нужно платить в слудующих случаях:\n" +
                    "- Акция была продана дороже, чем приобретена\n" +
                    "- Иностранная валюта была продана дороже, чем приобретена\n" +
                    "- Были получены дивиденды\n" +
                    "Учет прибыли за продажу акций осуществляется по принципу ФИФО (First In First Out). " +
                    "ФИФО применяется, когда акции определенной компании покупались в ходе нескольких сделок и по разным ценам. " +
                    "Тогда при продаже части позиции по этому активу первыми будут учитываться те акции, которые приобретались раньше всего.\n" +
                    "Налог на полученные дивинденды: $dividendsTax рублей.\n" +
                    "Налог на прибыль со сделок: $totalTransactionTax рублей. \n" +
                    "Итого: ${dividendsTax + totalTransactionTax}"
        }
        fun getTaxBrokerAccountTypeA(): String{
            return "Расчет налогов на ИИС типа А осуществляется аналогично обычному брокерскому счету.\n" + getTaxDefaultBrokerAccount()
        }
        fun getTaxBrokerAccountTypeB(): String{
            val receivedDividendsInRuble: Double = portfolio.history_of_portfolio!!.maxOf { it.receivedDividendsInRuble!! }
            val totalTax: Double =
                if (receivedDividendsInRuble > 5e6) (receivedDividendsInRuble - 5e6) * 0.15 + 5e6 * 0.13
                else receivedDividendsInRuble * 0.13

            return  "ИИС типа Б снимет с вас обязанность платить налоги на доход от инвестиций, " +
                    "полученный за три года владения ИИС, кроме дохода по дивидендам. \n " +
                    "Налог на полученные дивиденды: $totalTax рублей."
        }
        // 1 - default, 2 - iis type A, 3 - iis type B
        return when (portfolio.type_broker_account!!.id_type_of_broker_account){
            1 -> getTaxDefaultBrokerAccount()
            2 -> getTaxBrokerAccountTypeA()
            3 -> getTaxBrokerAccountTypeB()
            else -> "Ошибка! Невозможно получить данные"
        }
    }

    fun getComparisonWithIndicesInfo(idPortfolio: Int, listIdIndices: List<String>): IndicesInterval{
        val portfolio: InvestmentPortfolio = portfolioDao!!.findByIdOrNull(idPortfolio)!!

        val listOfIndices: Map<String, Triple<List<Double>, List<Double>, List<Double>>> =
            listIdIndices.associateWith { getIndicesInterval(portfolio, it) }

        return  IndicesInterval(
            indicesIntervalsInRuble = listOfIndices.keys.associateWith { listOfIndices[it]!!.first },
            indicesIntervalsInUsd = listOfIndices.keys.associateWith { listOfIndices[it]!!.second },
            indicesIntervalsInEuro = listOfIndices.keys.associateWith { listOfIndices[it]!!.third },
        )
    }

    fun addStockToPortfolio(token: String, compositionOfPortfolio: CompositionOfPortfolioDto): Boolean{
        return try {
            val user: MyUser = myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
            val owner: MyUser = portfolioDao!!.findByIdOrNull(compositionOfPortfolio.idPortfolio)!!.owner!!
            if (user == owner){
                val portfolio = user.portfolio_of_user!!.find { it.id_investment_portfolio == compositionOfPortfolio.idPortfolio }!!
                compositionService!!.addStockToComposition(portfolio, compositionOfPortfolio)
                portfolio.history_of_portfolio!!.forEach { it ->

                }
                true
            }
            else false
        }catch (ex: Exception){
            false
        }
    }

    fun addCurrencyToPortfolio(token: String, compositionOfPortfolio: CompositionOfPortfolioDto): Boolean{
        return try {
            val user: MyUser = myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
            val owner: MyUser = portfolioDao!!.findByIdOrNull(compositionOfPortfolio.idPortfolio)!!.owner!!
            if (user == owner){
                val portfolio = user.portfolio_of_user!!.find { it.id_investment_portfolio == compositionOfPortfolio.idPortfolio }!!
                compositionService!!.addCurrencyToComposition(portfolio, compositionOfPortfolio)
                portfolio.history_of_portfolio!!.forEach { it ->

                }
                true
            }
            else false
        }catch (ex: Exception){
            false
        }
    }
}

