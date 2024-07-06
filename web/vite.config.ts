import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
// import styleImport,{AntdResolve} from 'vite-plugin-style-import';
import * as path  from "path"
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    // styleImport({
    //   resolves: [
    //     AntdResolve()
    //   ],
    // }),
  ],
  resolve:{
    alias:{
      "@":path.resolve(__dirname,'./src')
    }
  },
  server: {
    host: '0.0.0.0', //Ip地址
    port: 3002, //端口号
    hmr: true, //热启动
    open: true, //自动打开浏览器
    //配置代理
    proxy: {
      '/api': {	//指定了要代理的请求路径前缀。这意味着所有以 /api 开头的请求都会被代理到指定的目标地址。
        target: 'http://localhost:8080',
        // target就是你要访问的目标地址，可以是基础地址，这样方便在这个网站的其他api口调用数据
        changeOrigin: true,//表示是否改变请求头中的 Origin 字段，如果设置为 true，则会把请求头中的 Origin 字段改为目标地址。
        rewrite: (path) => path.replace(/^\/api/, ''),
        //是对请求路径进行重写的选项，它指定了如何重写请求路径。在这里，path.replace(/^\/api/, '') 表示将请求路径中的 /api 前缀替换为空，这样就去掉了原始请求路径中的 /api 前缀，使得请求路径符合目标服务器的预期。
        // 要记得加rewrite这句
      }
    }
  }
})

