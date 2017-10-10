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

import HttpUtil from '../../utils/HttpUtil';

// let fetchMyData = (fn, ln) => {
//   return (dispatch) => {
//     dispatch({
//       type: SIGNUP_USER_REQUEST,
//       signup_data: {
//         birthdate: fn,
//         birthmonth: ln
//       }
//     });
//   }
// }

let fetchBday = (signup_data) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_BDAY,
      signup_data: {
        firstname: signup_data.firstname,
        lastname: signup_data.lastname,
        bday: signup_data.bday
      }
    });
  }
}

let getParam = {
  q: "hello"
}

class SignBdayScreen extends Component {
  static navigationOptions = {
    title: "Birthday",
  };
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <Text>Hello {this.props.signup_data.firstname} {this.props.signup_data.lastname}</Text>
        <TextInput placeholder="bday"
          onChangeText={(text) => this.bday = text}/>
      <Button 
        title="Next"
        onPress={() => {
            Alert.alert("Hello: " + this.props.signup_data.firstname + this.props.signup_data.lastname + " : " + this.bday);
            const { firstname, lastname } = this.props.signup_data;
            this.props.fetchBday({firstname: firstname, lastname: lastname, bday: this.bday});
            this.props.navigation.navigate("SignSex");
          }}
      />
      </View>
    );
  }
}

class SignBdayScreen_Old extends Component {
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
            
//             HttpUtil.fetchGet("http://localhost:3005/manga/api/rnfetchget", getParam,
//               (jsonData) => {
//                 console.log(jsonData);
//               },
//               (error) => {
//                 console.log(error);
//               }
//             )
            HttpUtil.fetchPost("http://localhost:3005/manga/api/signin/createuser", getParam,
            //HttpUtil.fetchPost("http://localhost:3005/manga/api/rnfetchpost", getParam,
              (jsonData) => {
              var szMsg = jsonData.result.message;
              Alert.alert(szMsg);
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

// export default SignBdayScreen = connect(
//   (state) => {
// //     const { firstname, lastname } = state.signupReducer;
//     const { firstname, lastname } = state.signupReducer.signup_data;
//     return {
//       signup_data: {
//         firstname,
//         lastname
//       }
//     }
//   },
//   {
//     fetchMyData
//   }
// )(SignBdayScreen);

export default SignBdayScreen = connect(
  (state) => {
    const { firstname, lastname } = state.signupReducer.signup_data;
    return {
      signup_data: {
        firstname,
        lastname
      }
    }
  },
  {
    fetchBday
  }
)(SignBdayScreen);