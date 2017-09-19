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

import SignBdayScreen from './signbday'

let fetchMyData = (fn, ln) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_REQUEST,
        firstname: fn,
        lastname: ln
    });
  }
}

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
            Alert.alert("hello: " + this.firstname + ", " + this.lastname);
            this.props.fetchMyData(this.firstname, this.lastname);
            this.props.navigation.navigate("SignBday");
//             this.props.navigation.navigate("SignBday", {
//               "name": { "first": this.firstname, "last": this.lastname}
//             })
        }}
      />
      </View>
    );
  }
}

// export default SignNameScreen;

export default SignNameScreen = connect(
  (state) => {
    const { firstname, lastname } = state.signupReducer;
    return {
      firstname,
      lastname
    }
  },
  {
    fetchMyData
  }
)(SignNameScreen);