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
  View
} from 'react-native';

class LogOrSign extends Component {
  handleOnPress() {
    Alert.alert("handle button login pressed")
  }
  render() {
    return (
      <Button 
        title="Login"
        onPress={this.handleOnPress}
//         onPress={()=>{
//           this.handleOnPress()
// //           Alert.alert("button login pressed")
//         }}
      />
    );
  }
}

export default LogOrSign;