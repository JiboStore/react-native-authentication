import {
  AppRegistry,
  StyleSheet,
  Alert,
  Button,
  Text,
  TextInput,
  View
} from 'react-native';

let Httputil = {
  justRequest: (url) => {
    fetch(url).then((response) => {
      console.log(response);
//       Alert.alert(response);
    }).catch((error) => {
      console.log(error);
//       Alert.alert(error);
    }).done();
  },
  
  fetchGet: (url, params, successCallback, errorCallback) => {
    fetch(url).then(
      (response) => {
        console.log(response);
        return response.json();
      }
    ).then(
      (responseObj) => {
        console.log(responseObj);
        successCallback(responseObj);
      }
    ).catch(
      (error) => {
        console.log(error);
        errorCallback(error);
      }
    ).done();
  },
  
  fetchPost: (url, params, successCallback, errorCallback) => {
    // todo: fetch method post
  }
}

export default Httputil;