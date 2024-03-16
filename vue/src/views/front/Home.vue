<template>
    <div class="main-content">

        <div style="border: 2px solid black;margin:15% 43%;width: 240px;height: 100px">
            <el-button icon="el-icon-folder-add" type="warning" size="mini"
                       style="
                       margin: 13% 10%;
                       font-size: 22px;background-color: rgb(25,80,134); border-color: rgb(25,80,134)"@click="handleAdd" >点击创建问卷
            </el-button>
        </div>

        <el-dialog title="模板信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
            <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
                <el-form-item label="模板名称" prop="name">
                    <el-input v-model="form.name" placeholder="模板名称"></el-input>
                </el-form-item>
                <el-form-item label="模板介绍" prop="descr">
                    <el-input v-model="form.descr" placeholder="模板介绍"></el-input>
                </el-form-item>
                <el-form-item label="封面图" prop="img">
                    <el-upload
                        :action="$baseUrl + '/files/upload'"
                        :headers="{ token: user.token }"
                        list-type="picture"
                        :on-success="handleImgSuccess"
                    >
                        <el-button type="primary">上传</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="是否公开" prop="open" v-if="user.role == 'ADMIN'">
                    <el-radio-group v-model="form.open">
                        <el-radio label="是"></el-radio>
                        <el-radio label="否"></el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="fromVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>

export default {
    name: "Home",
    data() {
        return {
            fromVisible:false,
            form: {},
            user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
        }
    },
    mounted() {
        this.load()
    },
    // methods：本页面所有的点击事件或者其他函数定义区
    methods: {
        handleAdd() {   // 新增数据
            this.form = {}  // 新增数据的时候清空数据
            this.fromVisible = true   // 打开弹窗
        },
        save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
            this.$refs.formRef.validate((valid) => {
                if (valid) {
                    this.$request({
                        url: this.form.id ? '/pages/update' : '/pages/add',
                        method: this.form.id ? 'PUT' : 'POST',
                        data: this.form
                    }).then(res => {
                        if (res.code === '200') {  // 表示成功保存
                            this.$message.success('保存成功')
                            this.load(1)
                            this.fromVisible = false
                        } else {
                            this.$message.error(res.msg)  // 弹出错误的信息
                        }
                    })
                }
            })
        },
        handleImgSuccess(res) {
            // 将后台返回的图片url赋值给表单属性
            this.form.img=res.data
        },
        load() {

        }
    }
}
</script>

<style>
body{
    background-color: #dac9c9;
}

</style>