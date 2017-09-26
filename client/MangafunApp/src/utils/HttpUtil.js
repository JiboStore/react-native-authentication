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
      url += "?" + paramsBody;
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
    var strParams = JSON.stringify(params);
    console.log('params: ' + strParams)
    console.log('paramsBody: ' + paramsBody)
    fetch(url, {
        method: 'POST',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: strParams      
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