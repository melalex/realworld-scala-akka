package com.melalex.realworld
package commons.i18n.service.impl

import test.spec.UnitTestSpec

import org.scalatest.prop.TableDrivenPropertyChecks

import java.util.Locale

class ResourceBundleI18nServiceSuite extends UnitTestSpec with TableDrivenPropertyChecks {

  private case class MessageBlueprint(key: String, args: String*)

  private implicit val locale: Locale = Locale.ENGLISH

  private val messageBlueprints = Table(
    ("blueprint", "expected"),
    (MessageBlueprint("model.User"), "User"),
    (MessageBlueprint("model.User.id"), "id"),
    (MessageBlueprint("model.User.email"), "email"),
    (MessageBlueprint("model.User.username"), "username"),
    (MessageBlueprint("model.User.password"), "password"),
    (MessageBlueprint("model.User.bio"), "bio"),
    (MessageBlueprint("model.User.image"), "image"),
    (MessageBlueprint("model.User.createdAt"), "created at"),
    (MessageBlueprint("model.User.updatedAt"), "updated at"),
    (MessageBlueprint("error.internal"), "Internal server error"),
    (MessageBlueprint("error.token.invalid"), "Provided toke is invalid"),
    (MessageBlueprint("error.user.unauthorized"), "User must be authorized to perform this action"),
    (MessageBlueprint("error.credentials.invalid"), "Provided credentials are invalid"),
    (MessageBlueprint("error.notFound", "model.User", "1"), "User with id 1 not found"),
    (MessageBlueprint("error.validation.empty", "model.User.username"), "Field username cannot be empty"),
    (MessageBlueprint("error.validation.min", "model.User.username", "4"), "Field username should be shorter than 4 characters"),
    (MessageBlueprint("error.validation.max", "model.User.username", "4"), "Field username should be longer than 4 characters"),
    (MessageBlueprint("error.validation.regex", "model.User.username", "^.$"), "Field username should match regex ^.$")
  )

  private val resourceBundleI18nService = new ResourceBundleI18nService()

  "ResourceBundleI18nService" should "provide translation for keys and arguments" in {
    forAll(messageBlueprints) { (blueprint, expected) =>
      resourceBundleI18nService.getMessage(blueprint.key, blueprint.args: _*) shouldEqual expected
    }
  }
}
