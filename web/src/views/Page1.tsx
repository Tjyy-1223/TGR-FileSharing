import {useSelector, useDispatch} from "react-redux"
import numStatus from "@/store/NumStatus"
const View = () => {
  const dispatch = useDispatch();

  // 对num的操作
  // 通过useSelector获取仓库数据
  const {num,sarr} = useSelector((state:RootState)=>({
    num:state.handleNum.num,
    sarr:state.handleArr.sarr
  }))
  // 通过useDispatch修改仓库数据
  const changeNum = () =>{
    // dispatch({type:"字符串(认为是一个记号)",val:3})   type是固定的  val是自定义的
    // dispatch({type:"add1"})
    dispatch({type:"add3",val:100})   // 触发了reducer函数的执行
  }
  const changeNum2 = () =>{
    // 最开始的写法-同步的写法
    // dispatch({type:"add1"})  

    // 异步的写法- redux-thunk的用法  基本格式：  dispatch(异步执行的函数)
    // dispatch((dis:Function)=>{
    //   setTimeout(()=>{
    //     dis({type:"add1"})
    //   },1000)
    // })

    // 优化redux-thunk的异步写法  `
    // dispatch(调用状态管理中的asyncAdd1)
    dispatch(numStatus.asyncActions.asyncAdd1)
  }
  // 对sarr的操作
  // const {sarr} = useSelector((state:RootState)=>({
  //   sarr:state.handleArr.sarr
  // }));

  const changeArr = () =>{
    dispatch({type:"sarrpush",val:100}) 
  }

  return(
    <div className='home'>
        <p>这是Page1页面内容</p>
        <p>{num}</p>
        <button onClick={changeNum}>同步按钮</button>
        <button onClick={changeNum2}>异步按钮</button>

        <p>{sarr}</p>
        <button onClick={changeArr}>按钮</button>
    </div>
  )
}

export default View