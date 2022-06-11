import { ApiResponse, ChannelItem } from '@/types/data'
import { ChannelAction, RootThunkAction } from '@/types/store'
import request from '@/utils/request'

export const getChannels = (): RootThunkAction => {
  return async (dispatch) => {
    const res = await request.get<ApiResponse<{ channels: ChannelItem[] }>>('/channels')
    dispatch({
      type: 'CHANNEL_SAVE',
      payload: res.data.data.channels,
    })
  }
}

export const changeActive = (id: number): ChannelAction => ({
  type: 'CHANNEL_ACTIVE',
  id,
})
