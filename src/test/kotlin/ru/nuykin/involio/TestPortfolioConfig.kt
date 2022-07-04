package ru.nuykin.involio

import org.mockito.Mockito.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nuykin.involio.repository.*
import ru.nuykin.involio.service.UserService

@Configuration
class TestPortfolioConfig {
//    @Bean
//    public fun investmentPortfolioService(): InvestmentPortfolioService{
//        return InvestmentPortfolioService()
//    }
//
//    @Bean
//    public fun investmentPortfolioRepository(): InvestmentPortfolioRepository{
//        return mock(InvestmentPortfolioRepository::class.java)
//    }
//
//    @Bean
//    public fun myUserRepository(): MyUserRepository{
//        return mock(MyUserRepository::class.java)
//    }
//
//    @Bean
//    public fun typeOfBrokerAccountRepository(): TypeOfBrokerAccountRepository{
//        return mock(TypeOfBrokerAccountRepository::class.java)
//    }
//
//    @Bean fun brokerRepository(): BrokerRepository{
//        return mock(BrokerRepository::class.java)
//    }
//
//    @Bean fun JWTUtil(): JWTUtil{
//        return mock(JWTUtil::class.java)
//    }

    @Bean
    public fun userService(): UserService{
        return UserService()
    }

    @Bean
    public fun userRepository(): MyUserRepository{
        return mock(MyUserRepository::class.java)
    }
}