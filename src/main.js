import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './plugins/element.js'

import './assets/css/global.css'

import './assets/fonts/iconfont.css'

import axios from 'axios'

import VueQuillEditor from 'vue-quill-editor'

// require styles
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

axios.defaults.baseURL = 'http://40.73.72.56:1311/nacos-provide'

Vue.prototype.$http = axios

Vue.config.productionTip = false

Vue.use(VueQuillEditor)

new Vue({
router,
store,
render: h => h(App)
}).$mount('#app')
