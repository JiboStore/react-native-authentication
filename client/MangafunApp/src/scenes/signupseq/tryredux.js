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

import { connect } from 'react-redux';

import SignNameScreen from './signname'
import SignBdayScreen from './signbday'

const StackNav = StackNavigator({
  SignName: { screen: SignNameScreen},
  SignBday: { screen: SignBdayScreen}
})

//export default StackNav;

export default TryRedux = connect(
)(TryRedux);