package com.melalex.realworld
package users.model

import commons.auth.model.SecurityToken

case class UserWithToken(
    user: SavedUser,
    token: SecurityToken
)
