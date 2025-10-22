<template>
	<magic-editor :config="defaultConfig"/>
</template>
<script setup>
let defaultConfig = {};
try {
	if (parent && parent.MAGIC_EDITOR_CONFIG) {
		defaultConfig = {...parent.MAGIC_EDITOR_CONFIG};
	}
} catch (ignored) { }
if (window.MAGIC_EDITOR_CONFIG) {
	defaultConfig = {...defaultConfig, ...window.MAGIC_EDITOR_CONFIG}
}
defaultConfig.baseURL = import.meta.env.MODE === 'development' ? 'http://127.0.0.1:9999/magic/web' : './';
defaultConfig.serverURL = import.meta.env.MODE === 'development' ? 'http://127.0.0.1:9999/' : './';
defaultConfig.inJar = true;
const getQueryString = name => {
	var r = window.location.search.substr(1).match(new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i'));
	return r && unescape(r[2])
}
const headerName = getQueryString('headerName')
const headerValue = getQueryString('headerValue')
if(headerName && headerValue){
	defaultConfig.request = defaultConfig.request || {}
	defaultConfig.request.beforeSend = cfg => {
		cfg.headers[headerName] = headerValue
		return cfg
	}
}
</script>
<style>
html,body,#app{
  width: 100%;
  height: 100%;
  margin:0 !important;
}
</style>