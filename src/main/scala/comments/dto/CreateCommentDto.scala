package com.melalex.realworld
package comments.dto

case class CreateCommentDto(
    comment: CreateCommentDto.Body
)

object CreateCommentDto {

  case class Body(
      body: String
  )
}
