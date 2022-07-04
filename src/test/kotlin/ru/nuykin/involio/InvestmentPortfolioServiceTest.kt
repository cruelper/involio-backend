package ru.nuykin.involio

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.support.AnnotationConfigContextLoader
import ru.nuykin.involio.model.Broker
import ru.nuykin.involio.model.InvestmentPortfolio
import ru.nuykin.involio.model.MyUser
import ru.nuykin.involio.model.TypeOfBrokerAccount
import ru.nuykin.involio.repository.BrokerRepository
import ru.nuykin.involio.repository.InvestmentPortfolioRepository
import ru.nuykin.involio.repository.MyUserRepository
import ru.nuykin.involio.repository.TypeOfBrokerAccountRepository
import ru.nuykin.involio.service.InvestmentPortfolioService
import ru.nuykin.involio.util.security.JWTUtil
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ContextConfiguration(classes = [TestPortfolioConfig::class], loader = AnnotationConfigContextLoader::class)
class InvestmentPortfolioServiceTest {
    @Autowired
    private val investmentPortfolioService: InvestmentPortfolioService? = null
    @Autowired
    private val investmentPortfolioRepository: InvestmentPortfolioRepository? = null
    @MockBean
    private val myUserRepository: MyUserRepository? = null
    @MockBean
    private val typeOfBrokerAccountRepository: TypeOfBrokerAccountRepository? = null
    @MockBean
    private val brokerRepository: BrokerRepository? = null
    @MockBean
    private val portfolioDao: InvestmentPortfolioRepository? = null
    @MockBean
    private val JWTUtil: JWTUtil? = null

    private val token: String = ""
    private val user: MyUser = mock(MyUser::class.java)
    private val user_: MyUser = mock(MyUser::class.java)
    private val listPortfolio = listOf(
        InvestmentPortfolio(user, "", mock(TypeOfBrokerAccount::class.java), Date(0), mock(Broker::class.java)),
        InvestmentPortfolio(user, "", mock(TypeOfBrokerAccount::class.java), Date(0), mock(Broker::class.java))
    )

    @Test
    public fun getListPortfolioTest(){
        Mockito.`when`(JWTUtil!!.extractUsername(token)).thenReturn(token)
        Mockito.`when`(myUserRepository!!.findByLogin(token)).thenReturn(user)
        Mockito.`when`(investmentPortfolioRepository!!.findAllByOwner(user)).thenReturn(listPortfolio)
        var listPortfolioDto = investmentPortfolioService!!.getListPortfolio(token)

//        listPortfolioDto = listPortfolioDto + listPortfolioDto[0]

        assertEquals(
            listPortfolioDto.size, listPortfolio.size,
            "Количество портфелей не совпадает с реальным"
        )

//        listPortfolioDto[0].name = "1111111111111"

        listPortfolioDto.forEach{
            val realPortfolio = listPortfolio.find { p -> p.id_investment_portfolio == it.id }
            assert(
                realPortfolio != null,
                { "В результате появился лишний портфель с id=${it.id} или/и пропал реальный с id=${realPortfolio!!.id_investment_portfolio}" }
            )
            if (realPortfolio != null){
                assertEquals(
                    realPortfolio.name_portfolio, it.name,
                    "Название портфеля с id=${it.id} отличается от реального"
                )
                assertEquals(
                    realPortfolio.date_of_creation, it.dataOfCreation,
                    "Дата создания портфеля с id=${it.id} отличается от реального"
                )
                assertEquals(
                    realPortfolio.broker_of_portfolio!!.id_broker, it.idBroker,
                    "Брокер портфеля с id=${it.id} отличается от реального"
                )
                assertEquals(
                    realPortfolio.type_broker_account!!.id_type_of_broker_account, it.idTypeBrokerAccount,
                    "Брокер портфеля с id=${it.id} отличается от реального"
                )
            }
        }
    }

    @Test
    public fun deletePortfolioTest_shouldAccept_whenPortfolioShouldDelete(){
        var portfolio: InvestmentPortfolio = listPortfolio[0]
        val idPortfolio = listPortfolio[0].id_investment_portfolio
        Mockito.`when`(JWTUtil!!.extractUsername(token)).thenReturn(token)
        Mockito.`when`(myUserRepository!!.findByLogin(token)).thenReturn(user)
        Mockito.`when`(investmentPortfolioRepository!!.findById(idPortfolio)).thenReturn(Optional.of(portfolio))

        var httpstatus = investmentPortfolioService!!.deletePortfolio(idPortfolio, token)

        assertEquals(
            HttpStatus.OK, httpstatus,
            "Удаление не произошло, хотя должно было было закончиться успехом"
        )
    }

    @Test
    public fun deletePortfolioTest_shouldAccept_whenPortfolioNotShouldDelete(){
        var portfolio: InvestmentPortfolio = listPortfolio[0]
        val idPortfolio = listPortfolio[0].id_investment_portfolio
        Mockito.`when`(JWTUtil!!.extractUsername(token)).thenReturn(token)
        Mockito.`when`(myUserRepository!!.findByLogin(token)).thenReturn(user_)
        Mockito.`when`(investmentPortfolioRepository!!.findById(idPortfolio)).thenReturn(Optional.of(portfolio))

        var httpstatus = investmentPortfolioService!!.deletePortfolio(idPortfolio, token)

        assertEquals(
            HttpStatus.NOT_ACCEPTABLE, httpstatus,
            "Удаление произошло, хотя этого быть не должно "
        )
    }

//    @Test
//    public fun createPortfolioTest(){
//        var portfolio: InvestmentPortfolio = listPortfolio[0]
//        var portfolioDto = PortfolioDto(
//            portfolio.id_investment_portfolio,
//            portfolio.name_portfolio!!,
//            portfolio.broker_of_portfolio!!.id_broker,
//
//        )
//
//        Mockito.`when`(JWTUtil!!.extractUsername(token)).thenReturn(token)
//        Mockito.`when`(myUserRepository!!.findByLogin(token)).thenReturn(user)
//
//        val response = investmentPortfolioService!!.createPortfolio(portfolio, token)
//    }
}