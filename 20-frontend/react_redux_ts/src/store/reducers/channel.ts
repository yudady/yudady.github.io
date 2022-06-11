import { ChannelItem } from '@/types/data'
import { ChannelAction } from '@/types/store'

interface IState {
  channels: ChannelItem[]
  active: number
}

const initValue: IState = {
  channels: [],
  active: 0,
}

export default function channel(state = initValue, action: ChannelAction): IState {
  switch (action.type) {
    case 'CHANNEL_SAVE':
      return {
        ...state,
        channels: action.payload,
        active: action.payload[0].id,
      }
    case 'CHANNEL_ACTIVE':
      return {
        ...state,
        active: action.id,
      }
    default:
      return state
  }
}
