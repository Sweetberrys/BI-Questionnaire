<template>
  <div>

      <el-empty description="403" v-if="user.role === 'ADMIN'"><router-link to="/">返回首页</router-link></el-empty>
      <el-empty description="403" v-else><router-link to="/front/home">返回首页</router-link></el-empty>
  </div>
</template>

<script>
export default {
  name: "Auth",
  data() {
    return {
        user: JSON.parse(localStorage.getItem('xm-user') || '{}'),

    }
  },
  created() {

  },
  methods: {}
}
</script>

<style scoped>

</style>