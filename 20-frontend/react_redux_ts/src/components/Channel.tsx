import { changeActive, getChannels } from '@/store/actions/channel'
import { RootState } from '@/types/store'
import { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'

export default function Channel() {
  const dispatch = useDispatch()
  const { channels, active } = useSelector((state: RootState) => state.channel)
  const handleClickChannel = (id: number) => dispatch(changeActive(id))
  useEffect(() => {
    dispatch(getChannels())
  }, [dispatch])
  return (
    <ul className='catagtory'>
      {channels.map((item) => (
        <li key={item.id} className={item.id === active ? 'select' : ''} onClick={() => handleClickChannel(item.id)}>
          {item.name}
        </li>
      ))}
    </ul>
  )
}
