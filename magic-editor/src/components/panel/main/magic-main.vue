<template>
	<div class="magic-main">
		<!-- 左侧导航条 -->
		<magic-navbar direction="vertical" :to="$refs.mrl" v-if="nextRender" ref="mnl" :spliter="true">
			<magic-navbar-item v-for="(navbar, index) in leftNavbars" :key="index" v-bind="navbar">
				<magic-resizer :max="750" :min="270" direction='x'>
					<magic-loading :loading="loading">
						<magic-resource v-bind="navbar" :data="resources[navbar.type]" @close="$refs.mnl.select(-1)" @onLoad="resourceOnLoad" tooltip-direction="left-bottom"/>
					</magic-loading>
				</magic-resizer>
			</magic-navbar-item>
		</magic-navbar>
		<div class="magic-main-body-wrapper">
			<div class="magic-main-body">
				<div ref="mrl" class="magic-navbar magic-navbar__vertical"/>
				<magic-script-editor/>
				<div ref="mrr" class="magic-navbar magic-navbar__vertical reverse"/>
			</div>
			<!-- 底部工具条 -->
			<magic-toolbar />
		</div>
		<!-- 右侧导航条 -->
		<magic-navbar  :reverse="true" :default-select="-1" direction="vertical" :to="$refs.mrr" :spliter="true" tooltip-direction="left">
			<magic-navbar-item v-for="(navbar, index) in rightNavbars" :key="index" :title="navbar.title" :icon="navbar.icon">
				<magic-resizer :max="420" :min="140" :value="200" direction='x' :reverse="true" v-if="nextRender">
					<magic-loading :loading="loading">
						<magic-data-resource  :type="navbar.type" :title="navbar.name" :data="resources[navbar.type]"/>
					</magic-loading>
				</magic-resizer>
			</magic-navbar-item>
		</magic-navbar>
		<!-- 全局搜索 -->
		<magic-search />
		<!-- 导出 -->
		<magic-export />
		<!-- 上传 -->
		<magic-upload />
		<!-- 推送 -->
		<magic-push />
		<!-- 最近打开 -->
		<magic-recent-opened />
	</div>
</template>
<script setup>
import { ref, nextTick, onMounted, provide } from 'vue'
import request from '../../../scripts/request.js'
import bus from '../../../scripts/bus.js'
import MagicAPI from '../../../scripts/service/magic-api.js'
import MagicFunction from '../../../scripts/service/magic-function.js'
import MagicDatasource from '../../../scripts/service/magic-datasource.js'
import Message from '../../../scripts/constants/message.js'
import { replaceURL } from '../../../scripts/utils.js'
import store from '../../../scripts/store.js'
import constants from '../../../scripts/constants.js'
import $i from '../../../scripts/i18n.js'
const nextRender = ref(false)
const resources = ref({})
const loading = ref(true)
const mnl = ref(null)
const emit = defineEmits(['onLoad'])
const deepFind = (array, itemOrId, stack) => {
	for(let i=0, len = array.length; i< len; i++){
		const item = array[i]
		if(item.id === itemOrId || item === itemOrId){
			stack.push(item)
			return true
		}else if(item.folder && item.children){
			if(deepFind(item.children, itemOrId, stack)) {
				stack.push(item)
				return true
			}
		}
	}
	return false
}
const findResource = id => {
	const entries = Object.entries(resources.value)
	for(let i=0, len = entries.length; i< len; i++){
		const nodes = []
		const entry = entries[i]
		deepFind(entry[1], id, nodes)
		if(nodes.length > 0){
			return {
				item: nodes[0],
				type: entry[0],
				name: replaceURL('/' + nodes.reverse().map(it => it.name).join('/')),
				path: replaceURL('/' + nodes.reverse().map(it => it.path || '').join('/'))
			}
		}
	}
}
provide('findResource', findResource)
const leftNavbars = [
	{ type: 'api', title: $i('api.name'), icon: 'api'},
	{ type: 'function', title: $i('fn.name'), icon: 'function'}
]
const services = {
	api: MagicAPI,
	function: MagicFunction,
	datasource: MagicDatasource
}
constants.PLUGINS.filter(it => it.resource && it.resource.length > 0).map(it => it.resource).forEach(res => res.forEach(it => {
	leftNavbars.push({
		type: it.type,
		icon: it.icon,
		title: $i(it.title),
	})
	if(it.service){
		services[it.type] = it.service
	}
}))
provide('service', services)
leftNavbars.map(it => it.type).forEach(key => resources.value[key] = [])
const rightNavbars = [
	{ type: 'datasource', title: $i('datasource.title'), icon: 'datasource', name: $i('datasource.name')}
]
constants.PLUGINS.filter(it => it.datasources && it.datasources.length > 0).map(it => it.datasources).forEach(res => res.forEach(it => {
	rightNavbars.push({
		type: it.type,
		icon: it.icon,
		title: it.title,
		name: it.name
	})
}))
rightNavbars.map(it => it.type).forEach(key => resources.value[key] = [])
provide('resources', () => {
    const navbars = [...leftNavbars, ...rightNavbars]
    const convert = (array) => {
        if(array.length === 1 && array[0].id.endsWith(':0')){
            return array[0].children
        }
        return array
    }
    return navbars.map(navbar => {
        return { key: navbar.type, navbar, tree: convert(JSON.parse(JSON.stringify(resources.value[navbar.type] || []))) }
    })
})
Object.values(services).forEach(service => service.injectResources && service.injectResources(type => resources.value[type]))
nextTick(() => nextRender.value = true)
const processNode = item => {
	return {
		...item.node,
		folder: item.node.parentId !== undefined,
		opened: item.node.parentId !== undefined && constants.DEFAULT_EXPAND === true,
		children: item.children&& item.children.length ? item.children.map(it => processNode(it)) : undefined
	}
}
const loadAllResources = (type, callback) => {
	loading.value = true
	resources.value = {}
	const name = type ? ((leftNavbars.find(it => it.type === type) || rightNavbars.find(it => it.type === type))?.title || '') : $i('message.all')
	bus.status(`message.getResource`, true, name)
	request.send('/resource').success(data => {
		[...leftNavbars, ...rightNavbars].filter(it => !type || it.type === type).forEach(it => {
			resources.value[it.type] = data[it.type]?.children?.map(item => processNode(item)) || []
		})
		loading.value = false
		bus.status(`message.getResourceFinish`, true, name)
		nextTick(() => callback())
	})
}
bus.$on(Message.RELOAD_RESOURCES, () => {
	loading.value = true
	request.sendGet('/reload').success(() => {
		bus.status('message.reloadResourceSuccess')
		loadAllResources(null, () => bus.$emit(Message.RELOAD_RESOURCES_FINISH))
	}).end(() => loading.value = false)

})
const loadResources = type => loadAllResources(type, () => bus.$emit(Message.LOAD_RESOURCES_FINISH, type))
bus.$on(Message.LOAD_RESOURCES, loadResources)
bus.$on(Message.RELOAD_RESOURCES_FINISH, loadResources)
const selectNavbarByItem = itemOrId => {
	let item;
	Object.values(resources.value).some(array => {
		const stack = []
		if(deepFind(array, itemOrId, stack)){
			item = stack[0]
			const index = leftNavbars.findIndex(it => it.type === stack[stack.length - 1].type)
			mnl.value&&mnl.value.select(index) // 选中
			return true
		}
		return false
	})
	return item
}
bus.$on(Message.SELECT_NAVBAR_BY_ITEM, selectNavbarByItem)
bus.$on(Message.OPEN_WITH_ID, id => {
	const item = selectNavbarByItem(id)
	if(item){
		bus.$emit(Message.OPEN_ITEM, item)
	}
})
bus.$on(Message.LOGOUT, () => resources.value = {})
defineExpose({ loadResources })
onMounted(() => emit('onLoad'))
let resource_load_idnex = 0
const resourceOnLoad = () => {
	if(++resource_load_idnex % leftNavbars.length === 0){
		try{
			JSON.parse(store.get(constants.RECENT_OPENED_TAB) || '[]').forEach(it => {
				bus.$emit(Message.OPEN_WITH_ID, it)
			})
		}catch(e){
			console.error(e)
		}
	}
}
bus.$on(Message.REFRESH_RESOURCE, id => {
	const resource = findResource(id)
	resource && request.sendGet(`/resource/file/${id}`).success(data => {
		Object.keys(data).forEach(key => resource.item[key] = data[key])
	})
})
</script>
<style scoped>
.magic-main{
	flex: 1;
	display: flex;
	border-bottom: 1px solid var(--main-border-color);
	height: 0;
	width: 100%;
}
.magic-main-body-wrapper{
	display:flex;
	flex:1;
	flex-direction: column;
}
.magic-main-body{
	display:flex;
	flex:1;
	overflow: hidden;
}
.magic-toolbar :deep(.magic-resizer-y .magic-resizer-event){
	top: auto;
}
</style>
