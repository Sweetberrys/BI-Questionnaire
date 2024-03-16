<template>
    <div class="main-content">

        <div class="big-box"
             style="padding-bottom:10px;border-radius:20px;height:50%;width: 32%;margin: 30px 3%;background-color:#fff;">
            <div class="box-title" style="border-radius:3px;font-size:25px;margin:0px 32%;padding:21px;width: 58.3%">
                <strong>AI智能分析</strong>
            </div>
            <el-form ref="form" :model="form" label-width="80px" style="margin-left: 10px;margin-right: 20px"
                     :rules="rules">
                <el-form-item label="分析目标" prop="goal">
                    <el-input placeholder="请输入需要分析的内容" v-model="form.goal"></el-input>
                </el-form-item>
                <el-form-item label="问卷名称" prop="name">
                    <el-input placeholder="请输入问卷名称" v-model="form.name"></el-input>
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
                    <el-button style=" background-color: #333; border-color: #333; color: white" type="primary"
                               @click="getPageChart">提 交
                    </el-button>
                    <el-button style=" background-color: #333; border-color: #333; color: white" @click="reset">重 置
                    </el-button>
                </el-form-item>


            </el-form>
            <div class="box-title" style="border-radius:3px;font-size:20px;margin-left:20px;padding:21px;width: 95%">
                <strong>AI分析结论</strong>
                <div>{{ backendData }}</div>
            </div>

        </div>

        <div class="big-box" style="padding-bottom:10px;border-radius:20px;height:80%;width: 63%;margin-left: 36%;margin-top:-25.2%;background-color:#fff;">
            <div class="box-title" style="border-radius:3px;font-size:25px;margin-left:10px;padding:11px;width: 98.3%;">
                <strong>AI可视化图表</strong>
                <div style="margin-left: 40px;margin-top: 80px; display: flex; grid-gap: 10px">
                    <div style="flex: 1">
                        <div style="height: 400px" id="aiOption"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</template>

<script>
import * as echarts from 'echarts'
let chartOption={};
// let chartOption={
//     "toolbox": {
//         "show": true,
//         "feature": {
//             "dataView": { "show": true, "readOnly": false },
//             "magicType": { "show": true, "type": ["pie"] },
//             "restore": { "show": true },
//             "saveAsImage": { "show": true }
//         }
//     },
//     "series": [
//         {
//             "type": "pie",
//             "data": [
//                 { "name": "星期一", "value": 2 },
//                 { "name": "星期二", "value": 1 },
//                 { "name": "星期三", "value": 0 },
//                 { "name": "星期四", "value": 0 }
//             ]
//         }
//     ]
// }


export default {
    name: "Chart",
    data() {
        return {
            fromVisible: false,
            form: {},
            backendData: '',
            questionGoalNameType: [],
            myChart: '',
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
        getData(pageId) {
            this.$request.get('/pageCount', {
                params: {
                    pageId: pageId
                }
            }).then(res => {
                this.pageCount = res.data || {};
                this.$request.post('/chart/gen', {
                    answerCount: this.pageCount,
                    questionGoalNameType: this.questionGoalNameType
                }).then(res => {
                    if (res.code === '200') {
                        this.$message.success("提交成功！AI正在分析中...");
                        this.backendData = res.data[1];
                        // res.data[0]是一个JSON字符串，需要将其解析为对象
                        try {
                            this.chartOption = JSON.parse(res.data[0]);
                            this.myChart.setOption(this.chartOption); // 使用解析后的对象更新图表
                        } catch (error) {
                            console.error("解析chartOption时出错:", error);
                            this.$message.error("图表配置解析失败");
                        }
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            }).catch(error => {
                console.error("请求出错:", error);
                this.$message.error("请求失败");
            });
        },

        getPageChart() {
            if(this.form.type != null && this.form.goal != null&& this.form.name != null){
                this.questionGoalNameType = [this.form.goal, this.form.name, this.form.type]
                // 获取当前页面的 URL 参数部分
                const urlParams = new URLSearchParams(window.location.search);
                // 获取特定参数的值，比如 pageId
                let pageId = urlParams.get('pageId');
                this.getData(pageId)
            }else{
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