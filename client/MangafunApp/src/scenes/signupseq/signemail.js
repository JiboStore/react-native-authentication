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

import SignBdayScreen from './signbday'

let fetchEmail = (signupdata) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_EMAIL,
      signup_data: signupdata
    });
  }
}

class SignEmailScreen extends Component {
  static navigationOptions = {
    title: 'Email screen',
  };
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <TextInput placeholder="email"
          onChangeText={(text) => this.email = text}/>
      <Button
        title="Next"
        onPress={() => {
//             Alert.alert("hello: " + this.email);
            const { signup_data } = this.props;
//             let obj = {email: this.email};
            Alert.alert("hello: " + signup_data.firstname + ": " + signup_data.bday);
            let obj = signup_data;
            obj.email = this.email;
            this.props.fetchEmail(obj);
            this.props.navigation.navigate("SignPwd");
        }}
      />
      </View>
    );
  }
}

export default SignEmailScreen = connect(
  (state) => {
//     const { firstname, lastname } = state.signupReducer;
    const { signup_data } = state.signupReducer;
    return {
      signup_data
    }
  },
  {
    fetchEmail
  }
)(SignEmailScreen);
