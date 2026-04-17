<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px">
      <el-form-item label="参与者" prop="participantKey">
        <el-input v-model="queryParams.participantKey" placeholder="用户名/标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport">导出明细</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="answerList" border>
      <el-table-column type="expand">
        <template slot-scope="scope">
          <div class="answer-json">{{ prettyJson(scope.row.answerJson) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="答卷ID" prop="answerId" width="100" />
      <el-table-column label="参与者" prop="participantKey" min-width="160" />
      <el-table-column label="提交IP" prop="submitIp" width="140" />
      <el-table-column label="用户代理" prop="userAgent" min-width="220" show-overflow-tooltip />
      <el-table-column label="提交时间" prop="submitTime" width="170">
        <template slot-scope="scope">{{ parseTime(scope.row.submitTime) }}</template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listSurveyAnswers } from '@/api/survey/survey'

export default {
  name: 'SurveyAnswer',
  data() {
    return {
      loading: false,
      surveyId: undefined,
      total: 0,
      answerList: [],
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        participantKey: undefined,
        params: {}
      }
    }
  },
  created() {
    this.surveyId = this.$route.params.surveyId
    this.getList()
  },
  methods: {
    buildQuery() {
      const query = { ...this.queryParams }
      query.params = query.params || {}
      query.params.beginTime = this.dateRange && this.dateRange.length ? this.dateRange[0] : undefined
      query.params.endTime = this.dateRange && this.dateRange.length ? this.dateRange[1] : undefined
      return query
    },
    getList() {
      this.loading = true
      listSurveyAnswers(this.surveyId, this.buildQuery()).then(res => {
        this.answerList = res.rows || []
        this.total = res.total || 0
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
      this.dateRange = []
      this.handleQuery()
    },
    handleExport() {
      this.download(`/survey/survey/${this.surveyId}/answers/export`, this.buildQuery(), `survey_answers_${this.surveyId}_${Date.now()}.xlsx`)
    },
    prettyJson(text) {
      if (!text) return '-'
      try {
        return JSON.stringify(JSON.parse(text), null, 2)
      } catch (e) {
        return text
      }
    }
  }
}
</script>

<style scoped>
.answer-json {
  margin: 0;
  padding: 10px;
  white-space: pre-wrap;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-family: Consolas, 'Courier New', monospace;
  font-size: 12px;
}
</style>