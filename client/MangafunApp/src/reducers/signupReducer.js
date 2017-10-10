import {
  SIGNUP_USER_REQUEST,
  SIGNUP_USER_RESPONSE,
  SIGNUP_USER_NAME,
  SIGNUP_USER_BDAY,
  SIGNUP_USER_SEX,
  SIGNUP_USER_EMAIL,
  SIGNUP_USER_PWD
} from '../constants/type';

const initialState = {
  signup_data: {
    firstname: "myfirstname",
    lastname: "mylastname",
    bday: "bday",
    sex: "sex",
    email: "email",
    pwd: "pwd"
  }
}

const signupReducer = (state = initialState, action) => {
  switch (action.type) {
    case SIGNUP_USER_REQUEST:
      return Object.assign({}, state, {
        signup_data: action.signup_data
      })
    case SIGNUP_USER_RESPONSE:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.firstname,
          lastname: action.lastname
        }
      })
    case SIGNUP_USER_NAME:
      return Object.assign({}, state, {
        signup_data: {
        }
      })
    default:
      return state;
  }
}

export default signupReducer;