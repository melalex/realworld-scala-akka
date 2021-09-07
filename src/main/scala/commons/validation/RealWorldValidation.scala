package com.melalex.realworld
package commons.validation

import commons.errors.model.RealWorldError
import commons.errors.model.RealWorldError.{AboveMax, BelowMin, EmptyField, RegExMismatch}
import commons.validation.RealWorldValidation.{AlphaNumericRegEx, EmailRegEx}

import cats.data._
import cats.implicits._

import scala.util.matching.Regex

trait RealWorldValidation {

  type ValidationResult[A] = ValidatedNec[RealWorldError, A]
  type FieldValidation[F]  = (String, F) => Option[RealWorldError]
  type FormValidation[F]   = F => ValidationResult[F]

  trait Required[F] extends (F => Boolean)
  trait Min[F]      extends ((F, Int) => Boolean)
  trait Max[F]      extends ((F, Int) => Boolean)
  trait RegEx[F]    extends ((F, Regex) => Boolean)

  implicit val requiredString: Required[String]  = _.nonEmpty
  implicit val minimumStringLength: Min[String]  = _.length >= _
  implicit val maximumStringLength: Max[String]  = _.length <= _
  implicit val maximumOptionStringLength: Max[Option[String]]  = (opt, limit) => opt.forall(_.length <= limit)
  implicit val stringMatchesRegEx: RegEx[String] = (str, regex) => (regex findFirstIn str).isDefined

  def required[F](implicit req: Required[F]): FieldValidation[F] = (fieldName, field) => fieldValidation(req(field))(EmptyField(fieldName))

  def min[F](limit: Int)(implicit minimum: Min[F]): FieldValidation[F] =
    (fieldName, field) => fieldValidation(minimum(field, limit))(BelowMin(fieldName, limit))

  def max[F](limit: Int)(implicit maximum: Max[F]): FieldValidation[F] =
    (fieldName, field) => fieldValidation(maximum(field, limit))(AboveMax(fieldName, limit))

  def regEx[F](regex: Regex)(implicit reg: RegEx[F]): FieldValidation[F] =
    (fieldName, field) => fieldValidation(reg(field, regex))(RegExMismatch(fieldName, regex))

  def email: FieldValidation[String] = regEx(EmailRegEx)

  def alphanumeric: FieldValidation[String] = regEx(AlphaNumericRegEx)

  def validateForm[F, A](form: F)(f: ValidationResult[F] => A)(implicit formValidation: FormValidation[F]): A = f(formValidation(form))

  def validator[F](fieldName: String, first: FieldValidation[F], rest: FieldValidation[F]*)(field: F): ValidationResult[F] =
    validate(fieldName, field, first :: rest.toList)

  private def validate[F](fieldName: String, field: F, validations: List[FieldValidation[F]]): ValidationResult[F] = {
    validations.flatMap(_(fieldName, field)) match {
      case head :: tail => NonEmptyChain(head, tail: _*).invalid
      case Nil          => field.valid
    }
  }

  private def fieldValidation[F](isValid: Boolean)(error: => RealWorldError): Option[RealWorldError] = Option.when(!isValid)(error)
}

object RealWorldValidation extends RealWorldValidation {

  private val EmailRegEx =
    """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  private val AlphaNumericRegEx = """^[a-zA-Z0-9_]*$""".r
}
