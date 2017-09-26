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

import HttpUtil from '../../utils/HttpUtil';

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

let getParam = {
  q: "hello"
}

class SignBdayScreen extends Component {
  static navigationOptions = {
    title: 'Birthday',
  };
  handleNext() {
    Alert.alert("handle button next pressed")
//     HttpUtil.justRequest("http://www.google.com/");
//     HttpUtil.fetchGet("http://www.google.com/search", getParam, 
//                      (jsonData) => {
//       console.log(jsonData);
//       Alert.alert(jsonData);
//     },
//                      (error) => {
//       console.log(error);
//       Alert.alert(error);
//     });
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
//         onPress={this.handleNext}
        onPress={() => {
//             this.handleNext();
// //             HttpUtil.justRequest("https://www.google.com/");
//                 HttpUtil.fetchGet("https://www.google.com/search", getParam, 
//                      (jsonData) => {
//                         console.log(jsonData);
// //                         Alert.alert(jsonData);
//                       },
//                                        (error) => {
//                         console.log(error);
// //                         Alert.alert(error);
//                       });
            
            HttpUtil.fetchGet("http://localhost:3005/manga/api/rnfetchget", getParam,
              (jsonData) => {
                console.log(jsonData);
              },
              (error) => {
                console.log(error);
              }
            )
            
//             HttpUtil.fetchPost("https://www.google.com/search", getParam,
//               (jsonData) => {
//                 console.log(jsonData);
//                 //Alert.alert(jsonData);
//               },
//               (error) => {
//                 console.log(error);
//                 //Alert.alert(error);
//               }
//             )
            
          }}
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