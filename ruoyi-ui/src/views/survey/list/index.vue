<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="问卷标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入问卷标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['survey:survey:add']">新增</el-button></el-col>
      <el-col :span="1.5"><el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['survey:survey:edit']">修改</el-button></el-col>
      <el-col :span="1.5"><el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['survey:survey:remove']">删除</el-button></el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="surveyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="问卷ID" prop="surveyId" width="90" />
      <el-table-column label="问卷标题" prop="title" min-width="220" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="170">
        <template slot-scope="scope">{{ parseTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="360">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['survey:survey:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['survey:survey:remove']">删除</el-button>
          <el-button size="mini" type="text" icon="el-icon-share" @click="handleShare(scope.row)">分享</el-button>
          <el-button size="mini" type="text" icon="el-icon-document" @click="handleAnswer(scope.row)">答卷</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="shareTitle" :visible.sync="shareOpen" width="520px" append-to-body>
      <el-alert type="info" :closable="false" title="已改为站内真实填写地址，不再依赖外部二维码服务" show-icon />
      <el-input type="textarea" :rows="3" v-model="shareLink" readonly style="margin-top: 12px;" />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="copyShareLink">复制链接</el-button>
        <el-button @click="shareOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSurvey, delSurvey } from '@/api/survey/survey'

export default {
  name: 'Survey',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      surveyList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        status: undefined
      },
      shareOpen: false,
      shareTitle: '问卷分享',
      shareLink: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listSurvey(this.queryParams).then(response => {
        this.surveyList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.surveyId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.$router.push('/survey/form')
    },
    handleUpdate(row) {
      const surveyId = row.surveyId || this.ids[0]
      this.$router.push('/survey/form/' + surveyId)
    },
    handleDelete(row) {
      const surveyIds = row.surveyId || this.ids
      this.$modal.confirm('确认删除选中的问卷吗？').then(() => {
        return delSurvey(surveyIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleShare(row) {
      this.shareTitle = '分享问卷：' + row.title
      this.shareLink = window.location.origin + '/survey/fill/' + row.surveyId
      this.shareOpen = true
    },
    async copyShareLink() {
      if (!this.shareLink) return
      try {
        await navigator.clipboard.writeText(this.shareLink)
        this.$modal.msgSuccess('链接已复制')
      } catch (e) {
        const input = document.createElement('input')
        input.value = this.shareLink
        document.body.appendChild(input)
        input.select()
        document.execCommand('copy')
        document.body.removeChild(input)
        this.$modal.msgSuccess('链接已复制')
      }
    },
    handleAnswer(row) {
      this.$router.push('/survey/answer/' + row.surveyId)
    }
  }
}
</script>