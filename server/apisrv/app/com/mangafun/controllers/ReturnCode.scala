package com.mangafun.controllers

object ReturnCode extends Enumeration {
  val TEST_FETCHGET = Value("TEST_FETCHGET")
  val TEST_FETCHPOST = Value("TEST_FETCHPOST")
  val CREATE_USER = Value("CREATE_USER")
  val SIGNIN_USER = Value("SIGNIN_USER")
}