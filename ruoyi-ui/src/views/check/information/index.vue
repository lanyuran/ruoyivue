<template>
  <div class="app-container">
    <el-form
      ref="queryForm"
      :model="queryParams"
      :inline="true"
      size="small"
      v-show="showSearch"
      label-width="90px"
      class="query-form"
    >
      <el-form-item label="患者姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          class="query-input"
          placeholder="请输入患者姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input
          v-model="queryParams.phone"
          class="query-input"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="就诊医院" prop="hospital">
        <el-input
          v-model="queryParams.hospital"
          class="query-input"
          placeholder="请输入就诊医院"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="病历号" prop="medicalRecordNo">
        <el-input
          v-model="queryParams.medicalRecordNo"
          class="query-input"
          placeholder="请输入病历号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item class="query-form__actions">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar-row mb8">
      <div class="toolbar-actions">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['patient:information:edit']"
        >修改</el-button>
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['patient:information:remove']"
        >删除</el-button>
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['patient:information:export']"
        >导出</el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <el-table v-loading="loading" :data="informationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="80" align="center">
        <template slot-scope="scope">
          {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="患者姓名" prop="name" min-width="120" />
      <el-table-column label="联系电话" prop="phone" width="130" />
      <el-table-column label="就诊医院" prop="hospital" min-width="180" show-overflow-tooltip />
      <el-table-column label="病历号" prop="medicalRecordNo" min-width="160" />
      <el-table-column label="就诊时间" prop="visitTime" width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.visitTime, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['patient:information:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
            v-hasPermi="['patient:information:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <visit-edit-dialog
      :visible.sync="editOpen"
      :visit-id="editingVisitId"
      @success="getList"
    />
  </div>
</template>

<script>
import { listInformation, delInformation } from '@/api/patient/information'
import VisitEditDialog from '@/views/patient/components/VisitEditDialog'

export default {
  name: 'CheckInformation',
  components: {
    VisitEditDialog
  },
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      informationList: [],
      ids: [],
      single: true,
      multiple: true,
      editOpen: false,
      editingVisitId: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        phone: undefined,
        medicalRecordNo: undefined,
        hospital: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listInformation(this.queryParams).then(response => {
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
    handleDelete(row) {
      const visitIds = (row && row.visitId) || this.ids
      if (!visitIds || (Array.isArray(visitIds) && visitIds.length === 0)) {
        this.$modal.msgWarning('请选择至少一条需要删除的数据')
        return
      }
      this.$modal.confirm('确认删除选中的数据吗？').then(() => {
        return delInformation(visitIds)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleDetail(row) {
      const visitId = (row && row.visitId) || this.ids[0]
      if (!visitId) {
        this.$modal.msgWarning('请选择一条数据')
        return
      }
      this.$router.push(`/patient-detail/${visitId}`)
    },
    handleExport() {
      this.download('patient/information/export', { ...this.queryParams }, `patient_information_${Date.now()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.query-form {
  display: flex;
  flex-wrap: wrap;
}

.query-input {
  width: 220px;
  max-width: 100%;
}

.query-form__actions {
  margin-right: 0;
}

.toolbar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 768px) {
  .query-form {
    display: block;
  }

  .query-form .el-form-item {
    width: 100%;
    margin-right: 0;
  }

  .query-input {
    width: 100%;
  }

  .toolbar-row {
    align-items: flex-start;
  }
}
</style>
