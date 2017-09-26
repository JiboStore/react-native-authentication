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
    if (params) {
      var paramsBody = Object.keys(params)
          .reduce((a, k) => {
            a.push(k + "=" + encodeURIComponent(params[k]));
            return a;
          }, [])
          .join('&');
      url += "&" + paramsBody;
    }
    console.info("url:"+url);
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
    var paramsBody = Object.keys(params)
    .reduce((a, k) => {
        a.push(k + "=" + encodeURIComponent(params[k]));
        return a;
    }, [])
    .join('&');
    fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
//         body: paramsBody      
      }
    ).then(
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
  }
}

export default Httputil;