import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import viteCompression from 'vite-plugin-compression'
import vitePluginImp from 'vite-plugin-imp'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        react(),
        // Antd 按需引入
        vitePluginImp({
            libList: [
                {
                    libName: 'antd',
                    style: (name) => {
                        // 某些组件没有 style 目录，跳过样式加载
                        const skipStyleComponents = ['theme', 'app'];
                        if (skipStyleComponents.includes(name)) {
                            return false;
                        }
                        return `antd/es/${name}/style/index.js`;
                    },
                },
            ],
        }),
        // Gzip压缩
        viteCompression({
            verbose: true,
            disable: false,
            threshold: 10240,
            algorithm: 'gzip',
            ext: '.gz',
        }),
        // Brotli压缩
        viteCompression({
            verbose: true,
            disable: false,
            threshold: 10240,
            algorithm: 'brotliCompress',
            ext: '.br',
        }),
    ],
    optimizeDeps: {
        include: [
            'react',
            'react-dom',
            'react-router-dom',
            'antd',
            '@ant-design/icons',
            '@reduxjs/toolkit',
            'react-redux',
            'axios',
        ],
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            }
        }
    },
    build: {
        // 启用CSS代码分割
        cssCodeSplit: true,
        // 减小chunk大小警告限制
        chunkSizeWarningLimit: 1000,
        // 构建目标
        target: 'es2015',
        // 最小化
        minify: 'terser',
        // sourcemap
        sourcemap: false,
        rollupOptions: {
            output: {
                // 静态资源分类
                assetFileNames: (assetInfo) => {
                    if (assetInfo.name && assetInfo.name.includes('tac')) {
                        return 'tac/[name][extname]';
                    }
                    const name = assetInfo.name;
                    if (!name) {
                        return 'assets/[name].[hash][extname]';
                    }
                    if (/\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/i.test(name)) {
                        return `media/[name].[hash][extname]`;
                    }
                    if (/\.(png|jpe?g|gif|svg|webp|avif)(\?.*)?$/i.test(name)) {
                        return `images/[name].[hash][extname]`;
                    }
                    if (/\.(woff2?|eot|ttf|otf)(\?.*)?$/i.test(name)) {
                        return `fonts/[name].[hash][extname]`;
                    }
                    return `assets/[name].[hash][extname]`;
                },
                // chunk文件命名
                chunkFileNames: 'js/[name].[hash].js',
                // 入口文件命名
                entryFileNames: 'js/[name].[hash].js',
                // 手动分包
                manualChunks: (id) => {
                    // node_modules中的包单独打包
                    if (id.includes('node_modules')) {
                        // antd相关
                        if (id.includes('antd')) {
                            return 'vendor-antd';
                        }
                        // react相关
                        if (id.includes('react') || id.includes('react-dom') || id.includes('react-router')) {
                            return 'vendor-react';
                        }
                        // redux相关
                        if (id.includes('redux')) {
                            return 'vendor-redux';
                        }
                        // 其他第三方库
                        return 'vendor';
                    }
                    return undefined;
                },
            },
        },
        terserOptions: {
            compress: {
                drop_console: true,
                drop_debugger: true,
                pure_funcs: ['console.log'],
            },
        } as any,
    },
})