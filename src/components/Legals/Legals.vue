<template>
  <div>
    <!-- 面包屑导航区域 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>法律法规管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 卡片视图区域 -->
    <el-card>
      <!-- 搜索与添加区域 -->
      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="8">
          <el-input placeholder="请输入内容" v-model="queryInfo.searchContent" clearable @keyup.enter.native="getLegalList">
            <el-button slot="append" icon="el-icon-search" @click="getLegalList"></el-button>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="addDialogVisible = true">添加法律法规</el-button>
        </el-col>
      </el-row>

      <!-- <el-row>
        <el-col>
          <el-radio-group v-model="radio2" size="medium">
            <el-radio-button :label="item" v-for="(item,index) in categories[0].specific" :key="index"></el-radio-button>
          </el-radio-group>
        </el-col>
      </el-row> -->

      <el-card style="width: 50%;">
        <div v-for="(item,index) in categories" :key="index">
          <label>{{item.name}}:</label><br>
          <el-radio-group v-model="filter[index]" size="small" style="margin: 15px 0;">
            <el-radio-button label="空"></el-radio-button>
            <el-radio-button :label="specific" v-for="(specific,index1) in item.specific" :key="index1">
            </el-radio-button>
          </el-radio-group>
        </div>
        <el-button type="primary" @click="screenLegals">筛选</el-button>
      </el-card>

      <!-- 列表区域 -->
      <el-table :data="legalList" border stripe>
        <el-table-column type="index"></el-table-column>
        <el-table-column label="标题" prop="title"></el-table-column>
        <el-table-column label="行业分类" prop="category"></el-table-column>
        <el-table-column label="具体分类" prop="specific"></el-table-column>
        <el-table-column label="施行日期" prop="executeDate"></el-table-column>
        <el-table-column label="公布日期" prop="publicDate">
        </el-table-column>
        <el-table-column label="操作" width="180px">
          <template slot-scope="scope">
            <!-- 修改按钮 -->
            <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row.id)"></el-button>
            <!-- 删除按钮 -->
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="showDeleteDialog(scope.row.id)">
            </el-button>
            <!-- 导出按钮 -->
            <!-- <el-tooltip effect="dark" content="导出" placement="top" :enterable="false">
              <el-button type="warning" icon="el-icon-setting" size="mini" @click="XHRLoadLoadFile(scope.row.id)">
              </el-button>
            </el-tooltip> -->
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="queryInfo.pageNum" :page-sizes="[1, 2, 5, 10]" :page-size="queryInfo.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </el-card>

    <!-- 添加法律法规的对话框 -->
    <el-dialog title="添加/修改法律法规" :visible.sync="addDialogVisible" width="50%" @close="addDialogClosed">
      <!-- 内容主体区域 -->
      <el-form :model="addForm" ref="addFormRef" label-width="70px">
        <el-form-item prop="id">
        </el-form-item>
        <el-form-item prop="modifyDate">
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="addForm.title" @blur="checkName(addForm.title)"></el-input>
              <i class="el-icon-error" v-if="titleOnce">该法律法规已存在</i>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="公布机关" prop="office">
              <el-input v-model="addForm.office"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="公布时间" prop="publicDate">
              <el-date-picker v-model="addForm.publicDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="施行时间" prop="executeDate">
              <el-date-picker v-model="addForm.executeDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="效力" prop="efficacy">
          <el-select v-model="addForm.efficacy" placeholder="请选择">
            <el-option label="有效" value="有效"></el-option>
            <el-option label="无效" value="无效"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="基础分类" prop="kind">
          <el-select v-model="addForm.kind" placeholder="请选择">
            <el-option v-for="(item,index) in kinds" :key="index" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="6">
            <el-form-item label="行业分类" prop="category">
              <el-select v-model="addForm.category" placeholder="请选择" clearable>
                <el-option v-for="(item,index) in categories" :key="index" :label="item.name" :value="item.name">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="具体分类" prop="specific" v-if="addForm.category === '综合类'">
              <el-select v-model="addForm.specific" placeholder="请选择" clearable>
                <el-option v-for="(item,index) in categories[0].specific" :key="index" :label="item" :value="item">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="具体分类" prop="specific" v-if="addForm.category === '水路运输'">
              <el-select v-model="addForm.specific" placeholder="请选择" clearable>
                <el-option v-for="(item,index) in categories[1].specific" :key="index" :label="item" :value="item">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="具体分类" prop="specific" v-if="addForm.category === '道路运输'">
              <el-select v-model="addForm.specific" placeholder="请选择" clearable>
                <el-option v-for="(item,index) in categories[2].specific" :key="index" :label="item" :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="简介" prop="synopsis">
          <el-input type="textarea" v-model="addForm.synopsis"></el-input>
        </el-form-item>
        <el-form-item label="文件上传">
          <el-upload class="upload-demo" :action="uploadUrl" :on-success="handleSuccess" :show-file-list="false">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <!-- 底部区域 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addLegal">确 定</el-button>
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
          kind: '全库',
          synthesize: '',
          waterway: '',
          road: '',
          pageSize: 10
        },
        legalList: [],
        total: 0,
        // 控制添加法律法规对话框的显示与隐藏
        addDialogVisible: false,
        // 控制修改对话框的显示与隐藏
        editDialogVisible: false,
        // 控制删除对话框的显示与隐藏
        deleteDialogVisible: false,
        // 添加的表单数据
        addForm: {
          title: '',
          office: '',
          publicDate: '',
          executeDate: '',
          efficacy: '有效',
          kind: '法律法规',
          category: '',
          specific: '',
          synopsis: '',
          filePath: ''
        },
        editForm: {},
        kinds: [
          '法律法规', '标准规范', '文件'
        ],
        categories: [{
            name: '综合类',
            specific: [
              '安全生产', '职业健康', '特种设备'
            ]
          },
          {
            name: '水路运输',
            specific: [
              '港口', '航道', '水路运输管理'
            ]
          },
          {
            name: '道路运输',
            specific: [
              '公路基础设施', '道路运输管理'
            ]
          }
        ],
        addFormRules: {},
        uploadUrl: 'http://40.73.72.56:1311/nacos-provide/upload',
        titleOnce: false,
        filter: [
          '', '', ''
        ],
        tempList: []
      }
    },
    created() {
      this.getLegalList()
    },
    methods: {
      // 添加或修改法律法规
      async addLegal() {
        console.log(this.addForm)
        const {
          data: res
        } = await this.$http.post('addLegal', this.addForm)
        console.log(res)
        if (res.status !== 0) {
          return this.$message.error('操作失败')
        }
        this.addDialogVisible = false
        // this.getLegalList()     
        this.$message.success('操作成功')
        location.reload()
      },
      editLegalInfo() {

      },
      // 修改
      async showEditDialog(id) {
        console.log(id)
        this.addDialogVisible = true
        const {
          data: res
        } = await this.$http.get('getLegal', {
          params: {
            id: id
          }
        })
        this.addForm = res.date
      },
      // 删除
      showDeleteDialog(id) {
        this.deleteDialogVisible = true
        this.$confirm('此操作将永久删除该法律法规, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          const {
            data: res
          } = await this.$http.get('delete', {
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

          // this.getLegalList()
          location.reload()
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      },
      // 获取列表
      async getLegalList() {
        if (this.queryInfo.searchContent !== '') {
          this.queryInfo.pageNum = 1
        }
        const {
          data: res
        } = await this.$http.post('SearchLawTitle',
          this.queryInfo
        )
        this.legalList = res.date
        this.tempList = res.date
        this.total = res.totalPage
      },
      // 翻页
      async getLegalList1() {
        const {
          data: res
        } = await this.$http.post('SearchLawTitle',
          this.queryInfo
        )
        this.legalList = res.date
        this.tempList = res.date
        this.total = res.totalPage
      },
      // 检查标题是否唯一
      async checkName(title) {
        console.log(title)
        if (title === '') {
          return
        }
        const {
          data: res
        } = await this.$http.get('checkTitle', {
          params: {
            title: title
          }
        })
        if (res.status === -2) {
          console.log(res.date)
          for (const legalTitle of res.date) {
            if (title === legalTitle) {
              this.titleOnce = true
              return
            }
            this.titleOnce = false
          }
        }
        if (res.status === 0) {
          this.titleOnce = false
        }
      },
      // 按具体分类筛选法律法规（后台）
      async screenLegals() {
        this.queryInfo.synthesize = this.filter[0]
        this.queryInfo.waterway = this.filter[1]
        this.queryInfo.road = this.filter[2]
        if (this.queryInfo.searchContent !== '' || this.queryInfo.synthesize !== '' || this.queryInfo.waterway !==
          '' || this.queryInfo.road !== '') {
          this.queryInfo.pageNum = 1
        }
        const {
          data: res
        } = await this.$http.post('SearchLawTitle',
          this.queryInfo
        )
        this.legalList = res.date
        // this.tempList = res.date
        this.total = res.totalPage
      },
      // 按具体分类筛选法律法规(前端分析错误)
      screenLegal() {
        // 将a的内容赋值给b，改变b里对象的值，
        // 发现a里的值也跟着变化了。为什么会出现这种情况呢？
        // 其实，这是一个引用传递而不是值传递，
        // a和b指向的是同一个内存地址。
        // 可以先把a转换成字符串，然后在转换成对象，代码如下：
        this.legalList = JSON.parse(JSON.stringify(this.tempList))
        console.log('集合=' + this.legalList)
        const newList = []
        // 带索引的循环方式
        this.legalList.some((item, i) => {
          console.log('具体分类为：' + item.specific)
          for (const str of this.filter) {
            console.log('筛选条件为：' + str)
            if (str !== '空' && str !== '') {
              if (item.specific === str) {
                newList.push(item)
                console.log(newList)
              }
            }
          }
        })
        // 对象判空
        console.log(Object.keys(newList).length === 0)
        if (Object.keys(newList).length !== 0) {
          this.legalList = JSON.parse(JSON.stringify(newList))
        }
        console.log('结果=' + this.legalList)
      },
      XHRLoadLoadFile: (id) => {
        const xhr = new XMLHttpRequest()
        xhr.open('post', 'http://localhost:8081/download')
        // 如果需要请求头中这是token信息可以在这设置
        xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8')
        xhr.responseType = 'blob'
        xhr.send(JSON.stringify(id))
        xhr.onreadystatechange = function () {
          if (xhr.readyState === 4 && xhr.status === 200) {
            const blob = new Blob([xhr.response])
            const url = window.URL.createObjectURL(blob)

            // 创建一个a标签元素，设置下载属性，点击下载，最后移除该元素
            const link = document.createElement('a')
            link.href = url
            link.style.display = 'none'
            // 取出下载文件名
            const fileName = xhr.getResponseHeader('filename')
            console.log(fileName)
            const downloadFileName = decodeURIComponent(fileName)

            link.setAttribute('download', downloadFileName)
            link.click()
            window.URL.revokeObjectURL(url)
          }
        }
      },
      // 监听 pagesize 改变的事件
      handleSizeChange(newSize) {
        // console.log(newSize)
        this.queryInfo.pageSize = newSize
        this.getLegalList1()
      },
      // 监听 页码值 改变的事件
      handleCurrentChange(newPage) {
        console.log(newPage)
        this.queryInfo.pageNum = newPage
        this.getLegalList1()
      },
      // 监听添加对话框的关闭事件
      addDialogClosed() {
        // 使用 validate、resetFields 方法时列表必须有prop属性
        this.$refs.addFormRef.resetFields()
      },
      editDialogClosed() {

      },
      // 监听文件上传成功的钩子函数
      handleSuccess(response) {
        this.$message.success('上传成功')
        const path = response.date
        this.addForm.filePath = path
        console.log(this.addForm)
      }
    }
  }

</script>

<style lang="less" scoped>
</style>
