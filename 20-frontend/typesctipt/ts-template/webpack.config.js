const path = require('path'); // 引入一个包
const HTMLWebpackPlugin = require('html-webpack-plugin'); // 引入html插件
const {CleanWebpackPlugin} = require('clean-webpack-plugin'); // 引入clean插件

module.exports = { // webpack中的所有的配置信息都应该写在module.exports中
    entry: "./src/index.ts", // 指定入口文件
    output: { // 指定打包文件所在目录
        path: path.resolve(__dirname, 'dist'), // 指定打包文件的目录
        filename: "bundle.js", // 打包后文件的文件
        environment: { // 告诉webpack不使用箭头
            arrowFunction: false
        }
    },
    module: { // 指定webpack打包时要使用模块
        rules: [ // 指定要加载的规则
            {
                test: /\.ts$/, // test指定的是规则生效的文件
                use: [ // 要使用的loader
                    { // 配置babel
                        loader: "babel-loader", // 指定加载器
                        options: { // 设置babel
                            presets: [
                                [ // 设置预定义的环境
                                    "@babel/preset-env", // 指定环境的插件
                                    { // 配置信息
                                        targets: {"chrome": "58", "ie": "11"}, /* 要兼容的目标浏览器*/
                                        "corejs": "3", // 指定corejs的版本
                                        "useBuiltIns": "usage" // 使用corejs的方式 "usage" 表示按需加载
                                    }
                                ]
                            ]
                        }
                    },
                    'ts-loader' // 編譯TS
                ],
                exclude: /node-modules/ // 要排除的文件
            }
        ]
    },
    plugins: [ // 配置Webpack插件
        new CleanWebpackPlugin(),
        new HTMLWebpackPlugin({
            template: "./src/index.html" // title: "这是一个自定义的title"
        }),
    ],
    resolve: { // 用来设置引用模块
        extensions: ['.ts', '.js']
    },
    mode: "development"
};