// 这个文件专门定义请求参数的类型，和响应的类型

// 登录请求参数类型约束
interface LoginAPIReq{
  username:string; 
  password:string;
}

// 注册请求参数类型约束
interface RegisterAPIReq{
  username:string;
  password:string;
}

// 注册的响应类型约束
interface Res<T = any>{
  status:Status;
  result: T;
}

interface Status{
  code:number;
  msg:string;
}