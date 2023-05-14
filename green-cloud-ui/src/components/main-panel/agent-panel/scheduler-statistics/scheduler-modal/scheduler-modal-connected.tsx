import { RootState, selectChosenClient, selectScheduledJobs } from '@store'
import { connect } from 'react-redux'
import { ScheduleModal } from './scheduler-modal'

const mapStateToProps = (state: RootState) => {
   return {
      jobs: selectScheduledJobs(state),
      clientData: selectChosenClient(state),
   }
}

export default connect(mapStateToProps)(ScheduleModal)
