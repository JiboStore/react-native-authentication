/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Alert,
  Button,
  Text,
  TextInput,
  View
} from 'react-native';

import { StackNavigator } from 'react-navigation';

import {
  Provider,
  connect
} from 'react-redux';

import {
  SIGNUP_USER_REQUEST,
  SIGNUP_USER_RESPONSE,
  SIGNUP_USER_NAME,
  SIGNUP_USER_BDAY,
  SIGNUP_USER_SEX,
  SIGNUP_USER_EMAIL,
  SIGNUP_USER_PWD
} from '../../constants/type';

import store from '../../store/store';

import SignNameScreen from './signname'
import SignBdayScreen from './signbday'
import SignSexScreen from './signsex'
import SignEmailScreen from './signemail'
import SignPwdScreen from './signpwd'

const StackNav = StackNavigator({
  SignName: { screen: SignNameScreen},
  SignBday: { screen: SignBdayScreen},
  SignSex: { screen: SignSexScreen},
  SignEmail: { screen: SignEmailScreen},
  SignPwd: { screen: SignPwdScreen}
})

export default StackNav;
