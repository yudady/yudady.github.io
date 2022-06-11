import { ArticleItem } from '@/types/data'
import { ArticleAction } from '@/types/store'

interface IState {
  articles: ArticleItem[]
}

const initValue: IState = {
  articles: [],
}

export default function article(state = initValue, action: ArticleAction): IState {
  switch (action.type) {
    case 'ARTICLE_SAVE':
      return {
        ...state,
        articles: action.payload,
      }
    default:
      return state
  }
}
