import { RootState, selectReports, selectReportsForChosenAgent } from '@store'
import { connect } from 'react-redux'
import LivePanel from './live-panel'

const mapStateToProps = (state: RootState) => {
   return {
      reports: selectReports(state),
      agentReports: selectReportsForChosenAgent(state),
   }
}

export default connect(mapStateToProps, null)(LivePanel)
