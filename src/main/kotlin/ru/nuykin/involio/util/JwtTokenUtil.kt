//package ru.nuykin.involio.util
//
//import io.jsonwebtoken.Jwts
//import io.jsonwebtoken.SignatureAlgorithm
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.authority.AuthorityUtils
//import java.util.*
//import java.util.stream.Collectors
//
//
//fun getJWTToken(username: String): String? {
//    val secretKey = "mySecretKey"
//    val grantedAuthorities = AuthorityUtils
//        .commaSeparatedStringToAuthorityList("ROLE_USER")
//    val token = Jwts
//        .builder()
//        .setId("softtekJWT")
//        .setSubject(username)
//        .claim("authorities",
//            grantedAuthorities.stream()
//                .map { obj: GrantedAuthority -> obj.authority }
//                .collect(Collectors.toList()))
//        .setIssuedAt(Date(System.currentTimeMillis()))
//        .setExpiration(Date(System.currentTimeMillis() + 600000))
//        .signWith(
//            SignatureAlgorithm.HS512,
//            secretKey.toByteArray()
//        ).compact()
//    return "Bearer $token"
//}