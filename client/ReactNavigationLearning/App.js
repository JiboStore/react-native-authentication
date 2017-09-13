import React from 'react';
import {
  AppRegistry,
  Text,
} from 'react-native';
import { StackNavigator } from 'react-navigation';

class HomeScreen extends React.Component {
  static navigationOptions = {
    title: 'Welcome',
  };
  render() {
    return <Text>Hello, Navigation!</Text>;
  }
}

const ReactNavigationLearning = StackNavigator({
  Home: { screen: HomeScreen },
});

AppRegistry.registerComponent('ReactNavigationLearning', () => ReactNavigationLearning);