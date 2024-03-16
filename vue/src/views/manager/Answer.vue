<template>
  <div>
    <div class="search">
        <el-button type="warning" plain @click="delBatch">批量删除</el-button>

        <el-input placeholder="请输入问卷名称查询" style="margin-left:28%;width: 200px;margin-right: 10px" v-model="pageName"></el-input>
        <el-input placeholder="请输入题目名称查询" style="width: 200px;margin-right: 10px" v-model="questionName"></el-input>
      <el-input placeholder="请输入内容查询" style="width: 200px" v-model="content"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>


    <div class="table">
        <el-table :data="tableData" strip @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center"></el-table-column>
            <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
            <el-table-column prop="questionId" label="题目ID"></el-table-column>
            <el-table-column prop="pageId" label="问卷ID"></el-table-column>
            <el-table-column prop="pageName" label="问卷名称"></el-table-column>
            <el-table-column prop="questionName" label="问题名称"></el-table-column>
            <el-table-column prop="content" label="答题内容"></el-table-column>
            <el-table-column prop="userId" label="问卷创建人ID"></el-table-column>
            <el-table-column prop="userName" label="问卷创建人"></el-table-column>
            <el-table-column prop="createTime" label="答题时间"></el-table-column>
            <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                    <el-button type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
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


    <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
        <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
            <el-form-item label="问卷ID" prop="pageId">
                <el-input v-model="form.pageId" placeholder="问卷ID"></el-input>
            </el-form-item>
            <el-form-item label="题目ID" prop="questionId">
                <el-input v-model="form.questionId" placeholder="题目ID"></el-input>
            </el-form-item>
            <el-form-item label="内容" prop="content">
                <el-input v-model="form.content" placeholder="内容"></el-input>
            </el-form-item>
            <el-form-item label="调查编号" prop="content">
                <el-input v-model="form.no" placeholder="调查编号"></el-input>
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
  name: "Answer",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      content: null,
        pageName: null,
        questionName:null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        // title: [
        //   {required: true, message: '请输入标题', trigger: 'blur'},
        // ],
        // content: [
        //   {required: true, message: '请输入内容', trigger: 'blur'},
        // ]
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
            url: this.form.id ? '/answer/update' : '/answer/add',
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
        this.$request.delete('/answer/delete/' + id).then(res => {
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
        this.$request.delete('/answer/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/answer/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          content: this.content,
          pageName: this.pageName,
          questionName: this.questionName,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.content = null
      this.pageName = null
      this.questionName = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
