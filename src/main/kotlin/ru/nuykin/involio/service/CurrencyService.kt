package ru.nuykin.involio.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.nuykin.involio.dto.*
import ru.nuykin.involio.model.*
import ru.nuykin.involio.repository.*
import ru.nuykin.involio.util.*
import ru.nuykin.involio.util.security.JWTUtil
import java.util.Date

@Service
@Transactional
class CurrencyService{
    @Autowired
    private val currencyDao: CurrencyRepository? = null
    @Autowired
    private val myUserDao: MyUserRepository? = null
    @Autowired
    private val portfolioDao: InvestmentPortfolioRepository? = null
    @Autowired
    private val portfolioValueDao: PortfoliosValueHistoryRepository? = null
    @Autowired
    private val currencyTransactionDao: CurrencyTransactionRepository? = null
    @Autowired
    private val jwtUtil: JWTUtil? = null

    fun getAllCurrency(): List<CurrencyDto> =
        currencyDao!!.findAll().toList().map { CurrencyDto(it.idCurrency!!, it.name_currency!!, it.sign_currency!!, it.id_on_yahoo_api!! ) }

    fun getAllCurrencyWithPrice(): List<Pair<CurrencyDto, Double>> {
        val allCurrencyWithoutRuble = getAllCurrency().toMutableList()
        allCurrencyWithoutRuble.removeIf { it.id == "rub" }

        return allCurrencyWithoutRuble.map { Pair(it, getPrice(it.idOnYahooApi)) }
    }

    fun getCurrencyById(id: String): Currency? =
        currencyDao!!.findByIdOrNull(id)

    fun getDtoCurrencyById(id: String): CurrencyDto? {
        val currency: Currency? = currencyDao!!.findByIdOrNull(id)
        return if(currency == null) null else CurrencyDto(currency.idCurrency!!, currency.name_currency!!, currency.sign_currency!!, currency.id_on_yahoo_api!!)
    }

    fun addCurrency(currencyDto: CurrencyDto){
        val newCurrency: Currency = Currency(id = currencyDto.id, name = currencyDto.name, sign = currencyDto.sign, currencyDto.idOnYahooApi)
        currencyDao!!.save(newCurrency)
    }

    fun updateCurrency(currencyDto: CurrencyDto){
        val currency = currencyDao!!.findByIdOrNull(currencyDto.id)
        if(currency != null){
            currency.name_currency = currencyDto.name
            currencyDao.save(currency)
        }
    }

    fun deleteCurrencyById(id: String){
        currencyDao!!.deleteById(id)
    }

    // Вспомогательные функции
    fun getCurrency(id: String): Currency =
        currencyDao!!.findByIdOrNull(id)!!
    fun getPortfolios(token: String): List<InvestmentPortfolio> =
        portfolioDao!!.findAllByOwner(
            myUserDao!!.findByLogin(jwtUtil!!.extractUsername(token.substringAfter(' ')))!!
        )!!

    //Основные функции
    fun getTransactions(portfolio: InvestmentPortfolio, currency: Currency): List<Transaction>{
        val transactions: List<CurrencyTransaction> = currencyTransactionDao!!
            .findByInvestmentPortfolioInCurrencyTransactionAndCurrencyInTransaction(
                portfolio, currency
            )!!

        return transactions.map {
            Transaction(
                portfolio.name_portfolio!!,
                it.date_transaction!!,
                it.item_count!!,
                it.unit_cost!!,
                it.sale_or_purchase!!
            ) }.sortedBy { it.date }
    }

    fun getCurrencyInPortfolioInfo(currency: Currency, portfolios: List<InvestmentPortfolio>): List<ItemInPortfolio>{
        var listCurrencyInPortfolio: ArrayList<ItemInPortfolio> = ArrayList<ItemInPortfolio>()

        for(portfolio in portfolios){
            val transactions: List<Transaction> = getTransactions(portfolio, currency)
            var sale: Int = 0
            var purchase: Int = 0
            for (transaction in transactions){
                if (transaction.saleOrPurchase == "SALE") sale+=1
                else purchase+=1
            }

            if(sale == purchase) continue
            else{
                var numInPortfolio: Int = purchase - sale
                val purchaseInPortfolio: ArrayList<Transaction> = ArrayList<Transaction>()
                for (i in (transactions.size - 1)..0){
                    if (transactions[i].saleOrPurchase == "PURCHASE" && transactions[i].count <= numInPortfolio){
                        numInPortfolio-=transactions[i].count
                        purchaseInPortfolio.add(transactions[i])
                    }
                    else if (transactions[i].saleOrPurchase == "PURCHASE" && transactions[i].count > numInPortfolio){
                        transactions[i].count = numInPortfolio
                        numInPortfolio = 0
                        purchaseInPortfolio.add(transactions[i])
                    }
                    if (numInPortfolio == 0) break
                }
                var price: Double = purchaseInPortfolio.sumOf { it.count * it.price }
                var count: Int = purchaseInPortfolio.sumOf { it.count }
                val purchases: List<Triple<Date, Int, Double>> =purchaseInPortfolio.map { Triple(it.date, it.count, it.price) }
                val currentPortfolioValue: Double = portfolioValueDao!!.findByDatePortfoliosValue(Date(System.currentTimeMillis()))!!.value_in_ruble!!

                listCurrencyInPortfolio.add(
                    ItemInPortfolio(
                        namePortfolio = portfolio.name_portfolio!!,
                        partOfPortfolio = price / currentPortfolioValue,
                        countInPortfolio = count,
                        averagePurchasePrice = price / count,
                        purchases = purchases,
                        currencySign = currency.sign_currency!!
                    )
                )
            }
        }

        return listCurrencyInPortfolio
    }

    fun getCurrencyInfo(id: String, token: String): CurrencyInfoDto {
        val curCurrency: Currency = getCurrency(id)

        val currentPrice: Double = getPrice(curCurrency.id_on_yahoo_api!!)
        val currencySign: String = curCurrency.sign_currency!!

        //Вкладка "В портфелях"
        val portfolioOwner: List<InvestmentPortfolio> = getPortfolios(token)
        val inPortfolio: List<ItemInPortfolio> = getCurrencyInPortfolioInfo(curCurrency, portfolioOwner)

        //Вкладка "Динамика цены"
        val dayInterval: List<Pair<Long, Double>> = getInterval(curCurrency.id_on_yahoo_api!!, "day")
        val weekInterval: List<Pair<Long, Double>> = getInterval(curCurrency.id_on_yahoo_api!!, "week")
        val monthInterval: List<Pair<Long, Double>> = getInterval(curCurrency.id_on_yahoo_api!!, "month")
        val yearInterval: List<Pair<Long, Double>> = getInterval(curCurrency.id_on_yahoo_api!!, "year")
        val fullInterval: List<Pair<Long, Double>> = getInterval(curCurrency.id_on_yahoo_api!!, "full")

        return CurrencyInfoDto(
            currentPriceInRuble = currentPrice,
            signOfCurrency = currencySign,
            inPortfolio = inPortfolio,
            dayInterval = dayInterval,
            weekInterval = weekInterval,
            monthInterval = monthInterval,
            yearInterval = yearInterval,
            fullInterval = fullInterval,
        )
    }

    fun searchCurrency(searchString: String): List<CurrencyDto>{
        val currencies: List<Currency> = currencyDao!!.findByIdCurrencyContainingIgnoreCase(searchString)

        return currencies.map {
            CurrencyDto(
                it.idCurrency!!,
                it.name_currency!!,
                it.sign_currency!!,
                it.id_on_yahoo_api!!
            )
        }
    }
}
