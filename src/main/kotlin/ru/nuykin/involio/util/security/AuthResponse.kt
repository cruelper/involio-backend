package ru.nuykin.involio.util.security

class AuthResponse {
    var jwtToken: String? = null

    constructor() {}
    constructor(jwtToken: String?) {
        this.jwtToken = jwtToken
    }
}