/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {RadioGroup, RadioButton} from 'react-native-flexi-radio-button'
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
      signup_data: sud
    });
  }
}

class SignSexScreen extends Component {
  static navigationOptions = {
    title: 'Gender',
  };
  onRadioSelected = (index, value) => {
    const { signup_data } = this.props;
    let obj = signup_data;
    obj.sex = value;
    this.props.fetchSignupSex(obj);
  }
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <Text>Hello {this.props.signup_data.firstname}</Text>
        <TextInput placeholder="gender"
          onChangeText={(text) => this.sex = text}/>
        <RadioGroup onSelect={(index, value) => this.onRadioSelected(index, value)}>
          <RadioButton value={'Male'}>
            <Text>Male</Text>
          </RadioButton>
          <RadioButton value={'Female'}>
            <Text>Female</Text>
          </RadioButton>
        </RadioGroup>
      <Button 
        title="Next"
        onPress={() => {
//             Alert.alert("Name: " + this.props.signup_data.firstname + " Bday: " + this.props.signup_data.bday);
            const { signup_data } = this.props;
            Alert.alert("Name: " + signup_data.firstname + " Bday: " + signup_data.bday + " sex: " + signup_data.sex);
//             this.props.fetchSignupSex({firstname: signup_data.firstname, lastname: signup_data.lastname, bday: signup_data.bday, sex: this.sex});
            this.props.navigation.navigate("SignEmail");
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