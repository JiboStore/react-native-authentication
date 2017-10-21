/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  AsyncStorage,
  StyleSheet,
  Alert,
  Button,
  Text,
  TextInput,
  View
} from 'react-native';

import DeviceInfo from 'react-native-device-info';
// var DeviceInfo = require('react-native-device-info');

import DeviceInfoUtil from '../../utils/DeviceInfoUtil';

import HttpUtil from '../../utils/HttpUtil';

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
  SIGNUP_USER_PWD,
  STORAGE_KEY_SESSIONID
} from '../../constants/type';

import store from '../../store/store';

import SignBdayScreen from './signbday'

let fetchName = (signupdata) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_NAME,
      signup_data: {
        firstname: signupdata.firstname,
        lastname: signupdata.lastname
      }
    });
  }
}

// let fetchMyData = (fn, ln) => {
//   return (dispatch) => {
//     dispatch({
//       type: SIGNUP_USER_REQUEST,
//       signup_data: {
//         firstname: fn,
//         lastname: ln
//       }
//     });
//   }
// }

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
  componentDidMount = () => {
    // test react-native-device-info
    console.log("Device Unique ID", DeviceInfo.getUniqueID());  // e.g. FCDBD8EF-62FC-4ECB-B2F5-92C9E79AC7F9
    console.log("Device Manufacturer", DeviceInfo.getManufacturer());  // e.g. Apple
    console.log("Device Model", DeviceInfo.getModel());  // e.g. iPhone
    console.log("Device Name", DeviceInfo.getSystemName());  // e.g. iPhone OS
    console.log("Device Version", DeviceInfo.getSystemVersion());  // e.g. 9.0
    console.log("Bundle Id", DeviceInfo.getBundleId());  // e.g. com.learnium.mobile
    console.log("Build Number", DeviceInfo.getBuildNumber());  // e.g. 89
    console.log("App Version", DeviceInfo.getVersion());  // e.g. 1.1.0
    console.log("App Version (Readable)", DeviceInfo.getReadableVersion());  // e.g. 1.1.0.89
    
    AsyncStorage.getItem("name").then((szObj) => {
      if ( szObj != null ) {
        const obj = JSON.parse(szObj);
        console.log("asyncstorage name: " + JSON.stringify(obj));
        this.props.fetchName(obj);
      }
    })
    
    AsyncStorage.getItem(STORAGE_KEY_SESSIONID).then((szSessionId) => {
      if ( szSessionId != null ) {
        console.log(STORAGE_KEY_SESSIONID + " => " + szSessionId);
        let deviceId = DeviceInfoUtil.getDeviceInfo().deviceId;
        let deviceInfo = DeviceInfoUtil.getDeviceInfo();
        let szDeviceInfo = JSON.stringify(deviceInfo);
        let postParam = {
          sessionid: szSessionId,
          deviceid: deviceId,
          deviceinfo: szDeviceInfo
        };
        HttpUtil.fetchPost("http://localhost:3005/manga/api/signin/signinsession", postParam,
          (jsonData) => {
            console.log(JSON.stringify(jsonData));
          },
          (error) => {
            console.error(error);
          }
        )
      }
    }) // end asyncstorage
    
  }
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <TextInput placeholder="first name"
//           value={this.props.signup_data.firstname}
          onChangeText={(text) => this.firstname = text}/>
        <TextInput placeholder="last name"
//           value={this.props.signup_data.lastname}
          onChangeText={(text) => this.lastname = text}/>
      <Button
        title="Next"
//         onPress={this.handleNext} // this doesnt work, the function cannot access props
        onPress={() => {
            Alert.alert("hello: " + this.firstname + ", " + this.lastname);
            let obj = {firstname: this.firstname, lastname: this.lastname};
            let szObj = JSON.stringify(obj);
            AsyncStorage.setItem("name", szObj);
            this.props.fetchName(obj);
//             this.props.fetchMyData(this.firstname, this.lastname);
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
//     fetchMyData
    fetchName
  }
)(SignNameScreen);
