package org.bootstrapbugz.api.shared.constants

/*
 * Check for regex on: https://regexr.com/
 * */
object Regex {
    /*
   * At least 2 characters
   * It supports only words with exception to thees characters: ^-`' ",.
   * It supports most languages and their special characters like: šđžčć
   *
   * e.g.
   * Dejan Zdravković
   * Дејан Здравковић
   * 德揚·茲德拉夫科維奇
   * ñÑâê都道府県Федерации
   * আবাসযোগ্য জমির걸쳐 있는
   *
   * */
    const val FIRST_AND_LAST_NAME = "^[^0-9_!¡?÷¿/\\\\+=@#$%ˆ&*(){}\\[\\]|~<>;:]{2,}$"

    /*
   * Between 2 and 16 characters long
   * It supports only letters, numbers, dot and underscore
   * Username can't start or end with dot or underscore
   * Username can't have two underscores or dots in a row
   * Username can't have underscore(_) and dot(.) in a row
   *
   * e.g.
   * test
   * test_test123
   * test.test123
   * test_test.test123
   * test123
   *
   * */
    const val USERNAME = "^(?=[a-zA-Z0-9._]{2,16}$)(?!.*[_.]{2})[^_.].*[^_.]$"

    /*
   * Email Regex base on this article:
   * https://www.baeldung.com/java-email-validation-regex#regular-expression-by-rfc-5322-for-email-validation
   * */
    const val EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"

    /*
   * At least one letter
   * At least one number
   * It can have a special characters
   * It can have an uppercase letter
   * It must be minimum 8 characters long
   *
   * e.g.
   * qwerty321
   * BlaBla123
   * blaBLA23"#
   *
   * */
    const val PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[a-zA-Z]).{8,}$"
}
