import {
  SIGNUP_USER_REQUEST,
  SIGNUP_USER_RESPONSE
} from '../constants/type';

const initialState = {
  signup_user: {
    first_name: "",
    last_name: "",
    birthday: ""
  }
}

const signupReducer = (state = initialState, action) => {
  switch (action.type) {
    case SIGNUP_USER_REQUEST:
      return Object.assign({}, state, {
        signup_user: action.signup_user
      })
    case SIGNUP_USER_RESPONSE:
      return Object.assign({}, state, {
        signup_user: action.signup_user
      })
    default:
      return state;
  }
}

export default signupReducer;