import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../components/Login.vue'
import Home from '../components/Home.vue'
import Legals from '../components/Legals/Legals.vue'
import News from '../components/News/News.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/', redirect: '/login'
  },
  { path: '/login', name: 'Login', component: Login },
  { 
    path: '/home', 
    name: 'Home', 
    component: Home, 
    redirect: '/legalList',
    children: [
        { path: '/legalList', component: Legals },
        { path: '/newsList', component: News }
    ]
  }
]

const router = new VueRouter({
  routes
})

export default router
