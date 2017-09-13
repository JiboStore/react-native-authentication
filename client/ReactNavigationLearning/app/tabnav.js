import React from 'react';
import {
  AppRegistry,
  Button,
  Text,
  View,
} from 'react-native';
import { 
  StackNavigator,
  TabNavigator,
} from 'react-navigation';

class RecentChatsScreen extends React.Component {
  render() {
    return <Text>List of recent chats</Text>
  }
}

class AllContactsScreen extends React.Component {
  render() {
    return <Text>List of all contacts</Text>
  }
}

const MainScreenNavigator = TabNavigator({
  Recent: { screen: RecentChatsScreen },
  All: { screen: AllContactsScreen },
});

export default MainScreenNavigator;