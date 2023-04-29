import { IconClients, IconCloudMenu, IconGear, IconNetwork } from '@assets'

export const ICON_SIZE = '45px'
export const ICON_OFFSET = -70

export const MENU_BUTTONS = [
   {
      header: 'CLOUD SUMMARY',
      icon: <IconCloudMenu size={ICON_SIZE} />,
      iconOffset: ICON_OFFSET,
      id: 'cloud',
   },
   {
      header: 'AGENTS',
      icon: <IconNetwork size={ICON_SIZE} />,
      iconOffset: ICON_OFFSET,
      id: 'agents',
   },
   {
      header: 'CLIENTS',
      icon: <IconClients size={ICON_SIZE} />,
      iconOffset: ICON_OFFSET,
      id: 'clients',
   },
   {
      header: 'ADAPTATION',
      icon: <IconGear size={ICON_SIZE} />,
      iconOffset: ICON_OFFSET,
      id: 'adaptation',
   },
]
