import axios from 'axios'

function getUserInfo() {
  console.log(localStorage.getItem('user-token'))
  const config = {
    method: 'get',
    url: '/api/information',
    headers: {
      'Content-Type': 'application/json',
      'token': localStorage.getItem('user-token'),
    },
  }

  return axios(config)
    .then((response) => {
      return response
    })
    .catch((error) => {
      throw error
    })
}

export { getUserInfo }
