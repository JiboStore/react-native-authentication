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

import TabNav from './app/tabnav'
/*
class HomeScreen extends React.Component {
  static navigationOptions = {
    title: 'Welcome',
  };
  render() {
    const { navigate } = this.props.navigation;
    return (
      <View>
        <Text>Hello, Chat App!</Text>
        <Button
          onPress={() => navigate('Chat', { user: 'Lucy' })}
          title="Chat with Lucy"
        />
      </View>
    );
  }
}
*/
class ChatScreen extends React.Component {
  static navigationOptions = ({ navigation }) => {
    const { state, setParams } = navigation;
    const isInfo = state.params.mode === 'info';
    const { user } = state.params;
    return {
      title: isInfo ? `${user}'s Contact info` : `Chat with ${state.params.user}`,
      headerRight: (
          <Button
            title={ isInfo ? `Done`: `${user}'s info`}
            onPress={()=>{
              setParams({mode: isInfo ? `none`: `info`});
            }}
          />
      )
    };
  };
  render() {
    // The screen's current route is passed in to `props.navigation.state`:
    const { params } = this.props.navigation.state;
    return (
      <View>
        <Text>Chat with {params.user}</Text>
      </View>
    );
  }
}

class NavigatorWrappingScreen extends React.Component {
  render() {
    return (
      <TabNav navigation={this.props.navigation}/>
    );
  }
}
NavigatorWrappingScreen.router = TabNav.router;

const ReactNavigationLearning = StackNavigator({
  //Home: { screen: HomeScreen },
  //Home: { screen: TabNav },
      Home: { screen: NavigatorWrappingScreen },
        Chat: { screen: ChatScreen },
      }, 
      // StackNavigatorConfig, https://reactnavigation.org/docs/navigators/stack
      { mode: "modal" } 
);

// AppRegistry.registerComponent('ReactNavigationLearning', () => ReactNavigationLearning)
// AppRegistry.registerComponent('ReactNavigationLearning', () => TabNav);
AppRegistry.registerComponent('ReactNavigationLearning', () => ReactNavigationLearning);