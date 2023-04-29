import { AdaptationPanel } from '@components'
import ClientStatistics from 'components/main-panel/client-panel/client-panel-connected'
import CloudStatistics from 'components/main-panel/cloud-panel/cloud-panel-connected'
import AgentPanel from './agent-panel/agent-panel-connected'

export const PANEL_TABS = [
   {
      header: 'Cloud network statistics',
      id: 'cloud',
      panel: <CloudStatistics />,
      removeScroll: false,
   },
   {
      header: 'Agents statistics',
      id: 'agents',
      panel: <AgentPanel />,
      removeScroll: false,
   },
   {
      header: 'Clients statistics',
      id: 'clients',
      panel: <ClientStatistics />,
      removeScroll: true,
   },
   {
      header: 'System adaptation statistics',
      id: 'adaptation',
      panel: <AdaptationPanel />,
      removeScroll: false,
   },
]
