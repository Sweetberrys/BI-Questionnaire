<template>
    <div class="main-content">

        <div>
            <el-input v-model="name" placeholder="请输入问卷名称进行搜索" clearable
                      style="width: 300px;margin-top:10px;margin-left: 600px;margin-right: 10px"
                      size="medium"></el-input>
            <el-button type="info" round size="medium" @click="load(1)"
                       style="font-size: 16px;background-color: rgb(84,76,76); border-color: rgb(84,76,76)">搜 索
            </el-button>
            <div style="">
                <el-button icon="el-icon-folder-add" type="warning" size="mini"
                           style="
                       margin:10px 45%;
                       font-size: 18px;background-color: rgb(25,80,134); border-color: rgb(25,80,134)"
                           @click="handleAdd()">创建问卷
                </el-button>
            </div>
        </div>


        <div class="card" v-for="item in tableData" :key="item.id" style="width: 53%;margin-left: 25%;margin-top: 20px">
            <div style="display: flex">
                <div style="flex: 1;width: 0">

                    <div style="display: flex;flex:1;width:0;align-items: center;">

                        <el-tag v-if="item.saved === '否'" type="warning" size="medium" style="margin-right: 10px">
                            未发布
                        </el-tag>
                        <el-tag v-if="item.saved === '是'" type="success" size="medium" style="margin-right: 10px">
                            已发布
                        </el-tag>
                    </div>
                    <div style="font-size: 20px;margin-top: -27px;margin-left: 70px;">{{ item.name }}</div>


                    <div>
                        <el-button type="text" style="color: #72767b;font-size: 19px;margin-top: 50px;margin-left: 0px"
                                   @click="handleEdit(item)">
                            <i class="el-icon-scissors"><strong> 编辑</strong></i>
                        </el-button>
                        <el-button type="text" @click="design(item.id)"
                                   style="color: #b0d766;font-size: 19px;margin-top: 50px;margin-left: 25px">
                            <i class="el-icon-paperclip"><strong> 设计</strong></i>
                        </el-button>
                        <el-button type="text" @click="copy(item.id)"
                                   style="color: orangered;font-size: 19px;margin-top: 50px;margin-left: 25px">
                            <i class="el-icon-tickets"><strong> 复制</strong></i>
                        </el-button>
                        <el-button type="text" v-if="item.saved==='是'" @click="share(item.id)"
                                   style="color: #98984c;font-size: 19px;margin-top: 50px;margin-left: 25px">
                            <i class="el-icon-upload"><strong> 分享</strong></i>
                        </el-button>
                        <el-button type="text"
                                   style="color: deeppink;font-size: 19px;margin-top: 50px;margin-left: 25px"
                                   v-if="item.open === '否'" @click="del(item.id)">
                            <i class="el-icon-close"><strong> 删除</strong></i>
                        </el-button>
                        <el-button type="text" v-if="item.saved==='是'" @click="getPageCount(item.id)"
                                   style="color: #4e4eb9;font-size: 19px;margin-top: 50px;margin-left: 25px">
                            <i class="el-icon-finished"><strong> 统计</strong></i>
                        </el-button>
                        <el-button type="text" v-if="item.saved==='是'" @click="getPageChart(item.id)"
                                   style="color: #4e4eb9;font-size: 19px;margin-top: 50px;margin-left: 25px">
                            <i class="el-icon-magic-stick"><strong> AI分析</strong></i>
                        </el-button>

                    </div>
                </div>

                <div style="width: 100px;">
                    <img :src="item.img" alt="" style="width: 100%;border-radius: 5px;display: block">
                </div>
            </div>

        </div>
        <!-- 分页 -->
        <div v-if="total " class="pagination" style="margin-left: 25%;margin-top: 30px;">
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
                <el-form-item label="是否公开" prop="open" v-if="user.role === 'ADMIN'">
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

        <el-dialog title="数据统计" :visible.sync="pageCountVisible" width="40%" :close-on-click-modal="false"
                   destroy-on-close>
            <div v-if="pageCount.answerCount ">
                <div style="margin-bottom: 10px">
                    <span style="margin-bottom: 10px">共收集<strong>{{
                        pageCount.answerCount
                        }}</strong>份问卷结果</span>
                </div>
                <div style="margin-bottom: 15px" class="card" v-for="(item,index) in pageCount.questionList"
                     :key="index">

                    <div style="margin-bottom: 5px"><strong>题目：{{ item.name }}</strong></div>
                    <div v-if="item.questionItemList.length">
                        <el-table :data="item.questionItemList" v-if="item.type === '单选题' || item.type === '多选题'">
                            <el-table-column label="题目选项" prop="content"></el-table-column>
                            <el-table-column label="小计" prop="count"></el-table-column>
                            <el-table-column label="比例">
                                <template v-slot="scope">
                                    <el-progress :text-inside="true" :stroke-width="20" :color="randomColor"
                                                 :percentage="scope.row.percentage"></el-progress>
                                </template>
                            </el-table-column>
                        </el-table>

                        <el-table :data="item.questionItemList" v-else>
                            <el-table-column label="内容" prop="content"></el-table-column>
                        </el-table>

                    </div>
                    <div style="margin-top: 10px"><strong>有效填写: {{ item.count }}</strong></div>

                </div>
            </div>
            <div v-else>未收集到该问卷结果</div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="pageCountVisible =false">确定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="分享" :visible.sync="shareVisiable" width="40%" :close-on-click-modal="false"
                   destroy-on-close>
            <div style="display: flex; align-items: center">
                <span style="margin-right: 5px">链接</span>
                <el-input v-model="link" style="flex: 1; margin-right: 5px"/>
                <el-button type="primary" @click="copyLink">复制</el-button>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="shareVisible = false">关 闭</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>

export default {
    name: "FrontPages",
    data() {
        return {
            name: null,
            tableData: [],  // 所有的数据
            pageNum: 1,   // 当前的页码
            pageSize: 2,  // 每页显示的个数
            total: 0,
            fromVisible: false,
            pageCountVisible: false,
            shareVisiable: false,
            link: '',
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
            pageCount: {},
        }
    },
    created() {
        this.load(1)
    },
    // methods：本页面所有的点击事件或者其他函数定义区
    methods: {
        copy(pageId) {
            this.$request.post('/pages/copy?pageId=' + pageId).then(res => {
                if (res.code === '200') {
                    this.$message.success("复制成功！")
                    this.load(1)

                } else {
                    this.$message.error(res.msg)
                }
            })
        },
        randomColor() {
            let arr = ['#f56c6c', '#e6a23c', '#5cb87a', '#1989fa', '#6f7ad3']
            return arr[Math.floor(Math.random() * arr.length)]  //  0.8 * 1  = 0
        },
        //复制函数
        copyLink() {
            let _input = document.createElement("input");   // 直接构建input

            _input.value = this.link;  // 设置内容
            document.body.appendChild(_input);    // 添加临时实例
            _input.select();   // 选择实例内容
            document.execCommand("Copy");   // 执行复制
            document.body.removeChild(_input)
            this.$message.success("复制成功")
        },
        share(pageId) {
            this.link = location.href.substring(0, location.href.indexOf('/front')) + '/realPage?pageId=' + pageId
            this.shareVisiable = true
        },
        getPageChart(pageId) {

            window.open('/front/chart?pageId=' + pageId )
        },
        //获取问卷结果数量
        getPageCount(pageId) {
            this.$request.get('/pageCount', {
                params: {
                    pageId: pageId
                }
            }).then(res => {
                this.pageCount = res.data || {}
                this.pageCountVisible = true
            })
        },

        del(pageId) {   // 单个删除
            this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
                this.$request.delete('/pages/delete/' + pageId).then(res => {
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
        design(pageId) {
            window.open('/front/design?pageId=' + pageId)
        },
        handleAdd() {   // 新增数据
            this.form = {}  // 新增数据的时候清空数据
            this.fromVisible = true   // 打开弹窗
        },
        handleEdit(row) {   // 编辑数据
            this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
            this.fromVisible = true   // 打开弹窗
        },
        load(pageNum) {  // 分页查询
            if (pageNum) this.pageNum = pageNum
            this.$request.get('/pages/selectPage', {
                params: {
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                    name: this.name,
                }
            }).then(res => {
                this.tableData = res.data?.list
                this.total = res.data?.total
            })
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
        reset() {
            this.name = null
            this.load(1)
        },
        handleCurrentChange(pageNum) {
            this.load(pageNum)
        },
        handleImgSuccess(res) {
            // 将后台返回的图片url赋值给表单属性
            this.form.img = res.data
        }
    }
}
</script>

<style>
body {
    background-color: #dac9c9;
}

.card {
    padding: 20px;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.1);
}

</style>