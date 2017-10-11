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

let validatePwd = (pwd1,pwd2) => {
  return pwd1 === pwd2
}

class SignPwdScreen extends Component {
  static navigationOptions = {
    title: 'Password',
  };
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <Text>Hello {this.props.signup_data.firstname} {this.props.signup_data.lastname}</Text>
        <Text>Bday {this.props.signup_data.bday} Gender: {this.props.signup_data.sex}</Text>
        <Text>Email {this.props.signup_data.email} Pwd: {this.pwd}</Text>
        <TextInput placeholder="new password"
          secureTextEntry={true}
          onChangeText={(text) => this.pwd = text}/>
        <TextInput placeholder="re-enter new password"
          secureTextEntry={true}
          onChangeText={(text) => this.repwd = text}/>
      <Button
        title="Next"
        onPress={() => {
            const { signup_data } = this.props;
            if ( validatePwd(this.pwd, this.repwd) ) {
              Alert.alert("password match");
            } else {
              Alert.alert("password mismatch");
            }
//             Alert.alert("hello: " + signup_data.firstname + " your email: " + signup_data.email);
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
