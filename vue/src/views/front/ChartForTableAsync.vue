<template>
    <div class="main-content">

        <div style="display: flex;margin:20px 6%;">
            <div class="title" style="width:100%;height: 500px;font-size:30px;background-color: #fff;border-radius: 20px;">
                <div style="margin:20px 43%;">
                    <strong>AI智能分析</strong>

                </div>
                <el-divider></el-divider>

                <div style="margin-left: 30%;">
                    <el-form ref="form" :model="form" label-width="80px"
                             style="margin-left: 10px;margin-right: 20px;width: 60%;"
                             :rules="rules">
                        <el-form-item label="分析目标" prop="goal">
                            <el-input placeholder="请输入需要分析的内容" v-model="form.goal"></el-input>
                        </el-form-item>
                        <el-form-item label="图表名称" prop="name">
                            <el-input placeholder="请输入图表名称" v-model="form.name"></el-input>
                        </el-form-item>

                        <el-form-item label="上传图表">
                            <el-upload :show-file-list="false" style="margin-left:20px;display: inline-block; margin-left: 10px" :action="$baseUrl + '/fileOperation/upload'" :headers="{ token: user.token }" :on-success="handleImport" >
                                <el-button type="info" plain>上传文件</el-button>
                            </el-upload>

                        </el-form-item>
                        <el-form-item label="图表类型" prop="type">
                            <el-select v-model="form.type" placeholder="请选择图表类型">
                                <el-option value="柱形图" label="柱形图"></el-option>
                                <el-option value="折线图" label="折线图"></el-option>
                                <el-option value="饼图" label="饼图"></el-option>
                                <el-option value="散点图" label="散点图"></el-option>
                                <el-option value="雷达图" label="雷达图"></el-option>
                                <el-option value="K线图" label="K线图"></el-option>
                                <el-option value="盒须图" label="盒须图"></el-option>
                                <el-option value="旭日图" label="旭日图"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button style="margin:60px; background-color: #333; border-color: #333; color: white" type="primary"
                                       @click="getPageChart">提 交
                            </el-button>
                            <el-button style=" background-color: #333; border-color: #333; color: white" @click="reset">
                                重 置
                            </el-button>
                        </el-form-item>

                    </el-form>
                </div>

            </div>
        </div>
    </div>

</template>

<script>
import * as echarts from 'echarts'



export default {
    name: "ChartForTableAsync",
    data() {
        return {
            fromVisible: false,
            form: {},
            backendData: '',
            goalNameType: [],
            resultData:'',
            user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
            rules: {
                goal: [
                    {required: true, message: '请输入分析目标', trigger: 'blur'},
                ],
                name: [
                    {required: true, message: '请输入问卷名称', trigger: 'blur'},
                ],
                type: [
                    {required: true, message: '请选择图表类型', trigger: 'blur'},
                ],
            },
        }

    },
    mounted() {
        let chartDom = document.getElementById("aiOption");
        this.myChart = echarts.init(chartDom);
        this.myChart.setOption(chartOption)

    },
    // methods：本页面所有的点击事件或者其他函数定义区
    methods: {
        handleImport(res) {
            if (res.code === '200') {  // 成功
                this.$message.success('上传成功')
                console.log("res.data="+res.data)
                this.resultData=res.data;
            } else {
                this.$message.error(res.msg)
            }
        },
        getData(data) {
            data=this.resultData
            this.$request.post('/chart/createTable/async?fileName='+data,{
                goalNameType: this.goalNameType,
            }).then(res =>{
                if (res.code === '200') {
                    this.$message.success("AI正在分析中...，请前往我的AI图表进行查看");
                    console.log("res.data=",res.data)
                } else {
                    this.$message.error(res.msg);
                }
                console.log(res.data)
            }).catch(error => {
                console.error("请求出错:", error);
                this.$message.error("请求失败");
            });
        },

        getPageChart() {
            if (this.form.type != null && this.form.goal != null && this.form.name != null) {
                this.goalNameType = [this.form.goal, this.form.name, this.form.type]
                // 获取当前页面的 URL 参数部分
                this.getData()
            } else {
                this.$message.error("参数有误")

            }
            console.log("---------------")
            console.log(this.form.goal)
            console.log(this.form.name)
            console.log(this.form.type)
            console.log("--------------")

        },
        reset() {
            this.form.goal = null
            this.form.name = null
            this.form.type = null
        },
    }
}
</script>

<style>
body {
    background-color: #dac9c9;
}
</style>