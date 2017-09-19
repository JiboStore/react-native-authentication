import {
  SIGNUP_USER_REQUEST,
  SIGNUP_USER_RESPONSE
} from '../constants/type';

const initialState = {
  signup_data: {
    firstname: "myfirstname",
    lastname: "mylastname"
  }
}

const signupReducer = (state = initialState, action) => {
  switch (action.type) {
    case SIGNUP_USER_REQUEST:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.firstname,
          lastname: action.lastname
        }
      })
    case SIGNUP_USER_RESPONSE:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.firstname,
          lastname: action.lastname
        }
      })
    default:
      return state;
  }
}

export default signupReducer;