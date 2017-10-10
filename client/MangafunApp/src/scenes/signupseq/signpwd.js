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

let fetchPwd = (signupdata) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_PWD,
      signupdata
    });
  }
}

class SignPwdScreen extends Component {
  static navigationOptions = {
    title: 'Password',
  };
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <TextInput placeholder="password"
          onChangeText={(text) => this.firstname = text}/>
        <TextInput placeholder="re-enter password"
          onChangeText={(text) => this.lastname = text}/>
      <Button
        title="Next"
        onPress={() => {
            const { signup_data } = this.props;
            Alert.alert("hello: " + signup_data.firstname + " your email: " + signup_data.email);
//             Alert.alert("hello: " + this.firstname + ", " + this.lastname);
//             let obj = {firstname: this.firstname, lastname: this.lastname};
//             this.props.fetchPwd(obj);
//             this.props.navigation.navigate("SignBday");
        }}
      />
      </View>
    );
  }
}

export default SignPwdScreen = connect(
  (state) => {
    const { signup_data } = state.signupReducer;
    return {
      signup_data
    }
  },
  {
    fetchPwd
  }
)(SignPwdScreen);
