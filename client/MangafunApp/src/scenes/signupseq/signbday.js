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
  TouchableOpacity,
  View
} from 'react-native';

import { DatePickerDialog } from 'react-native-datepicker-dialog'

import moment from 'moment'

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

let fetchBday = (sud) => {
  return (dispatch) => {
    dispatch({
      type: SIGNUP_USER_BDAY,
      signup_data: sud
    });
  }
}

let getParam = {
  q: "hello"
}

// let onBdayPicked = (date) => {
//   this.bdayDate = date;
// }

class SignBdayScreen extends Component {
  static navigationOptions = {
    title: "Birthday",
  };
  onBdayPressed = () => {
    let bdayDate = this.bdayDate;
    if ( !bdayDate || bdayDate == null ) {
      bdayDate = new Date();
    }
    this.bdayDate = bdayDate;
    // open the dialog
    this.refs.bdayDialog.open({
      date: bdayDate,
      maxDate: new Date() // to restrict future date
    });
  }
  onBdayPicked = (date) => {
    this.bdayDate = date;
    this.bdayText = moment(date).format('DD-MMM-YYYY')
//     Alert.alert("bdayText: " + this.bdayText);
    const { signup_data } = this.props;
    let obj = signup_data;
    obj.bday = this.bdayText;
    this.props.fetchBday(obj);
  }
  render() {
    return (
      <View style={{flex: 1, flexDirection: 'column', justifyContent: 'space-around'}}>
        <Text>Hello {this.props.signup_data.firstname} {this.props.signup_data.lastname}</Text>
        <TouchableOpacity onPress={this.onBdayPressed.bind(this)} >
          <View style={styles.datePickerBox}>
              <Text style={styles.datePickerText}>Bday: {this.props.signup_data.bday}</Text>
          </View>
        </TouchableOpacity>
        <DatePickerDialog ref="bdayDialog" onDatePicked={this.onBdayPicked.bind(this)} />
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

const styles = StyleSheet.create({
  datePickerBox:{
    marginTop: 9,
    borderColor: '#ABABAB',
    borderWidth: 0.5,
    padding: 0,
    borderTopLeftRadius: 4,
    borderTopRightRadius: 4,
    borderBottomLeftRadius: 4,
    borderBottomRightRadius: 4,
    height: 38,
    justifyContent:'center'
  },
  datePickerText: {
    fontSize: 14,
    marginLeft: 5,
    borderWidth: 0,
    color: '#121212',
  },
});

export default SignBdayScreen = connect(
  (state) => {
    const { signup_data } = state.signupReducer;
    return {
      signup_data
    }
  },
  {
    fetchBday
  }
)(SignBdayScreen);