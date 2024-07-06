import { useEffect } from 'react'
import { useRoutes, useLocation,useNavigate } from "react-router-dom"
import router from "./router"
import { message } from "antd"

// 去往登录页的组件
function ToLogin(){
  const navigateTo = useNavigate()
  // 加载完这个组件之后实现跳转
  useEffect(()=>{
    // 加载完组件之后执行这里的代码
    navigateTo("/login");
    message.warning("您还没有登录，请登录后再访问！");
  },[])
  return <div></div>
}
// 去往首页的组件
function ToPage1(){
  const navigateTo = useNavigate()
  // 加载完这个组件之后实现跳转
  useEffect(()=>{
    // 加载完组件之后执行这里的代码
    navigateTo("/page1");
    message.warning("您已经登录过了！");
  },[])
  return <div></div>
}
// 手写封装路由守卫
function BeforeRouterEnter(){
  const outlet = useRoutes(router);

  /*
    后台管理系统两种经典的跳转情况：
    1、如果访问的是登录页面， 并且有token， 跳转到首页
    2、如果访问的不是登录页面，并且没有token， 跳转到登录页
    3、其余的都可以正常放行
  */
    const location = useLocation()
    let token = localStorage.getItem("lege-react-management-token");
    //1、如果访问的是登录页面， 并且有token， 跳转到首页
    if(location.pathname==="/login" && token){
      // 这里不能直接用 useNavigate 来实现跳转 ，因为需要BeforeRouterEnter是一个正常的JSX组件
      return <ToPage1 />
    }
    //2、如果访问的不是登录页面，并且没有token， 跳转到登录页
    if(location.pathname!=="/login" && !token){
      return <ToLogin />
    }

    return outlet
}


function App() {  
  return (
    <div className="App">

      {/* <Link to="/home">Home</Link> |
      <Link to="/about">About</Link> |
      <Link to="/user">User</Link> */}

      {/* 占位符组件，类似于窗口，用来展示组件的，有点像vue中的router-view */}
      {/* <Outlet></Outlet> */}
      {/* {outlet} */}
      <BeforeRouterEnter />
    </div>
  )
}

export default App
