import axios from "axios";

const createAxiosFn = (token: any, param1: any, param2: any) => {
    let headerParams = {
        "token": token,
        "param1": param1,
        "param2": param2
    }

    let newParams = {}
    for (let key in headerParams){
        if (headerParams[key]){
            newParams[key] = headerParams[key]
        }
    }

    let service = axios.create({
        baseURL: "/api",
        timeout: 20000,
        headers:{
            ...newParams
        },
    });

    // 请求拦截
    service.interceptors.request.use(
        config=>{
            return config
        },err=>{
            return Promise.reject(err)
        }
    )

    // 响应拦截
    service.interceptors.response.use(
        res=>{
            return res.data
        },err=>{
            return Promise.reject(err)
        }
    )

    return service
}

export default createAxiosFn