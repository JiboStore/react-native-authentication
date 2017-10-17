package com.mangafun.controllers

object ReturnCode extends Enumeration {
  val TEST_FETCHGET = Value("TEST_FETCHGET")
  val TEST_FETCHPOST = Value("TEST_FETCHPOST")
  val CREATE_USER = Value("CREATE_USER")
  val SIGNIN_USER = Value("SIGNIN_USER")
}

object ReturnResult extends Enumeration {
  val RESULT_ERROR = Value("-1")
  val RESULT_SUCCESS = Value("0")
  val RESULT_FAILED = Value("1")
}