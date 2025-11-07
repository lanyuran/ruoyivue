<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="患者姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入患者姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="就诊医院名称" prop="hospital">
        <el-input
          v-model="queryParams.hospital"
          placeholder="请输入就诊医院名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="病历号" prop="medicalRecordNo">
        <el-input
          v-model="queryParams.medicalRecordNo"
          placeholder="请输入病历号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系手机号码" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系手机号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏（保留右侧搜索控制） -->
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="informationList">
      <el-table-column label="就诊记录ID" align="center" prop="visitId" />
      <el-table-column label="患者姓名" align="center" prop="name" />
      <el-table-column label="患者性别" align="center" prop="gender" />
      <el-table-column label="患者出生日期" align="center" prop="birthDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.birthDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="就诊时间" align="center" prop="visitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.visitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="就诊医院名称" align="center" prop="hospital" />
      <el-table-column label="病历号" align="center" prop="medicalRecordNo" />
      <el-table-column label="家长姓名" align="center" prop="parentName" />
      <el-table-column label="联系手机号码" align="center" prop="phone" />
      <el-table-column label="既往用药史" align="center" prop="pastMedication" />
      <el-table-column label="主诉" align="center" prop="chiefComplaint" />
      <el-table-column label="主证" align="center" prop="mainSymptom" />
      <el-table-column label="共患病" align="center" prop="comorbidity" />
      <el-table-column label="体格检查结果" align="center" prop="physicalExam" />
      <el-table-column label="舌脉情况" align="center" prop="tonguePulse" />
      <el-table-column label="过敏原检测-总IgE" align="center" prop="allergenTotalIge" />
      <el-table-column label="过敏原检测-特异性IgE" align="center" prop="allergenSpecificIge" />
      <el-table-column label="中医诊断" align="center" prop="tcmDiagnosis" />
      <el-table-column label="中医治法" align="center" prop="tcmTreatment" />
      <el-table-column label="中医外治处方" align="center" prop="tcmExternalPrescription" />
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listInformation } from "@/api/patient/information"

export default {
  name: "Information",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      informationList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        hospital: null,
        medicalRecordNo: null,
        phone: null,
        mainSymptom: null,
      },
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询患者信息 */
    getList() {
      this.loading = true
      listInformation(this.queryParams).then(response => {
        this.informationList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
  }
}
</script>
