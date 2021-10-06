package com.melalex.realworld
package comments.route

import comments.dto.CreateCommentDto
import comments.mapper.CommentMapper
import comments.service.CommentService
import commons.auth.service.TokenService
import commons.web.RouteProvider

import akka.http.scaladsl.server.Route
import cats.data.EitherT
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class CommentRouteProvider(
    tokenService: TokenService,
    commentService: CommentService[Future],
    commentMapper: CommentMapper
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route = pathPrefix("articles" / slugMatcher / "comments") { slug =>
    pathEnd {
      authenticated(tokenService) { auth =>
        entity(as[CreateCommentDto]) { dto =>
          post {
            val payload = EitherT(commentService.createComment(dto.comment.body, auth.id, slug))
              .map(commentMapper.map)

            completeEitherT(payload)
          }
        }
      }
      maybeAuthenticated(tokenService) { implicit auth =>
        get {
          val payload = commentService
            .getAllByArticleSlug(slug)
            .map(commentMapper.map)

          complete(payload)
        }
      }
    }
    path(modelIdMatcher) { id =>
      authenticated(tokenService) { implicit auth =>
        delete {
          completeUnit(commentService.delete(id))
        }
      }
    }
  }
}
