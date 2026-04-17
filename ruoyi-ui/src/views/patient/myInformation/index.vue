<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" size="small" v-show="showSearch" label-width="90px">
      <el-form-item label="Name" prop="name">
        <el-input v-model="queryParams.name" placeholder="Patient name" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="Hospital" prop="hospital">
        <el-input v-model="queryParams.hospital" placeholder="Hospital" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="Record No" prop="medicalRecordNo">
        <el-input v-model="queryParams.medicalRecordNo" placeholder="Medical record no" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="Phone" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="Phone" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">Search</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">Reset</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-upload" size="mini" @click="handleExcelImport" v-hasPermi="['patient:information:add']">Import</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['patient:information:add']">Add</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['patient:information:edit']">Edit</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['patient:information:remove']">Delete</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['patient:information:export']">Export</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="informationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="visitId" width="80" align="center" />
      <el-table-column label="Name" prop="name" min-width="120" />
      <el-table-column label="Record No" prop="medicalRecordNo" min-width="140" />
      <el-table-column label="Hospital" prop="hospital" min-width="160" show-overflow-tooltip />
      <el-table-column label="Phone" prop="phone" width="130" />
      <el-table-column label="Visit Date" prop="visitTime" width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.visitTime, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="Action" width="220" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">Detail</el-button>
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)" v-hasPermi="['patient:information:edit']">Edit</el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)" v-hasPermi="['patient:information:remove']">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="Name" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="Gender"><el-select v-model="form.gender"><el-option label="Male" value="男" /><el-option label="Female" value="女" /></el-select></el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="Birth Date" prop="birthDate"><el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Visit Date" prop="visitTime"><el-date-picker v-model="form.visitTime" type="date" value-format="yyyy-MM-dd" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="Hospital" prop="hospitalDeptId">
              <el-select v-model="form.hospitalDeptId" clearable filterable placeholder="Select hospital" style="width: 100%" @change="handleHospitalChange">
                <el-option v-for="item in hospitalOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="Other Hospital"><el-input v-model="hospitalOther" @input="handleOtherHospitalInput" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Record No" prop="medicalRecordNo"><el-input v-model="form.medicalRecordNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Phone" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="Parent"><el-input v-model="form.parentName" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="Chief Complaint" prop="chiefComplaint"><el-input v-model="form.chiefComplaint" type="textarea" :rows="2" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="Main Symptom" prop="mainSymptom"><el-input v-model="form.mainSymptom" type="textarea" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">OK</el-button>
        <el-button @click="cancel">Cancel</el-button>
      </div>
    </el-dialog>

    <el-dialog title="Import Failures" :visible.sync="importResultOpen" width="720px" append-to-body>
      <div class="import-summary">
        <span>Success {{ importSummary.successCount }}</span>
        <span class="danger">Fail {{ importSummary.failureCount }}</span>
      </div>
      <el-table :data="importSummary.failureDetails" border max-height="360">
        <el-table-column label="Row" prop="rowNum" width="90" align="center" />
        <el-table-column label="Record No" prop="medicalRecordNo" width="160" />
        <el-table-column label="Reason" prop="reason" min-width="320" show-overflow-tooltip />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="downloadImportFailures" :disabled="!importSummary.failureDetails.length">Download CSV</el-button>
        <el-button @click="importResultOpen = false">Close</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listMyInformation,
  getInformation,
  delInformation,
  addInformation,
  updateInformation,
  importInformationExcel,
  listHospitalOptions
} from '@/api/patient/information'

export default {
  name: 'MyInformation',
  data() {
    return {
      loading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      informationList: [],
      hospitalOptions: [],
      hospitalOther: '',
      title: '',
      open: false,
      importResultOpen: false,
      importSummary: {
        successCount: 0,
        failureCount: 0,
        failureDetails: []
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        hospital: undefined,
        medicalRecordNo: undefined,
        phone: undefined
      },
      form: {},
      rules: {
        name: [{ required: true, message: 'Please input name', trigger: 'blur' }],
        birthDate: [{ required: true, message: 'Please select birth date', trigger: 'change' }],
        visitTime: [{ required: true, message: 'Please select visit date', trigger: 'change' }],
        medicalRecordNo: [{ required: true, message: 'Please input medical record no', trigger: 'blur' }],
        phone: [
          { required: true, message: 'Please input phone', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: 'Invalid phone format', trigger: 'blur' }
        ],
        chiefComplaint: [{ required: true, message: 'Please input chief complaint', trigger: 'blur' }],
        mainSymptom: [{ required: true, message: 'Please input main symptom', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.reset()
    this.getList()
    this.loadHospitalOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listMyInformation(this.queryParams).then(response => {
        this.informationList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    loadHospitalOptions() {
      return listHospitalOptions().then(res => {
        this.hospitalOptions = res.data || []
      }).catch(() => {
        this.hospitalOptions = []
      })
    },
    handleHospitalChange(value) {
      const selected = this.hospitalOptions.find(item => item.deptId === value)
      if (selected) {
        this.form.hospital = selected.deptName
        this.hospitalOther = ''
      }
    },
    handleOtherHospitalInput(value) {
      const val = value ? value.trim() : ''
      if (val) {
        this.form.hospital = val
        this.form.hospitalDeptId = undefined
      }
    },
    normalizeHospitalInput() {
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      if (selected) {
        this.form.hospital = selected.deptName
        this.hospitalOther = ''
      } else {
        const val = this.hospitalOther ? this.hospitalOther.trim() : ''
        this.form.hospital = val || this.form.hospital
      }
    },
    reset() {
      this.form = {
        visitId: undefined,
        name: undefined,
        gender: undefined,
        birthDate: undefined,
        visitTime: undefined,
        hospitalDeptId: undefined,
        hospital: undefined,
        medicalRecordNo: undefined,
        parentName: undefined,
        phone: undefined,
        chiefComplaint: undefined,
        mainSymptom: undefined
      }
      this.hospitalOther = ''
      this.resetForm('form')
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
    handleAdd() {
      this.reset()
      this.open = true
      this.title = 'Add Patient'
    },
    handleUpdate(row) {
      const visitId = row.visitId || this.ids[0]
      if (!visitId) return
      this.reset()
      getInformation(visitId).then(res => {
        this.form = res.data || {}
        const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
        this.hospitalOther = selected ? '' : (this.form.hospital || '')
        this.open = true
        this.title = 'Edit Patient'
      })
    },
    handleDetail(row) {
      const visitId = row.visitId || this.ids[0]
      if (!visitId) {
        this.$modal.msgWarning('Please select one row')
        return
      }
      this.$router.push(`/patient-detail/${visitId}`)
    },
    submitForm() {
      this.normalizeHospitalInput()
      this.$refs.form.validate(valid => {
        if (!valid) return
        const req = this.form.visitId ? updateInformation(this.form) : addInformation(this.form)
        req.then(() => {
          this.$modal.msgSuccess(this.form.visitId ? 'Updated' : 'Created')
          this.open = false
          this.getList()
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleDelete(row) {
      const visitIds = row.visitId || this.ids
      if (!visitIds || visitIds.length === 0) return
      this.$modal.confirm('Confirm delete selected data?').then(() => {
        return delInformation(visitIds)
      }).then(() => {
        this.$modal.msgSuccess('Deleted')
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
          this.$modal.msgError('Please select .xlsx file')
          return
        }
        const formData = new FormData()
        formData.append('file', file)
        this.$modal.loading('Importing...')
        importInformationExcel(formData).then(res => {
          const successCount = Number(res.successCount || 0)
          const failureCount = Number(res.failureCount || 0)
          const failureDetails = Array.isArray(res.failureDetails) ? res.failureDetails : []
          this.importSummary = { successCount, failureCount, failureDetails }
          this.$modal.msgSuccess(res.msg || 'Import finished')
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
      const header = ['rowNum', 'medicalRecordNo', 'reason']
      const lines = [header.join(',')]
      rows.forEach(item => {
        const line = [item.rowNum, item.medicalRecordNo, item.reason]
          .map(v => '"' + String(v === undefined || v === null ? '' : v).replace(/"/g, '""') + '"')
          .join(',')
        lines.push(line)
      })
      const blob = new Blob([lines.join('\n')], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `patient_import_failures_${Date.now()}.csv`
      link.click()
      URL.revokeObjectURL(url)
    }
  }
}
</script>

<style scoped>
.import-summary {
  margin-bottom: 12px;
  display: flex;
  gap: 20px;
}
.danger {
  color: #f56c6c;
}
</style>