package ru.nuykin.involio.util.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors


@Service
class JWTUtil {
    @Value("\${jwt.secret}")
    private val SECRET_KEY: String? = null

    @Value("\${jwt.sessionTime}")
    private val sessionTime: Long = 0

    // генерация токена (кладем в него имя пользователя и authorities)
    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, Any> = HashMap()
        val commaSeparatedListOfAuthorities =
            userDetails.authorities.stream().map { a: GrantedAuthority? -> a!!.authority }
                .collect(Collectors.joining(","))
        claims["authorities"] = commaSeparatedListOfAuthorities
        return createToken(claims, userDetails.username)
    }

    //извлечение имени пользователя из токена (внутри валидация токена)
    fun extractUsername(token: String?): String? {
        return extractClaim(token!!) { obj: Claims -> obj.subject }
    }

    //извлечение authorities (внутри валидация токена)
    fun extractAuthorities(token: String): String {
        val claimsListFunction: Function<Claims, String> =
            Function<Claims, String> { claims -> claims.get("authorities") as String}
        return extractClaim(token, claimsListFunction)
    }

    private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder().setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(expireTimeFromNow())
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact()
    }

    private fun expireTimeFromNow(): Date {
        return Date(System.currentTimeMillis() + sessionTime)
    }
}