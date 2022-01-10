package ru.nuykin.involio.util.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.nuykin.involio.service.CustomUserDetailsService
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JWTFilter : OncePerRequestFilter() {
    @Autowired
    private val jwtUtil: JWTUtil? = null

    @Autowired
    var userDetailsService: CustomUserDetailsService? = null

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader("Authorization")
        var username: String? = null
        var jwt: String? = null
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7)
            //если подпись не совпадает с вычисленной, то SignatureException
            //если подпись некорректная (не парсится) то MalformedJwtException
            //если подпись истекла по времени,  то ExpiredJwtException
            username = jwtUtil?.extractUsername(jwt)
        }
        if (username != null && SecurityContextHolder.getContext().authentication == null && jwt != null) {
            val commaSeparatedListOfAuthorities: String = jwtUtil!!.extractAuthorities(jwt)
            val authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedListOfAuthorities)
            val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(
                username, null, authorities
            )
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
        chain.doFilter(request, response)
    }
}