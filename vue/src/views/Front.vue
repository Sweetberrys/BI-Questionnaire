<template>
  <div>
    <div class="front-notice" style="background-color:#333;color: white;font-weight: bold;font-size: 15px">
        <i class="el-icon-bell" style="margin-left: 15%;height:20px;line-height: 20px;"></i> 公告：{{ top }}</div>
    <!--头部-->
    <div class="front-header" >
      <div class="front-header-left">
        <img src="@/assets/imgs/logo.png" alt="">
        <div class="title">企业问卷调查系统</div>
      </div>
      <div class="front-header-center" >
        <div class="front-header-nav" >
          <el-menu :default-active="$route.path" mode="horizontal" style="color: #355476;font-weight: bold" router>
						<el-menu-item index="/front/home" style="color: #5e5454">首页</el-menu-item>
              <el-menu-item index="/front/model"style="color: #5e5454">问卷模板</el-menu-item>
              <el-menu-item index="/front/frontPages"style="color: #5e5454">问卷列表</el-menu-item>
              <el-menu-item index="/front/chartForTable"style="color: #5e5454">AI图表分析</el-menu-item>
              <el-menu-item index="/front/chartForTableAsync"style="color: #5e5454">AI图表分析(异步化)</el-menu-item>
              <el-menu-item index="/front/myChart"style="color: #5e5454">我的AI图表</el-menu-item>
              <el-menu-item index="/front/person"style="color: #5e5454">个人资料</el-menu-item>
              <el-menu-item v-if="user.role==='ADMIN'" index="/home"style="color: #5e5454">进入后台</el-menu-item>

          </el-menu>
        </div>
      </div>
      <div class="front-header-right">
        <div v-if="!user.username">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown>
            <div class="front-header-dropdown">
              <img :src="user.avatar" alt="">
              <div style="margin-left: 10px">
                <span>{{ user.name }}</span><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
              </div>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div style="text-decoration: none" @click="logout">退出</div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <!--主体-->
    <div class="main-body">
      <router-view ref="child" @update:user="updateUser" />
    </div>
  </div>

</template>

<script>

export default {
  name: "FrontLayout",

  data () {
    return {
      top: '',
      notice: [],
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
    }
  },

  mounted() {
    this.loadNotice()
  },
  methods: {
    loadNotice() {
      this.$request.get('/notice/selectAll').then(res => {
        this.notice = res.data
        let i = 0
        if (this.notice && this.notice.length) {
          this.top = this.notice[0].content
          setInterval(() => {
            this.top = this.notice[i].content
            i++
            if (i === this.notice.length) {
              i = 0
            }
          }, 2500)
        }
      })
    },
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')   // 重新获取下用户的最新信息
    },
    // 退出登录
    logout() {
      localStorage.removeItem("xm-user");
      this.$router.push("/login");
    },
  }

}
</script>

<style scoped>
  @import "@/assets/css/front.css";
</style>