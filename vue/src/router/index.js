import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/front/home',  // 重定向到主页
    children: [
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/403.vue') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'pages', name: 'Pages', meta: { name: '问卷信息' }, component: () => import('../views/manager/Pages') },
      { path: 'question', name: 'Question', meta: { name: '题目信息' }, component: () => import('../views/manager/Question') },
      { path: 'questionItem', name: 'QuestionItem', meta: { name: '题目内容' }, component: () => import('../views/manager/QuestionItem') },
      { path: 'answer', name: 'Answer', meta: { name: '答题信息' }, component: () => import('../views/manager/Answer') },
    ]
  },
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue'),
    children: [
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/front/Home') },
      { path: 'person', name: 'Person', meta: { name: '个人信息' }, component: () => import('../views/front/Person') },
      { path: 'frontPages', name: 'FrontPages', meta: { name: '问卷列表' }, component: () => import('../views/front/FrontPages') },
      { path: 'model', name: 'Model', meta: { name: '问卷模板' }, component: () => import('../views/front/Model') },
      { path: 'viewPages', name: 'ViewPages', meta: { name: '问卷预览' }, component: () => import('../views/front/ViewPages') },
      { path: 'design', name: 'Design', meta: { name: '设计问卷' }, component: () => import('../views/front/Design') },
      { path: 'chart', name: 'Chart', meta: { name: 'AI图表分析' }, component: () => import('../views/front/Chart') },
      { path: 'chartForTable', name: 'ChartForTable', meta: { name: 'AI图表分析' }, component: () => import('../views/front/ChartForTable') },
      { path: 'chartForTableAsync', name: 'ChartForTableAsync', meta: { name: 'AI图表分析(异步化)' }, component: () => import('../views/front/ChartForTableAsync') },
      { path: 'myChart', name: 'MyChart', meta: { name: '我的AI图表分析结论' }, component: () => import('../views/front/MyChart') },
      { path: 'myChart2.0', name: 'MyChart2.0', meta: { name: '我的AI图表分析结论' }, component: () => import('../views/front/MyChart2.0') },
          ]
  },
  { path: '/realPage', name: 'RealPage', meta: { name: '填写问卷' }, component: () => import('../views/front/RealPage') },
  { path: '/thanks', name: 'Thanks', meta: { name: '感谢参与' }, component: () => import('../views/front/Thanks') },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
