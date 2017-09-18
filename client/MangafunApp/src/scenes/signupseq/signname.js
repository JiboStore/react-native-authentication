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

import SignBdayScreen from './signbday'

class SignNameScreen extends Component {
  static navigationOptions = {
    title: 'Welcome',
  };
  handleNext() {
    Alert.alert("handle button next pressed")
//     navigate("SignBday")
  }
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
        <TextInput placeholder="first name"
          onChangeText={(text) => this.firstname = text}/>
        <TextInput placeholder="last name"
          onChangeText={(text) => this.lastname = text}/>
      <Button 
        title="Next"
//         onPress={this.handleNext} // this doesnt work, the function cannot access props
        onPress={() => {
            this.props.navigation.navigate("SignBday", {
              "name": { "first": this.firstname, "last": this.lastname}
            })
        }}
      />
      </View>
    );
  }
}

export default SignNameScreen;