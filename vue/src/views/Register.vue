<template>
  <div class="container">
      <div style="margin-right:400px;width: 350px; padding: 30px; background-color: rgba(172,163,172,0.6); border-radius: 5px;box-shadow: 0 0 10px rgba(0,0,0,.5)">
      <div style="text-align: center; font-size: 20px; margin-bottom: 40px; color: #333">
          <img style="height: 25px;float: left;top: -10px;margin-left: 45px" src="@/assets/imgs/logo.png">
          <div style="float: left;margin-left: 10px">
              <strong>企业问卷调查系统</strong>
          </div>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="confirmPass">
          <el-input prefix-icon="el-icon-lock" placeholder="请确认密码" show-password  v-model="form.confirmPass"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button style="width: 100%; background-color: #333; border-color: #333; color: white" @click="register">注 册</el-button>
        </el-form-item>
        <div style="display: flex; align-items: center">
          <div style="flex: 1"></div>
          <div style="flex: 1; text-align: right">
              <strong style="color: #303133">已申请账号,请</strong> <a href="/login"><strong style="color: #5580d4">登录</strong></a>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    // 验证码校验
    const validatePassword = (rule, confirmPass, callback) => {
        if(this.form.password.length > 6 || this.form.password.length <8){
            if (confirmPass === '') {
                callback(new Error('请确认密码'))
            } else if (confirmPass !== this.form.password) {
                callback(new Error('原密码与确认密码不一致'))
            } else {
                callback()
            }
        }else{
            callback(new Error('密码与确认密码长度需在6-8个字符之间'))
        }
    }
    // 用户名校验
    const validateUserName = (rule,username, callback) => {
        if (username.length < 6 || username.length > 8) {
            callback(new Error('用户名长度需在6-8个字符之间'));
        } else if (!/^[a-zA-Z0-9]+$/.test(username)) { // 然后使用正则表达式检查用户名是否只包含字母和数字
            callback(new Error('用户名中不能包含特殊符号,.!！@#$%^&*()！。？/、+='));
        } else {
            callback(); // 如果以上两个条件都满足，则调用callback()表示验证通过
        }
    }
    return {
      form: {
          role:'USER',
      },
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
            { validator: validateUserName, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        confirmPass: [
          { validator: validatePassword, trigger: 'blur' },
        ],
      }
    }
  },
  created() {

  },
  methods: {
    register() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post('/register', this.form).then(res => {
            if (res.code === '200') {
              this.$router.push('/front/home')  // 跳转登录页面
              this.$message.success('注册成功')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/imgs/question_register.jpg");
  background-size: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}
a {
  color: #2a60c9;
}
</style>