<template>
  <div>
    <!-- 面包屑导航区域 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>新闻管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 卡片视图区域 -->
    <el-card>
      <!-- 搜索与添加区域 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="8">
          <el-input placeholder="请输入内容" v-model="queryInfo.searchContent" clearable @keyup.enter.native="getNewsList">
            <el-button slot="append" icon="el-icon-search" @click="getNewsList"></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="showDialogVisible">添加新闻</el-button>
        </el-col>
      </el-row>

      <el-card style="width: 50%;">
        <div>
          <label>类别:</label><br>
          <el-radio-group v-model="queryInfo.category" @change="screenNews">
            <el-radio-button label="空"></el-radio-button>
            <el-radio-button v-for="(item,index) in options" :key="index" :label="item.label" style="margin: 15px 0;">
            </el-radio-button>
          </el-radio-group>
        </div>
        <!-- <el-button type="primary" @click="screenNews">筛选</el-button> -->
      </el-card>

      <!-- 列表区域 -->
      <el-table :data="newsList" border stripe>
        <el-table-column type="index"></el-table-column>
        <el-table-column label="类别" prop="category"></el-table-column>
        <el-table-column label="标题" prop="title"></el-table-column>
        <el-table-column label="发布日期" prop="publicDate"></el-table-column>
        <el-table-column label="操作" width="180px">
          <template slot-scope="scope">
            <!-- 修改按钮 -->
            <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.id)"></el-button>
            <!-- 删除按钮 -->
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="showDeleteDialog(scope.row.id)">
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="queryInfo.pageNum" :page-sizes="[1, 2, 5, 10]" :page-size="queryInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </el-card>

    <!-- 添加的对话框 -->
    <el-dialog title="添加" :visible.sync="addDialogVisible" width="50%" @close="addDialogClosed">
      <!-- 内容主体区域 -->
      <el-form :model="addForm" ref="addFormRef" label-width="70px">
        <el-form-item prop="id">
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="addForm.title"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="分类" prop="category">
          <el-select v-model="addForm.category" placeholder="请选择">
            <el-option v-for="(item,index) in options" :key="index" :label="item.label" :value="item.label">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="synopsis" v-if="addForm.category !== '典型项目'">
          <el-input type="textarea" v-model="addForm.synopsis"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <label>（图片分辨率推荐800 * 450 不得过大！请自行准备符合要求的图片）</label>
          <quill-editor class="ql-editor" v-model="addForm.content" :options="editorOption">
          </quill-editor>
        </el-form-item>
        <el-form-item label="发布时间" prop="publicDate">
          <el-date-picker v-model="addForm.publicDate" type="date" format="yyyy-MM-dd" placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="封面上传" prop="filePath">
          <el-upload class="upload-demo" :action="uploadUrl" :on-success="handleSuccess" :show-file-list="false">
            <el-row>
              <el-col :span="20">
                <el-input v-model="addForm.filePath"></el-input>
              </el-col>
              <el-col :span="4">
                <el-button size="small" type="primary">点击上传</el-button>
              </el-col>
            </el-row>
          </el-upload>
        </el-form-item>
        <el-form-item label="附件上传" prop="appendix.filePath">
          <el-upload class="upload-demo" :action="uploadAppendix" :on-success="appendixHandleSuccess"
            :show-file-list="false">
            <el-row>
              <el-col :span="20">
                <el-input v-model="addForm.appendix.filePath"></el-input>
              </el-col>
              <el-col :span="4">
                <el-button size="small" type="primary">点击上传</el-button>
              </el-col>
            </el-row>
            <label size="small" type="primary">(以上传的附件的名称为标题，请提前修改正确的附件名称)</label>
          </el-upload>
        </el-form-item>
        <!-- <a @click="download">下载</a> -->
        <!-- <a :href="'http://40.73.72.56:1311/newsManagement/download?filePath=' + addForm.appendixPath">下载</a> -->
      </el-form>
      <!-- 底部区域 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addProjectNews">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
  export default {

    data() {
      return {
        // 获取列表的参数对象
        queryInfo: {
          searchContent: '',
          pageNum: 1,
          pageSize: 10,
          category: ''
        },
        total: 0,
        // 新闻列表
        newsList: [],
        addForm: {
          title: '',
          content: '',
          category: '',
          publicDate: '',
          filePath: '',
          synopsis: '',
          appendix: {
            title: '',
            filePath: ''
          }
        },
        // excel 上传
        uploadUrl: 'http://40.73.72.56:1311/newsManagement/upload',
        // 附件上传
        uploadAppendix: 'http://40.73.72.56:1311/newsManagement/uploadAppendix',
        // 控制添加新闻对话框的显示与隐藏
        addDialogVisible: false,
        options: [{
            label: '企业新闻',
            value: 'cNews'
          },
          {
            label: '典型项目',
            value: 'project'
          },
          {
            label: '最新政策',
            value: 'nNews'
          }
        ],
        // 富文本编辑器
        editorOption: {
          modules: {
            toolbar: [
              ['bold', 'italic', 'underline'], // toggled buttons
              ['blockquote', 'code-block'],
              ['image'],
              [{
                header: 1
              }, {
                header: 2
              }], // custom button values
              [{
                list: 'ordered'
              }, {
                list: 'bullet'
              }],
              [{
                script: 'sub'
              }, {
                script: 'super'
              }], // superscript/subscript
              [{
                indent: '-1'
              }, {
                indent: '+1'
              }], // outdent/indent
              [{
                direction: 'rtl'
              }], // text direction

              [{
                size: ['small', false, 'large', 'huge']
              }], // custom dropdown
              // [{
              //   size: ['10px', '14px', '18px', '22px', '26px', '32px', '48px']
              // }],
              [{
                header: [1, 2, 3, 4, 5, 6, false]
              }],

              [{
                color: []
              }, {
                background: []
              }], // dropdown with defaults from theme
              [{
                font: []
              }],
              [{
                align: []
              }],

              ['clean']
            ]
          }
        }

      }
    },
    // 页面加载完毕后触发的事件
    mounted() {
      this.addForm.publicDate = new Date() // 默认显示为当天时间     
    },
    created() {
      this.getNewsList()
    },
    methods: {
      // 获取新闻列表
      async getNewsList() {
        if (this.queryInfo.searchContent !== '') {
          this.queryInfo.pageNum = 1
        }
        const {
          data: res
        } = await this.$http.get('http://40.73.72.56:1311/newsManagement/news', {
          params: this.queryInfo
        })
        this.newsList = res.data
        this.total = res.totalPage
      },
      // 翻页
      async getNewsList1() {
        const {
          data: res
        } = await this.$http.get('http://40.73.72.56:1311/newsManagement/news', {
          params: this.queryInfo
        })
        this.newsList = res.data
        this.total = res.totalPage
      },
      showDialogVisible() {
        this.addDialogVisible = true
        if (this.addForm.id !== undefined) {
          return this.$message.error('操作失败,请重试')
        }
      },
      // 添加/修改
      async addProjectNews() {
        const {
          data: res
        } = await this.$http.post('http://40.73.72.56:1311/newsManagement/add', this.addForm)
        if (res.status !== 0) {
          return this.$message.error('操作失败')
        }
        // this.addForm.publicDate = this.getDate()
        this.addDialogVisible = false
        this.$message.success('操作成功')
        // this.getNewsList()
        location.reload()
      },
      // 修改
      async showEditDialog(id) {
        this.addDialogVisible = true
        const {
          data: res
        } = await this.$http.get('http://40.73.72.56:1311/newsManagement/getById', {
          params: {
            id: id
          }
        })
        this.addForm = res.data
      },
      showDeleteDialog(id) {
        this.deleteDialogVisible = true
        this.$confirm('此操作将永久删除, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          const {
            data: res
          } = await this.$http.get('http://40.73.72.56:1311/newsManagement/delete', {
            params: {
              id: id
            }
          })
          if (res.status !== 0) {
            return this.$message.error('请求失败')
          }
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          // this.getNewsList()
          location.reload()
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      },
      async screenNews() {
        if (this.queryInfo.category !== '空') {
          this.getNewsList()
        } else {
          this.queryInfo.category = ''
          this.getNewsList()
        }
      },
      // 监听 pagesize 改变的事件
      handleSizeChange(newSize) {
        // console.log(newSize)
        this.queryInfo.pageSize = newSize
        this.getNewsList1()
      },
      // 监听 页码值 改变的事件
      handleCurrentChange(newPage) {
        this.queryInfo.pageNum = newPage
        this.getNewsList1()
      },
      // 监听添加对话框的关闭事件
      addDialogClosed() {
        // 使用 validate、resetFields 方法时列表必须有prop属性
        this.$refs.addFormRef.resetFields()
      },
      // 监听文件上传成功的钩子函数
      handleSuccess(response) {
        this.$message.success('上传成功')
        const path = response.data
        this.addForm.filePath = path
      },
      // 附件上传成功钩子函数
      appendixHandleSuccess(response) {
        this.$message.success('上传成功')
        const path = response.data.filePath
        this.addForm.appendix.filePath = path
        this.addForm.appendix.title = response.data.title
      },
      getDate() {
        const date = new Date()
        const seperator1 = '-'
        const year = date.getFullYear()
        let month = date.getMonth() + 1
        let strDate = date.getDate()
        const hours = date.getHours()
        const minutes = date.getMinutes()
        const seconds = date.getSeconds()
        if (month >= 1 && month <= 9) {
          month = '0' + month
        }
        if (strDate >= 0 && strDate <= 9) {
          strDate = '0' + strDate
        }
        const currentDate = year + seperator1 + month + seperator1 + strDate + '  ' + hours + ':' + minutes + ':' +
          seconds
        return currentDate
      }
    }
  }

</script>

<style lang="less" scoped>
  .el-button {
    margin-left: 15px;
  }

</style>
