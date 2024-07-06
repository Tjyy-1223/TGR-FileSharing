const store={
  state:{   // 放数据
    num:20
  },
  actions:{  // 只放同步的方法
    add1(newState:{num:number},action:{type:string}){
        newState.num++
    },
    add2(newState:{num:number},action:{type:string,val:number}){
      newState.num+=action.val
    },
    add3(newState:{num:number},action:{type:string,val:number}){
      newState.num+=action.val
    },
  },
  // 优化redux-thunk的异步写法(模仿Vuex的写法)
  asyncActions:{  // 只放异步的方法
    asyncAdd1(dispatch:Function){
      setTimeout(()=>{
        dispatch({type:"add1"})
      },1000)
    }
  },
  // 名字统一管理
  // add1:"add1",
  // add2:"add2"
  // actionNames:{
  //   add1:"add1",
  //   add2:"add2",
  // }
  actionNames:{}
}
// 我们现在想做到actionNames自动生成。不用我每一次添加一个方法，都要在actionNames手动添加键值对，这样很麻烦。
let actionNames = {} // 定义一个全局的actionNames
// actionNames有多少对键值对，取决于action里面有多少个函数。所以遍历store.actions，给actionNames添加键值对
for(let key in store.actions){
  actionNames[key] = key
}
store.actionNames=actionNames;

export default store
// 封装的目的：最终是有利于我们的开发或者维护
// 封装的思路是：将来开发的时候只需要把数据写和方法写入到这个状态文件中，例如：XxxxStatus/index.ts，而不需要再去操作其他的文件。(我们往这个方向去封装)