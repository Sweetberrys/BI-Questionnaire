<template>
  <div>
    <div style="height: 100vh; overflow: hidden; display: flex; align-items: center; justify-content: center">
      <div style="font-size: 40px">404 找不到页面 <router-link to="/">返回首页</router-link></div>
    </div>
      <el-empty description="404" v-if="user.role === 'ADMIN'"><router-link to="/">返回首页</router-link></el-empty>
      <el-empty description="404" v-else><router-link to="/front/home">返回首页</router-link></el-empty>
  </div>
</template>

<script>
export default {
  name: "404",
  data() {
    return {}
  },
  created() {

  },
  methods: {}
}
</script>

<style scoped>

</style>