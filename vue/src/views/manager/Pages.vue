<template>
    <div>
        <div class="search">
            <el-button type="success" plain @click="handleAdd">新增</el-button>
            <el-button type="warning" plain @click="delBatch">批量删除</el-button>
            <el-input placeholder="请输入名称查询" style="margin-left:40%;width: 200px; margin-right: 10px"
                      v-model="name"></el-input>
            <el-input placeholder="请输入用户名称查询" style="width: 200px" v-model="userName"></el-input>
            <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
            <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
        </div>

        <div class="table">
            <el-table :data="tableData" strip @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
                <el-table-column prop="name" label="模板名称"></el-table-column>
                <el-table-column prop="descr" label="模板介绍"></el-table-column>
                <el-table-column prop="img" label="封面图">
                    <template v-slot="scope">
                        <div style="display: flex; align-items: center">
                            <el-image style="width: 50px; height: 50px; " v-if="scope.row.img"
                                      :src="scope.row.img" :preview-src-list="[scope.row.img]"></el-image>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="count" label="使用次数"></el-table-column>
                <el-table-column prop="userId" label="创建人ID"></el-table-column>
                <el-table-column prop="userName" label="创建人"></el-table-column>
                <el-table-column prop="open" label="是否公开"></el-table-column>
                <el-table-column prop="saved" label="是否发布"></el-table-column>
                <el-table-column prop="createTime" label="创建时间"></el-table-column>

                <el-table-column label="操作" align="center" width="180">
                    <template v-slot="scope">
                        <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination">
                <el-pagination
                        background
                        @current-change="handleCurrentChange"
                        :current-page="pageNum"
                        :page-sizes="[5, 10, 20]"
                        :page-size="pageSize"
                        layout="total, prev, pager, next"
                        :total="total">
                </el-pagination>
            </div>
        </div>


        <el-dialog title="模板信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false"
                   destroy-on-close>
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
                <el-form-item label="是否公开" prop="open">
                    <el-radio-group v-model="form.open">
                        <el-radio label="是"></el-radio>
                        <el-radio label="否"></el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="是否发布" prop="open">
                    <el-radio-group v-model="form.saved">
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
    name: "Pages",
    data() {
        return {
            tableData: [],  // 所有的数据
            pageNum: 1,   // 当前的页码
            pageSize: 10,  // 每页显示的个数
            total: 0,
            name: null,
            userName: null,
            fromVisible: false,
            form: {},
            user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
            rules: {
                name: [
                    {required: true, message: '请输入名称', trigger: 'blur'},
                ],
                descr: [
                    {required: true, message: '请输入简介', trigger: 'blur'},
                ]
            },
            ids: []
        }
    },
    created() {
        this.load(1)
    },
    methods: {
        handleAdd() {   // 新增数据
            this.form = {}  // 新增数据的时候清空数据
            this.fromVisible = true   // 打开弹窗
        },
        handleEdit(row) {   // 编辑数据
            this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
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
        del(id) {   // 单个删除
            this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
                this.$request.delete('/pages/delete/' + id).then(res => {
                    if (res.code === '200') {   // 表示操作成功
                        this.$message.success('操作成功')
                        this.load(1)
                    } else {
                        this.$message.error(res.msg)  // 弹出错误的信息
                    }
                })
            }).catch(() => {
            })
        },
        handleSelectionChange(rows) {   // 当前选中的所有的行数据
            this.ids = rows.map(v => v.id)   //  [1,2]
        },
        delBatch() {   // 批量删除
            if (!this.ids.length) {
                this.$message.warning('请选择数据')
                return
            }
            this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
                this.$request.delete('/pages/delete/batch', {data: this.ids}).then(res => {
                    if (res.code === '200') {   // 表示操作成功
                        this.$message.success('操作成功')
                        this.load(1)
                    } else {
                        this.$message.error(res.msg)  // 弹出错误的信息
                    }
                })
            }).catch(() => {
            })
        },
        load(pageNum) {  // 分页查询
            if (pageNum) this.pageNum = pageNum
            this.$request.get('/pages/selectPage', {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    name: this.name,
                    userName: this.userName,
                }
            }).then(res => {
                this.tableData = res.data?.list
                this.total = res.data?.total
            })
        },
        reset() {
            this.name = null
            this.userName = null
            this.load(1)
        },
        handleCurrentChange(pageNum) {
            this.load(pageNum)
        },
        handleImgSuccess(res) {
            this.form.img = res.data  // 把后台返回的图片url赋值给表单属性 img
        }
    }
}
</script>

<style scoped>

</style>