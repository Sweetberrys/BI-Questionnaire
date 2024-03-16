<template>
  <div style="height: 100%;">
    <div class="card" style="padding: 15px;display: flex;">
        <div>
            <a href="/front/home" style="margin-left: 10px"><strong>进入前台首页</strong></a>
        </div>
        <div style="margin-left:76%;">
            您好，<strong>{{ user?.name }}</strong>！欢迎使用本系统
        </div>
    </div>

    <div style="display: flex; margin: 10px 0;height: 90%">
      <div style="width: 100%;" class="card">
        <div style="margin-bottom: 30px; font-size: 30px; font-weight: bold;margin-left: 45%;display: flex;">
            <div><img src="@/assets/imgs/logo.png" style="width: 40px;margin:0px auto"></div>
            <div>公告列表</div>

        </div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: []
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
  }
}
</script>
