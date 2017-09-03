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

class LogOrSign extends Component {
  handleOnPress() {
    Alert.alert("handle button login pressed")
  }
  submitFunc = () => {
    const payload = {
      email: this.email,
      password: this.password
    }
    console.log(payload)
    Alert.alert("hello: " + payload.email)
  }
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <TextInput placeholder="email"
          onChangeText={(text) => this.email = text}/>
        <TextInput placeholder="password"
          onChangeText={(text) => this.password = text}/>
      <Button 
        title="Login"
        onPress={this.handleOnPress}
//         onPress={()=>{
//           this.handleOnPress()
// //           Alert.alert("button login pressed")
//         }}
      />
      <Button title="Sign up"
        onPress={this.submitFunc}
      />
      </View>
    );
  }
}

export default LogOrSign;