import { getArticle } from '@/store/actions/article'
import { RootState } from '@/types/store'
import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import avatar from '../assets/back.jpg'
export default function Article() {
  const dispatch = useDispatch()
  const {
    channel: { active },
    article: { articles },
  } = useSelector((state: RootState) => state)
  useEffect(() => {
    dispatch(getArticle(active))
  }, [dispatch, active])
  return (
    <div className='list'>
      {articles.map((item) => (
        <div key={item.art_id} className='article_item'>
          <h3 className='van-ellipsis'>{item.title}</h3>
          <div className='img_box'>
            <img src={item.cover.images ? item.cover.images[0] : avatar} className='w100' alt='' />
          </div>
          <div className='info_box'>
            <span>{item.pubdate}</span>
            <span>{item.comm_count} 评论</span>
            <span>{item.aut_id}</span>
          </div>
        </div>
      ))}
    </div>
  )
}
