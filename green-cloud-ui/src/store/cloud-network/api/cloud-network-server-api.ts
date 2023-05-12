import axios from 'axios'
import { toast } from 'react-toastify'

export const resetServerState = (address: string) => {
   axios
      .get(address + '/reset')
      .then(() => console.log('Reset successful'))
      .catch((err) => {
         if (err.code === 'ERR_NETWORK') {
            console.error('Server is disconnected')
            toast.dismiss()
            toast.error('Server is disconnected')
         }
         console.error('An error occured while reseting the state:' + err)
      })
}
