<template>
    <div class="main-content">

        <div style="display: flex; margin-left: 40%; margin-top: 20px; margin-right: 2%;">
            <div class="title"
                 style="width: 40%; height: 50%; font-size: 30px; background-color: #fff; border-radius: 20px;">
                <div style="margin-top: 20px; margin-bottom: 20px; margin-left: 100px;">
                    <strong>AI智能分析</strong>
                </div>
            </div>
        </div>

        <el-row :gutter="10">
            <el-col shadow="hover" :span="8" v-for="item in pagesList" :key="item.id"
                    style="margin-top: 30px; margin-left: 4%; width: 44%">
                <div class="text-title"
                     style="font-size: 20px; margin: 20px 2%; width: 100%; background-color: #fff; border-radius: 20px;">

                    <div style="">
                        <div style="padding-top:20px;margin-left: 4%">
                            <strong style="">《{{ item.goal }}》</strong>

                        </div>
                        <el-divider></el-divider>

                        <div style="margin-left: 5%">
                            <strong style="">AI分析{{ item.name }}结论</strong>
                        </div>
                        <el-divider></el-divider>

                    </div>

                    <div style="margin: 20px 20px; word-wrap: break-word; font-size: 20px;">
                        <div v-if="isChartDataValid(item)">
                            {{ item.genResult }}
                            <br/>
                            <strong style="font-size:18px;margin-left: 70%">{{ formatTime(item.createTime) }}</strong>
                            <br/>
                            <br/>
                        </div>
                        <div v-else>
                            <el-skeleton :rows="6" style="width: 100%;margin-bottom:20px;"></el-skeleton>
                        </div>
                    </div>
                </div>

                <div class="table-title"
                     style="margin-left: 2%; width: 100%; background-color: #fff; border-radius: 20px;">
                    <div class="box-title"
                         style="padding-top:20px;border-radius: 3px; margin-left: 4%; font-size: 20px; width: 95%;">
                        <strong>AI可视化图表</strong>
                    </div>
                    <div style="margin-left: 40px; margin-top: 20px; display: flex; grid-gap: 10px">
                        <div style="flex: 1">
                            <div v-if="isChartDataValid(item)">

                                <div ref="aiOption" style="height: 400px; font-size: 20px;">

                                </div>
                            </div>
                            <div v-else>
                                <el-skeleton :rows="6" style="margin-left:-20px;margin-bottom:20px;width: 100%;"></el-skeleton>
                            </div>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <div style="margin-top:20px;margin-left: 35%; color: white">
            <el-pagination
                :background="true"
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5, 10, 20]"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total"
            >
            </el-pagination>
        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
    name: "MyChart2.0",
    data() {
        return {
            pagesList: [],
            tableData: [],
            pageNum: 1,
            pageSize: 2,
            total: 0,
            form: {},
            backendData: '',
            myChart: null,
            myCharts: [],
            user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
        }
    },
    mounted() {
        this.load(1).then(() => {
            this.initChart(); // 确保数据加载完成后再初始化图表
        });
    },
    watch: {
        tableData() { // 监听tableData的变化
            if (!this.myChart && this.tableData.length > 0) {
                this.initChart();
            } else if (this.myChart && this.chartDataLoaded) {
                // 如果图表已初始化且数据已加载，则更新图表
                this.updateChart();
            }
        }
    },
    methods: {
        formatTime(timestamp) {
            const date = new Date(timestamp);
            return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
        },
        load(pageNum) {
            // 创建一个 Promise 来处理异步请求
            return new Promise((resolve, reject) => {
                if (pageNum) this.pageNum = pageNum;
                this.$request.get('/chart/selectPage', {
                    params: {
                        pageNum: this.pageNum,
                        pageSize: this.pageSize,
                    }
                }).then(response => {
                    // 数据加载成功
                    this.tableData = response.data?.list || []; // 确保tableData是一个数组
                    this.total = response.data?.total || 0; // 确保total是一个数字
                    this.pagesList = this.tableData; // 更新pagesList
                    this.chartDataLoaded = true; // 设置图表数据已加载的标志
                    console.log("this.pagesList=" + this.pagesList)
                    resolve(response); // 数据加载完成后 resolve 这个 Promise
                }).catch(error => {
                    // 数据加载失败
                    console.error('数据加载失败:', error);
                    this.chartDataLoaded = false; // 清除图表数据已加载的标志
                    reject(error); // 如果有错误，则 reject 这个 Promise
                });
            });
        },
        isChartDataValid(item) {
            try {
                JSON.parse(item.genChart); // 尝试解析genChart，如果失败则返回false
                return true; // 如果解析成功，则返回true
            } catch (error) {
                return false; // 如果解析失败，则返回false
            }
        },
        handleCurrentChange(pageNum) {
            this.load(pageNum).then(() => {
                this.initChart(); // 重新初始化图表
            });
        },
        // 修改 initChart 方法，确保对于每个图表都能正确初始化
        // 修改 initChart 方法，将图表实例保存到 myCharts 数组中
        initChart() {
            if (this.$refs.aiOption && this.$refs.aiOption.length > 0) {
                this.myCharts = this.$refs.aiOption.map(element => {
                    return echarts.init(element);
                });
                // 现在您可以遍历 myCharts 数组来更新图表
                this.myCharts.forEach((chart, index) => {
                    this.updateChart(chart, index);
                });
            } else {
                console.error('无法找到 aiOption 元素');
            }
            this.myCharts.forEach((chart, index) => {
                this.updateChart(chart, index);
            });
        },
        // 修改 updateChart 方法，确保根据索引更新正确的图表
        // 修改 updateChart 方法，确保根据索引更新正确的图表
        updateChart(chart, index) {
            try {
                if (chart && this.tableData[index]?.genChart) {
                    const genChart = JSON.parse(this.tableData[index].genChart);
                    chart.setOption(genChart);
                } else {
                    console.error("genChart 为空或不存在");
                    this.$message.error("图表配置为空或不存在");
                }
            } catch (error) {
                if (error instanceof SyntaxError) {
                    console.error("genChart 数据不是一个有效的 JSON 字符串:", error.message);
                    this.$message.error("图表配置数据无效");
                } else {
                    console.error("更新图表时发生错误:", error);
                    this.$message.error("更新图表时发生未知错误");
                }
            }
        }

    },
};
</script>

<style>
body {
    background-color: #dac9c9;
}
</style>
