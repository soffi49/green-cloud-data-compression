import { RootState, selectNetworkStatistics } from '@store'
import { connect } from 'react-redux'
import { CloudStatistics } from './cloud-panel'

const mapStateToProps = (state: RootState) => {
   return {
      cloudStatistics: selectNetworkStatistics(state),
   }
}

export default connect(mapStateToProps)(CloudStatistics)
