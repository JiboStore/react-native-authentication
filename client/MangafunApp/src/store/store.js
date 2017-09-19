import { combineReducers, createStore, applyMiddleware } from 'redux';

import reducers from '../reducers';

import thunk from 'redux-thunk';