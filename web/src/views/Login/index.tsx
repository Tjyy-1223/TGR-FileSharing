import { ChangeEvent, useEffect, useState } from "react"
import { Input,Space,Button,message } from 'antd';
import styles from "./login.module.scss"
import initLoginBg from "./init.ts"
import cookie from "react-cookies"
import 'antd/dist/antd.css'; // or 'antd/dist/antd.less'
import './login.less'
import { useNavigate } from "react-router-dom"
import {LoginAPI, RegisterAPI} from "@/request/api"
const view = ()=>{
  let navigateTo = useNavigate();
  // 加载完这个组件之后，加载背景
  useEffect(()=>{
    initLoginBg();
    window.onresize = function(){initLoginBg()};
  },[]);
  // 获取用户输入的信息
  const [usernameVal,setUsernameVal] = useState(""); // 定义用户输入用户名这个变量
  const [passwordVal,setPasswordVal] = useState(""); // 定义用户输入密码这个变量

  const usernameChange = (e:ChangeEvent<HTMLInputElement>)=>{
    // 获取用户输入的用户名
    // console.log(e.target.value);
    // 修改usernameVal这个变量为用户输入的那个值。 以后拿到usernameVal这个变量就相当于拿到用户输入的信息。
    setUsernameVal(e.target.value);
  }
  const passwordChange = (e:ChangeEvent<HTMLInputElement>)=>{
    setPasswordVal(e.target.value);
  }

  // 点击登录按钮的事件函数
  const gotoLogin = async ()=>{
    console.log("用户输入的用户名，密码分别是：",usernameVal,passwordVal);
    // 验证是否有空值
    if(!usernameVal.trim() || !passwordVal.trim()){
      message.warning("请完整输入信息！")
      return
    }
    // 发起登录请求
    let loginAPIRes = await LoginAPI({
      username:usernameVal,
      password:passwordVal
    })

    console.log(loginAPIRes);
    if(loginAPIRes.status.code === 0){
      message.success("登录成功！")
      navigateTo("/page1")
    }
  }

  // 点击注册按钮的事件函数
  const gotoRegister = async ()=>{
    console.log("用户输入的用户名，密码是：",usernameVal, passwordVal);
    // 验证是否有空值
    if(!usernameVal.trim() || !passwordVal.trim()){
      message.warning("请完整输入信息！")
      return
    }
    // 发起登录请求
    let registerAPIRes = await RegisterAPI({
      username:usernameVal,
      password:passwordVal,
    })

    console.log(registerAPIRes);
    if(registerAPIRes.status.code===0){
      // 1、提示登录成功
      message.success("注册成功！")
      navigateTo("/page1")
    }else{
      message.warning("注册失败, " + registerAPIRes.status.msg)
    }
  }

  return (
    <div className={styles.loginPage}>
      {/* 存放背景 */}
      <canvas id="canvas" style={{display:"block"}}></canvas>
      {/* 登录盒子 */}
      <div className={styles.loginBox+ " loginbox"}>
          {/* 标题部分 */}
          <div className={styles.title}>
              <h1>前端乐哥&nbsp;·&nbsp;通用后台系统</h1>
              <p>Strive Everyday</p>
          </div>
          {/* 表单部分 */}
          <div className="form">
            <Space direction="vertical" size="large" style={{ display: 'flex' }}>
              <Input placeholder="用户名" onChange={usernameChange}/>
              <Input.Password placeholder="密码" onChange={passwordChange}/>

              <div style={{display: "flex"}}>
                  <Button type="primary" className="registerBtn" block onClick={gotoRegister}>注册</Button>
                  <Button type="primary" className="loginBtn" block onClick={gotoLogin}>登录</Button>
              </div>

            </Space>
          </div>
      </div>
    </div>
  )
}
export default view