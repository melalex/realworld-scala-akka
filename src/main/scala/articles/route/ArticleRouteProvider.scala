package com.melalex.realworld
package articles.route

import articles.dto.CreateArticleParamsDto
import articles.mapper.ArticleMapper
import articles.model.ArticleFilter
import articles.service.ArticleService
import commons.auth.service.TokenService
import commons.model.Pageable
import commons.web.RouteProvider

import akka.http.scaladsl.server.Route
import cats.data.EitherT
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

class ArticleRouteProvider(
    tokenService: TokenService,
    articleService: ArticleService[Future],
    articleMapper: ArticleMapper
)(implicit ec: ExecutionContext)
    extends RouteProvider {

  override def provideRoute: Route = pathPrefix("articles") {
    pathEnd {
      maybeAuthenticated(tokenService) { implicit auth =>
        get {
          parameters(
            "tag".optional,
            "author".optional,
            "favorited".as[Boolean].withDefault(false),
            "limit".as[Long].withDefault(20L),
            "offset".as[Long].withDefault(0L)
          ).as(ArticleFilter.apply _) { filter =>
            val result = articleService
              .getAll(filter)
              .map(articleMapper.map)

            complete(result)
          }
        }
      }
      authenticated(tokenService) { implicit auth =>
        post {
          entity(as[CreateArticleParamsDto]) { dto =>
            val result = articleService
              .createArticle(articleMapper.map(dto))
              .map(articleMapper.map)

            complete(result)
          }
        }
      }
    }
    path("feed") {
      authenticated(tokenService) { implicit auth =>
        get {
          parameters(
            "limit".as[Long].withDefault(20L),
            "offset".as[Long].withDefault(0L)
          ).as(Pageable.apply _) { pageable =>
            val result = articleService
              .getFeed(pageable)
              .map(articleMapper.map)

            complete(result)
          }
        }
      }
    }
    pathPrefix(slugMatcher) { slug =>
      maybeAuthenticated(tokenService) { implicit auth =>
        pathEnd {
          get {
            val result = EitherT(articleService.getBySlug(slug))
              .map(articleMapper.map)

            completeEitherT(result)
          }
        }
      }
      authenticated(tokenService) { implicit auth =>
        pathEnd {
          put {
            val result = EitherT(articleService.getBySlug(slug))
              .map(articleMapper.map)

            completeEitherT(result)
          }
          delete {
            completeUnit(articleService.delete(slug))
          }
        }
        path("favorite") {
          post {
            completeUnitEitherT(EitherT(articleService.markFavorite(slug)))
          }
          delete {
            completeUnitEitherT(EitherT(articleService.unmarkFavorite(slug)))
          }
        }
      }
    }
  }
}
