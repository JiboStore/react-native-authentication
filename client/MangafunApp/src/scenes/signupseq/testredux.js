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
  SIGNUP_USER_RESPONSE
} from '../../constants/type';

import store from '../../store/store';

import SignNameScreen from './signname'
import SignBdayScreen from './signbday'

const StackNav = StackNavigator({
  SignName: { screen: SignNameScreen},
  SignBday: { screen: SignBdayScreen}
})

export default StackNav;