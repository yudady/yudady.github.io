import { ApiResponse, ArticleItem } from '@/types/data'
import { RootThunkAction } from '@/types/store'
import request from '@/utils/request'

export const getArticle = (id: number): RootThunkAction => {
  return async (dispatch) => {
    // 泛型参数 1 控制的就是 res.data 的类型
    const res = await request.get<ApiResponse<{ results: ArticleItem[] }>>(`/articles?channel_id=${id}&timestamp=${Date.now()}`)
    dispatch({
      type: 'ARTICLE_SAVE',
      payload: res.data.data.results,
    })
  }
}
