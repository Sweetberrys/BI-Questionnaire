<template>
    <div class="main-content">
        <div class="card" style="width: 70%;height:100%;margin:20px auto;background-color:#72767b;border-radius: 20px">
            <div style="text-align: center;color:#2d2929;font-weight:bold;font-size:35px;width:50%;margin:20px auto;border-bottom: 3px solid #2d2929">
                {{ pages.name }}
            </div>

            <div style="padding: 10px 0">
                <div v-for="(item, index) in questionList" :key="item.id" style="margin-bottom: 15px">
                    <div style="text-align: left;color:#2d2929;font-weight:bold;font-size:20px;width:50%;margin:20px auto;">
                        <span>{{ index + 1 }}、</span>
                        <span style="margin-right: 10px">{{ item.name }}</span>
                        <el-tag v-if="item.type === '单选题'" type="primary">(单选)</el-tag>
                        <el-tag v-if="item.type === '多选题'" type="success">(多选)</el-tag>
                        <el-tag v-if="item.type === '填空题'" type="warning">(填空)</el-tag>
                    </div>
                    <!--遍历内容-->
                    <div style="text-align: left;width:50%;margin:20px auto;border-bottom: 1px solid #2d2929;">

                        <!--选择题有内容-->
                        <div style="text-align: left;color:#2d2929;padding-left:20px;margin:20px auto;"
                             v-for="sub in item.questionItemList" :key="sub.id">
                            <div v-if="item.type === '单选题'">
                                <el-radio :label="sub.content" v-model="item.answer" style="color: #2d2929;"
                                          border></el-radio>
                            </div>
                        </div>
                        <el-checkbox-group v-model="item.answer" v-if="item.type === '多选题'">
                            <div style="text-align: left;color:#2d2929;padding-left:20px;margin:20px auto;"
                                 v-for="sub in item.questionItemList" :key="sub.id">
                                <el-checkbox :label="sub.content" style="color: #2d2929;" border></el-checkbox>
                            </div>
                        </el-checkbox-group>

                        <div v-if="item.type === '填空题'">
                            <el-input type="textarea" v-model="item.answer"></el-input>
                        </div>
                    </div>
                </div>

                <!--  关闭按钮  -->
                <div style="text-align: center">
                    <el-button size="medium" type="primary" @click="submitAnswer">提 交</el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "RealPage",
    data() {
        return {
            pages: {},
            questionList: [],
        }
    },
    created() {
        this.load()
    },
    methods: {
        //提交数据
        submitAnswer() {
            //进行校验
            for (let i = 0; i < this.questionList.length; i++) {
                if (!this.questionList[i].answer) {
                    this.$message.warning('请填写完问卷再提交')
                    return
                }
            }
            let no = new Date().getTime()
            //截取提交信息
            let answerList = []
            this.questionList.forEach(item => {
                let content = item.answer
                if (item.type === '多选题') {
                    content = JSON.stringify(content)   // 把数组转换成字符串存到数据库
                }
                answerList.push({ questionId: item.id, content: content, pageId: item.pageId, no: no+''})
            })
            this.$request.post('/answer/addBatch', answerList).then(res => {
                if (res.code === '200') {  // 表示成功保存
                    this.$message.success('提交成功')
                    setTimeout(() => {
                        location.href = '/thanks'
                    }, 500)
                } else {
                    this.$message.error(res.msg)  // 弹出错误的信息
                }
            })
        },
        load() {
            let pageId = this.$route.query.pageId
            this.$request.get('/pages/selectById/' + pageId).then(res => {
                this.pages = res.data || {}
            })

            this.$request.get('/question/selectByPageId', {
                params: {
                    pageId: pageId
                }
            }).then(res => {
                this.questionList = res.data || []

                //解决多选时，值为true/false
                this.questionList.forEach(item => {
                    if (item.type === '多选题') {
                        this.$set(item, 'answer', [])
                    } else {
                        this.$set(item, 'answer', '')
                    }
                })
            })
        }

    }
}
</script>

<style scoped>

</style>
<style>

body {
    background-color: #dac9c9;
}

</style>