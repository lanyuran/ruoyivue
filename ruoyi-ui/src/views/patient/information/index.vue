<template>
  <div class="app-container">
    <el-card class="quick-entry" shadow="never">
      <div class="quick-entry__content">
        <div>
          <div class="quick-entry__title">患者问卷入口</div>
          <div class="quick-entry__desc">
            患者无需注册，可直接打开填写链接提交信息；提交后系统会自动生成账号。
          </div>
          <div class="quick-entry__link">{{ fillUrl }}</div>
        </div>
        <div class="quick-entry__actions">
          <el-button type="primary" @click="openFillPage">直接填写</el-button>
          <el-button plain @click="copyFillLink">复制链接</el-button>
        </div>
      </div>
    </el-card>

    <el-form ref="queryForm" :model="queryParams" :inline="true" size="small" v-show="showSearch" label-width="90px">
      <el-form-item label="患者姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入患者姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="就诊医院" prop="hospital">
        <el-input v-model="queryParams.hospital" placeholder="请输入就诊医院" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="病历号" prop="medicalRecordNo">
        <el-input v-model="queryParams.medicalRecordNo" placeholder="请输入病历号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入联系电话" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8" v-if="!isPatientOnlyRole">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-upload" size="mini" @click="handleExcelImport" v-hasPermi="['patient:information:add']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['patient:information:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['patient:information:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['patient:information:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="informationList" @selection-change="handleSelectionChange">
      <el-table-column v-if="!isPatientOnlyRole" type="selection" width="55" align="center" />
      <el-table-column label="编号" prop="visitId" width="80" align="center" />
      <el-table-column label="患者姓名" prop="name" min-width="120" />
      <el-table-column label="病历号" prop="medicalRecordNo" min-width="160" />
      <el-table-column label="就诊医院" prop="hospital" min-width="180" show-overflow-tooltip />
      <el-table-column label="联系电话" prop="phone" width="130" />
      <el-table-column label="就诊日期" prop="visitTime" width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.visitTime, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="填表时间" prop="fillTime" width="170">
        <template slot-scope="scope">{{ parseTime(scope.row.fillTime) || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="!isPatientOnlyRole" size="mini" type="text" @click="handleUpdate(scope.row)" v-hasPermi="['patient:information:edit']">修改</el-button>
          <el-button v-if="!isPatientOnlyRole" size="mini" type="text" @click="handleDelete(scope.row)" v-hasPermi="['patient:information:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <visit-edit-dialog
      :visible.sync="editOpen"
      :visit-id="editingVisitId"
      @success="getList"
    />

    <el-dialog title="导入失败明细" :visible.sync="importResultOpen" width="720px" append-to-body>
      <div class="import-summary">
        <span>成功 {{ importSummary.successCount }} 条</span>
        <span class="danger">失败 {{ importSummary.failureCount }} 条</span>
      </div>
      <el-table :data="importSummary.failureDetails" border max-height="360">
        <el-table-column label="行号" prop="rowNum" width="90" align="center" />
        <el-table-column label="病历号" prop="medicalRecordNo" width="160" />
        <el-table-column label="失败原因" prop="reason" min-width="320" show-overflow-tooltip />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="downloadImportFailures" :disabled="!importSummary.failureDetails.length">下载 Excel</el-button>
        <el-button @click="importResultOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMyInformation,
  delInformation,
  importInformationExcel
} from '@/api/patient/information'
import VisitEditDialog from '@/views/patient/components/VisitEditDialog'

export default {
  name: 'Information',
  components: {
    VisitEditDialog
  },
  data() {
    return {
      loading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      informationList: [],
      editOpen: false,
      editingVisitId: null,
      importResultOpen: false,
      importSummary: {
        successCount: 0,
        failureCount: 0,
        failureDetails: []
      },
      fillUrl: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        hospital: undefined,
        medicalRecordNo: undefined,
        phone: undefined
      }
    }
  },
  computed: {
    isPatientOnlyRole() {
      const roles = this.$store.getters.roles || []
      return roles.includes('user') && !roles.includes('doctor') && !roles.includes('manager') && !roles.includes('admin')
    }
  },
  created() {
    this.fillUrl = `${window.location.origin}/patient-mobile/collect`
    this.getList()
  },
  methods: {
    openFillPage() {
      window.open(this.fillUrl, '_blank')
    },
    copyFillLink() {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(this.fillUrl).then(() => {
          this.$modal.msgSuccess('链接已复制')
        }).catch(() => {
          this.fallbackCopy()
        })
        return
      }
      this.fallbackCopy()
    },
    fallbackCopy() {
      const input = document.createElement('input')
      input.value = this.fillUrl
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$modal.msgSuccess('链接已复制')
    },
    getList() {
      this.loading = true
      listMyInformation(this.queryParams).then(response => {
        this.informationList = response.rows || []
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
      this.ids = selection.map(item => item.visitId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleUpdate(row) {
      const visitId = (row && row.visitId) || this.ids[0]
      if (!visitId) {
        this.$modal.msgWarning('请选择一条需要修改的数据')
        return
      }
      this.editingVisitId = visitId
      this.editOpen = true
    },
    handleDetail(row) {
      const visitId = (row && row.visitId) || this.ids[0]
      if (!visitId) {
        this.$modal.msgWarning('请选择一条数据')
        return
      }
      this.$router.push(`/patient-detail/${visitId}`)
    },
    handleDelete(row) {
      const visitIds = (row && row.visitId) || this.ids
      if (!visitIds || visitIds.length === 0) return
      this.$modal.confirm('确认删除选中的数据吗？').then(() => {
        return delInformation(visitIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('patient/information/export', { ...this.queryParams }, `patient_information_${Date.now()}.xlsx`)
    },
    handleExcelImport() {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = '.xlsx'
      input.onchange = () => {
        const file = input.files && input.files[0]
        if (!file) return
        if (!file.name.toLowerCase().endsWith('.xlsx')) {
          this.$modal.msgError('请选择 .xlsx 文件')
          return
        }
        const formData = new FormData()
        formData.append('file', file)
        this.$modal.loading('正在导入...')
        importInformationExcel(formData).then(res => {
          const successCount = Number(res.successCount || 0)
          const failureCount = Number(res.failureCount || 0)
          const failureDetails = Array.isArray(res.failureDetails) ? res.failureDetails : []
          this.importSummary = { successCount, failureCount, failureDetails }
          this.$modal.msgSuccess(res.msg || '导入完成')
          if (failureDetails.length > 0) {
            this.importResultOpen = true
          }
          this.getList()
        }).finally(() => {
          this.$modal.closeLoading()
          input.value = ''
        })
      }
      input.click()
    },
    downloadImportFailures() {
      const rows = this.importSummary.failureDetails || []
      if (!rows.length) return
      const html = this.buildImportFailureExcel(rows)
      const blob = new Blob(['\ufeff' + html], { type: 'application/vnd.ms-excel;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `patient_import_failures_${Date.now()}.xls`
      link.click()
      URL.revokeObjectURL(url)
    },
    buildImportFailureExcel(rows) {
      const body = rows.map(item => `
        <tr>
          <td>${this.escapeExcelText(item.rowNum)}</td>
          <td>${this.escapeExcelText(item.medicalRecordNo)}</td>
          <td>${this.escapeExcelText(item.reason)}</td>
        </tr>
      `).join('')
      return `
        <html xmlns:o="urn:schemas-microsoft-com:office:office"
              xmlns:x="urn:schemas-microsoft-com:office:excel"
              xmlns="http://www.w3.org/TR/REC-html40">
          <head>
            <meta charset="UTF-8" />
          </head>
          <body>
            <table border="1">
              <tr>
                <th>行号</th>
                <th>病历号</th>
                <th>失败原因</th>
              </tr>
              ${body}
            </table>
          </body>
        </html>
      `
    },
    escapeExcelText(value) {
      return String(value === undefined || value === null ? '' : value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/\r?\n/g, '<br/>')
    }
  }
}
</script>

<style scoped>
.quick-entry {
  margin-bottom: 16px;
  border: 1px solid #dbeafe;
  background: linear-gradient(135deg, #eff6ff 0%, #fff7ed 100%);
}

.quick-entry__content {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.quick-entry__title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 6px;
}

.quick-entry__desc {
  color: #475569;
  margin-bottom: 8px;
}

.quick-entry__link {
  color: #2563eb;
  word-break: break-all;
}

.quick-entry__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.import-summary {
  margin-bottom: 12px;
  display: flex;
  gap: 20px;
}

.danger {
  color: #f56c6c;
}

@media (max-width: 768px) {
  .quick-entry__content {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
