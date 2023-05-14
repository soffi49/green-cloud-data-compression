import { AdaptationPanel } from '@components'
import ClientStatistics from 'components/main-panel/client-panel/client-panel-connected'
import CloudStatistics from 'components/main-panel/cloud-panel/cloud-panel-connected'
import AgentPanel from './agent-panel/agent-panel-connected'
import { MenuTab } from '@types'

export const PANEL_TABS = [
   {
      header: 'Cloud network statistics',
      id: MenuTab.CLOUD_SUMMARY,
      panel: <CloudStatistics />,
      removeScroll: false,
   },
   {
      header: 'Agents statistics',
      id: MenuTab.AGENTS,
      panel: <AgentPanel />,
      removeScroll: false,
   },
   {
      header: 'Clients statistics',
      id: MenuTab.CLIENTS,
      panel: <ClientStatistics />,
      removeScroll: true,
   },
   {
      header: 'System adaptation statistics',
      id: MenuTab.ADAPTATION,
      panel: <AdaptationPanel />,
      removeScroll: false,
   },
]
