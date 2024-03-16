<template>
    <div class="main-content">
        <el-card style="width: 50%; margin: 30px auto">
            <div style="text-align: right; margin-bottom: 20px">
                <el-button type="danger" @click="updatePassword">修改密码</el-button>
            </div>
            <el-form :model="user" :rules="rules" label-width="100px" style="padding-right: 50px" ref="userForm">
                <div style="margin: 15px; text-align: center">
                    <el-upload
                            class="avatar-uploader"
                            :action="$baseUrl + '/files/upload'"
                            :show-file-list="false"
                            :on-success="handleAvatarSuccess"
                    >
                        <img v-if="user.avatar" :src="user.avatar" class="avatar"/>
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </div>
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="user.username" placeholder="用户名" disabled></el-input>
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="user.name" placeholder="姓名"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="user.phone" placeholder="电话"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="user.email" placeholder="邮箱"></el-input>
                </el-form-item>
                <div style="text-align: center; margin-bottom: 20px">
                    <el-button type="danger" @click="update">保 存</el-button>
                </div>
            </el-form>
        </el-card>
        <el-dialog title="修改密码" :visible.sync="dialogVisible" width="40%" :close-on-click-modal="false"
                   destroy-on-close>
            <el-form :model="user" label-width="100px" style="padding-right: 20px" :rules="rules" ref="formRef">
                <el-form-item label="原始密码" prop="password">
                    <el-input show-password v-model="user.password" placeholder="原始密码"></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input show-password v-model="user.newPassword" placeholder="新密码"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input show-password v-model="user.confirmPassword" placeholder="确认密码"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible=false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {

        const validatePassword = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请确认密码'))
            } else if (value !== this.user.newPassword) {
                callback(new Error('确认密码错误'))
            } else {
                callback()
            }
        }
        const validateNewPassword = (rule, value, callback) => {
            if (value.length > 6 || value.length < 8) {
                callback(new Error('新密码长度需在6-8个字符之间'))
            } else {
                callback()
            }
        }
        const validateName = (rule, name, callback) => {
            if (name.length < 6 || name.length > 8) {
                callback(new Error('用户名长度需在6-8个字符之间'));
            } else if (!/^[a-zA-Z0-9]+$/.test(name)) { // 然后使用正则表达式检查用户名是否只包含字母和数字
                callback(new Error('用户名中不能包含特殊符号,.!！@#$%^&*()！。？/、+='));
            } else {
                callback(); // 如果以上两个条件都满足，则调用callback()表示验证通过
            }
        }
        const validatePhone = (rule, phone, callback) => {
            if (!/^1[34578]\d{9}$/.test(phone)) {
                callback(new Error('手机号格式不正确'));
            } else {
                callback(); // 如果以上两个条件都满足，则调用callback()表示验证通过
            }
        }
        const validateEmail = (rule, email, callback) => {
            if (!/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
                .test(email)) {
                callback(new Error('邮箱格式不正确'));
            } else {
                callback(); // 如果以上两个条件都满足，则调用callback()表示验证通过
            }
        }
        return {
            user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
            dialogVisible: false,
            rules: {
                password: [
                    {required: true, message: '请输入原始密码', trigger: 'blur'},
                ],
                newPassword: [
                    {required: true, message: '请输入新密码', trigger: 'blur'},
                    {validator: validateNewPassword, required: true, trigger: 'blur'},

                ],
                confirmPassword: [
                    {validator: validatePassword, required: true, trigger: 'blur'},
                ],
                name: [
                    {validator: validateName, trigger: 'blur'}
                ],
                phone: [
                    {validator: validatePhone, trigger: 'blur'}
                ],
                email: [
                    {validator: validateEmail, trigger: 'blur'}
                ],
            }
        }
    },
    created() {

    },
    methods: {
        update() {
            let url = '';
            // 判断若是管理员，则更新管理员，反之用户也是
            if (this.user.role === 'ADMIN') {
                url = '/admin/update'
            } else {
                url = '/user/update'

            }
            // 保存当前的用户信息到数据库
            this.$refs['formRef'].validate((valid) => {
                if (valid) {
                    this.$request.put(url, this.user).then(res => {
                        if (res.code === '200') {
                            // 成功更新
                            this.$message.success('保存成功')
                            // 更新浏览器缓存里的用户信息
                            localStorage.setItem('xm-user', JSON.stringify(this.user))

                            // 触发父级的数据更新
                            this.$emit('update:user')
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                }
            })
        },
        handleAvatarSuccess(response, file, fileList) {
            // 把user的头像属性换成上传的图片的链接
            this.$set(this.user, 'avatar', response.data)
        },
        // 修改密码
        updatePassword() {
            this.dialogVisible = true
            this.fromVisible = true
        },
        save() {
            this.$refs.formRef.validate((valid) => {
                if (valid) {
                    this.$request.put('/updatePassword', this.user).then(res => {
                        if (res.code === '200') {
                            // 成功更新
                            this.$message.success('修改密码成功')
                            this.$router.push('/login')
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
<style>
body {
    background-color: #dac9c9;
}
</style>
<style scoped>


/deep/ .el-form-item__label {
    font-weight: bold;
}

/deep/ .el-upload {
    border-radius: 50%;
}

/deep/ .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    border-radius: 50%;
}

/deep/ .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
    border-radius: 50%;
}

.avatar {
    width: 120px;
    height: 120px;
    display: block;
    border-radius: 50%;
}
</style>