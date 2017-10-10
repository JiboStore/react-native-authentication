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
  SIGNUP_USER_RESPONSE,
  SIGNUP_USER_NAME,
  SIGNUP_USER_BDAY,
  SIGNUP_USER_SEX,
  SIGNUP_USER_EMAIL,
  SIGNUP_USER_PWD
} from '../../constants/type';

let fetchSignupSex = (sud) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_SEX,
      signup_data: {
        sex: sud.sex
      }
    });
  }
}

class SignSexScreen extends Component {
  static navigationOptions = {
    title: 'Gender',
  };
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <Text>Hello {this.props.signup_data.firstname}</Text>
        <TextInput placeholder="gender"
          onChangeText={(text) => this.sex = text}/>
      <Button 
        title="Next"
        onPress={() => {
            Alert.alert(this.props.signup_data.firstname + " " + this.props.signup_data.bday);
            this.props.fetchSignupSex({sex: this.sex});
        }}
      />
      </View>
    );
  }
}

// export default SignBdayScreen;

export default SignSexScreen = connect(
  (state) => {
    const { signup_data } = state.signupReducer;
    return {
      signup_data
    }
  },
  {
    fetchSignupSex
  }
)(SignSexScreen);