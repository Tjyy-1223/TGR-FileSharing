import request from "./index"


export const LoginAPI = (params: any):Promise<Res> =>request.get("/login/username?"
    + "username=" + params.username + "&password=" + params.password
);

export const RegisterAPI = (params:RegisterAPIReq):Promise<Res> => request.post("/login/register",params);