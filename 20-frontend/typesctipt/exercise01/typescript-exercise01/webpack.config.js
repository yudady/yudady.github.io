const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin'); // 通过 npm 安装
const webpack = require('webpack'); // 用于访问内置插件

const config = {
    entry: './src/index.js',
    output: {
        publicPath: 'public',
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'public'),
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                include: [path.resolve(__dirname, 'src')],
                use: 'ts-loader',
            }
        ]
    },
    plugins: [
        new HtmlWebpackPlugin({ template: './src/index.html' })
    ]
};

module.exports = config;