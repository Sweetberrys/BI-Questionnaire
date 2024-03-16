<template>
    <div class="main-content">
        <div>
            <el-row :gutter="10">
                <el-col shadow="hover" :span="8" v-for="item in pagesList" :key="item.id" style="margin-top:30px;margin-left: 160px">
                    <div class="card" style="margin-bottom: 10px; ">
                        <div style="display: flex; grid-gap: 10px; margin:20px 20px;">
                            <div style="flex: 1; width: 0">
                                <div style="width: 20px;border-radius: 10px;margin-left: -30px;margin-top: -30px;">
                                    <img src="@/assets/imgs/logo.png" alt="" style="width: 100%;border-radius: 10px">
                                </div>
                                <div style="margin-bottom: 10px; font-size: 30px" class="line1"><strong>{{ item.name }}</strong></div>
                                <div style="color: #666;margin-bottom: 10px; font-size: 20px" class="line1"><strong>{{ item.descr }}</strong></div>
                            </div>
                            <div style="width: 110px;border-radius: 10px">
                                <img :src="item.img" alt="" style="width: 100%;border-radius: 10px">
                            </div>
                        </div>

                        <div>
                            <el-button icon="el-icon-mobile" type="warning" size="mini" @click="viewPages(item.id)" style="font-size: 16px;background-color: rgb(25,80,134); border-color: rgb(25,80,134)">点击预览</el-button>
                            <el-button icon="el-icon-mouse" type="primary" size="mini" @click="copy(item.id)" style="font-size: 16px;background-color: rgb(206,13,13); border-color: rgb(206,13,13)">点击使用</el-button>
                            <div style="color: #c5315e;margin-left:335px;margin-top: -36px; font-size: 16px" class="line1"><strong>被使用次数：{{ item.count }}</strong></div>
                            <div style="color: #4f855a;margin-left:350px;margin-top: 0px;margin-bottom: 5px; font-size: 16px" class="line1">{{ item.createTime }}</div>
                        </div>
                    </div>
                </el-col>
            </el-row>


        </div>


        <div style="margin-left: 15%;color: white" >
            <el-pagination
                background="white"
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

export default {
    name:"Model",
    data() {
        return {
            pagesList: [],
            tableData: [],  // 所有的数据
            pageNum: 1,   // 当前的页码
            pageSize: 4,  // 每页显示的个数
            total: 0,
        }
    },
    mounted() {


    },
    created() {

        this.load(1)

    },
    // methods：本页面所有的点击事件或者其他函数定义区
    methods: {
        copy(pageId){
            this.$request.post('/pages/copy?pageId='+pageId).then(res=>{
                if(res.code === '200'){
                    this.$message.success("复制成功")

                    setTimeout(()=>{
                        location.href='/front/design?pageId='+res.data
                        console.log(res.data)
                    },500)

                }else {
                    this.$message.error(res.msg)
                }
            })
        },
        //传递参数展示
        viewPages(pageId){
            window.open('/front/viewPages?pageId='+pageId)
        },

        load(pageNum) {
            if (pageNum) this.pageNum = pageNum
            this.$request.get('/pages/selectPageByQuestion', {
                params: {
                    //包含open为是的数据
                    open: '是' ,
                    pageNum: this.pageNum,
                    pageSize: this.pageSize,
                }
            }).then(res => {
                console.log("res=",res.data)
                this.tableData = res.data?.list
                this.total = res.data?.total

                // 更新pagesList以展示当前页的内容
                this.pagesList = this.tableData  // 或者使用.concat()方法等
                console.log("this.pagesList=",this.pagesList)
            })
        },
        reset() {
            this.load(1)
        },
        handleCurrentChange(pageNum) {
            this.load(pageNum)
        },
    }

}
</script>

<style>
body{
    background-color: #dac9c9;
}
.card {
    padding: 20px;
    background-color: #fff;
    border-radius: 5px;
    box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.1);
}
.line1 {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

</style>