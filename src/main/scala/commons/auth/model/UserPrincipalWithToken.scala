package com.melalex.realworld
package commons.auth.model

case class UserPrincipalWithToken(
    principal: ActualUserPrincipal,
    token: SecurityToken
)
