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

import { 
  Provider,
  connect
} from 'react-redux';

import {
  SIGNUP_USER_REQUEST,
  SIGNUP_USER_RESPONSE
} from '../../constants/type';

let fetchMyData = (fn, ln) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_REQUEST,
      signup_data: {
        birthdate: fn,
        birthmonth: ln
      }
    });
  }
}

class SignBdayScreen extends Component {
  static navigationOptions = {
    title: 'Birthday',
  };
  handleNext() {
    Alert.alert("handle button next pressed")
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
        <Text>Hello {this.props.signup_data.firstname}</Text>
        <TextInput placeholder="first name"
          onChangeText={(text) => this.firstname = text}/>
        <TextInput placeholder="last name"
          onChangeText={(text) => this.lastname = text}/>
      <Button 
        title="Next"
        onPress={this.handleNext}
      />
      </View>
    );
  }
}

// export default SignBdayScreen;

export default SignBdayScreen = connect(
  (state) => {
//     const { firstname, lastname } = state.signupReducer;
    const { firstname, lastname } = state.signupReducer.signup_data;
    return {
      signup_data: {
        firstname,
        lastname
      }
    }
  },
  {
    fetchMyData
  }
)(SignBdayScreen);