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
          firstname: action.signup_data.firstname,
          lastname: action.signup_data.lastname
        }
      })
    case SIGNUP_USER_NAME:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.signup_data.firstname,
          lastname: action.signup_data.lastname
        }
      })
    case SIGNUP_USER_BDAY:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.signup_data.firstname,
          lastname: action.signup_data.lastname,
          bday: action.signup_data.bday
        }
      })
    case SIGNUP_USER_SEX:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.signup_data.firstname,
          lastname: action.signup_data.lastname,
          bday: action.signup_data.bday,
          sex: action.signup_data.sex
        }
      })
    case SIGNUP_USER_EMAIL:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.signup_data.firstname,
          lastname: action.signup_data.lastname,
          bday: action.signup_data.bday,
          sex: action.signup_data.sex,
          email: action.signup_data.email
        }
      })
    case SIGNUP_USER_PWD:
      return Object.assign({}, state, {
        signup_data: {
          firstname: action.signup_data.firstname,
          lastname: action.signup_data.lastname,
          bday: action.signup_data.bday,
          sex: action.signup_data.sex,
          email: action.signup_data.email,
          pwd: action.signup_data.pwd
        }
      })
    default:
      return state;
  }
}

export default signupReducer;