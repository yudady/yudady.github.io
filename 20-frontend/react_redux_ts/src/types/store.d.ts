import { ThunkAction } from 'redux-thunk'
import store from '../store'
import { ArticleItem, ChannelItem } from './data'
// !#1
export type RootState = ReturnType<typeof store.getState>
export type ChannelAction =
  | {
      type: 'CHANNEL_SAVE'
      payload: ChannelItem[]
    }
  | {
      type: 'CHANNEL_ACTIVE'
      id: number
    }

export type ArticleAction = {
  type: 'ARTICLE_SAVE'
  payload: ArticleItem[]
}
// !#2
export type RootAction = ChannelAction | ArticleAction

// !#3
export type RootThunkAction = ThunkAction<void, RootState, unknown, RootAction>
