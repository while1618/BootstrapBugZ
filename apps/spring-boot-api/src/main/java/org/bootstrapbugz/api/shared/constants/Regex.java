package org.bootstrapbugz.api.shared.constants;

/*
 * Check for regex on: https://regexr.com/
 * */
public class Regex {
  /*
   * At least 2 characters
   * It supports only words with exception of thees characters: ^-`' ",.
   * It supports most languages and their special characters like: šđžčć
   *
   * eg.
   * Dejan Zdravković
   * Дејан Здравковић
   * 德揚·茲德拉夫科維奇
   * ñÑâê都道府県Федерации
   * আবাসযোগ্য জমির걸쳐 있는
   *
   * */
  public static final String FIRST_AND_LAST_NAME =
      "^[^0-9_!¡?÷?¿/\\\\+=@#$%ˆ&*(){}\\[\\]|~<>;:]{2,}$";
  /*
   * Between 2 and 16 characters long
   * It supports only letters, numbers, dot and underscore
   * Username can't start or end with dot or underscore
   * Username can't have two underscores or dots in a row
   * Username can't have underscore and dot in a row
   *
   * eg.
   * test
   * test_test123
   * test.test123
   * test_test.test123
   * test123
   *
   * */
  public static final String USERNAME = "^(?=[a-zA-Z0-9._]{2,16}$)(?!.*[_.]{2})[^_.].*[^_.]$";
  /*
   * At least one letter
   * At least one number
   * It can have a special characters
   * It can have a uppercase letter
   * It must be minimum 8 characters long
   *
   * eg.
   * qwerty321
   * BlaBla123
   * blaBLA23"#
   *
   * */
  public static final String PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[a-zA-Z]).{8,}$";
  /*
   * It supports any string
   * */
  public static final String ANYTHING = "[\\s\\S]+";

  private Regex() {}
}
