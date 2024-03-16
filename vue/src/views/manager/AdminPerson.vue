<template>
    <div style="height: 100%;margin-left: 27%">
        <el-card style="width: 60%;height: 100%;">
            <el-form :model="user" :rules="rules" label-width="100px" style="padding-right: 50px" ref="userForm">
                <div style="margin: 45px; text-align: center">
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
                <el-form-item label="性别" prop="gender">
                    <el-input v-model="user.gender" placeholder="性别"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="user.phone" placeholder="电话"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="user.email" placeholder="邮箱"></el-input>
                </el-form-item>
                <el-form-item label="出生日期" prop="birth">
                    <el-date-picker
                        v-model="user.birth"
                        type="datetime"
                        placeholder="选择出生日期时间">
                    </el-date-picker>
                </el-form-item>
                <div style="text-align: center; margin-bottom: 20px">
                    <el-button type="danger" @click="update">保 存</el-button>
                </div>
            </el-form>
        </el-card>

    </div>
</template>

<script>
export default {
    name: "AdminPerson",
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
                gender: [
                    {required: true, message: '请输入性别', trigger: 'blur'},
                ],
            }
        }
    },
    created() {

    },
    methods: {
        update() {
            this.$refs['formRef'].validate((valid) => {
                if (valid) {
                    // 保存当前的用户信息到数据库
                    this.$request.put('/admin/update', this.user).then(res => {
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
    }
}
</script>

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