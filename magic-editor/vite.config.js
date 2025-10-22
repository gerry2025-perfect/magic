import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import viteSvgIcons from 'vite-plugin-svg-icons'
import { MonacoEditorNls } from './src/plugins/monaco-editor-nls.js'
import { MonacoExcludeLanguages } from './src/plugins/monaco-exclude-languages.js'
import path from 'path'
const buildIndex = () => {
	return {
		name: 'html-transform',
		transformIndexHtml(html) {
			return html.replace(/@VERSION@/g, require('./package.json').version)
		}
	}
}
// https://vitejs.dev/config/
export default defineConfig({
	base: './',
	optimizeDeps: {
		esbuildOptions: {
			plugins: [
				MonacoEditorNls(true),
				MonacoExcludeLanguages(true)
			]
		}
	},
	server: {
		host: true
	},
	build: {
		cssCodeSplit: false,
		rollupOptions: {
			// 屏蔽不需要的语言
			output: {
				minifyInternalExports: true,
				manualChunks: (id, { getModuleInfo }) => {
					if (id.includes('node_modules')) {
						if(id.includes('axios')){
							return 'axios'
						}
						if(id.includes('vue')){
							return 'vue'
						}
						return 'vendor';
					} else if(id.includes('/src/')){
						return 'app'
					}
				}
			},
		}
	},
	plugins: [
		vue(),
		viteSvgIcons({
			iconDirs: [path.resolve(process.cwd(), 'src/icons')],
			symbolId: 'magic-icon-[name]'
		}),
		buildIndex(),
		MonacoEditorNls(),
		MonacoExcludeLanguages()
	]
})