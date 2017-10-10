/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View
} from 'react-native';

import {
  Provider,
  connect
} from 'react-redux';

import store from './src/store/store';

import LogOrSign from './src/scenes/logorsign/logorsign'
// import SignUpSeq from './src/scenes/signupseq/index'
import SignUpSeq from './src/scenes/signupseq/index'

export default class MangafunApp extends Component {
  render() {
    let RootView = SignUpSeq;
    return (
      <Provider store={store}>
        <View style={{flex:1}}>
          <RootView/>
        </View>
      </Provider>
    )
  }
}

// AppRegistry.registerComponent('MangafunApp', () => MangafunApp);
// AppRegistry.registerComponent('MangafunApp', () => LogOrSign);
// AppRegistry.registerComponent('MangafunApp', () => SignUpSeq);
AppRegistry.registerComponent('MangafunApp', () => MangafunApp);
