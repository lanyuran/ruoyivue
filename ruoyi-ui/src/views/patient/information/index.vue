<template>
  <div class="app-container">
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleExcelImport"
          v-hasPermi="['patient:information:excel_add']"
        >excel导入</el-button>
        <input
          ref="excelInput"
          type="file"
          accept=".xlsx"
          style="display:none"
          @change="onExcelChange"
        />
        <el-progress
          v-if="uploading"
          :percentage="uploadPercent"
          :text-inside="true"
          :stroke-width="16"
          style="width:260px;margin-top:8px"
        />
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['patient:information:add']"
        >手动新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['patient:information:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['patient:information:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['patient:information:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="informationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['patient:information:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['patient:information:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改鼻炎患者就诊信息主（包含文档中所有字段）对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="患者姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入患者姓名" />
        </el-form-item>
        <el-form-item label="患者出生日期" prop="birthDate">
          <el-date-picker clearable
            v-model="form.birthDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择患者出生日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="就诊时间" prop="visitTime">
          <el-date-picker clearable
            v-model="form.visitTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择就诊时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="就诊医院名称" prop="hospital">
          <el-input v-model="form.hospital" placeholder="请输入就诊医院名称" />
        </el-form-item>
        <el-form-item label="病历号" prop="medicalRecordNo">
          <el-input v-model="form.medicalRecordNo" placeholder="请输入病历号" />
        </el-form-item>
        <el-form-item label="联系手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系手机号码" />
        </el-form-item>
        <el-form-item label="既往用药史" prop="pastMedication">
          <el-input v-model="form.pastMedication" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input v-model="form.chiefComplaint" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="主证" prop="mainSymptom">
          <el-input v-model="form.mainSymptom" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="共患病" prop="comorbidity">
          <el-input v-model="form.comorbidity" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="体格检查结果" prop="physicalExam">
          <el-input v-model="form.physicalExam" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="舌脉情况" prop="tonguePulse">
          <el-input v-model="form.tonguePulse" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="过敏原检测-总IgE" prop="allergenTotalIge">
          <el-input v-model="form.allergenTotalIge" placeholder="请输入过敏原检测-总IgE" />
        </el-form-item>
        <el-form-item label="过敏原检测-特异性IgE" prop="allergenSpecificIge">
          <el-input v-model="form.allergenSpecificIge" placeholder="请输入过敏原检测-特异性IgE" />
        </el-form-item>
        <el-form-item label="中医诊断" prop="tcmDiagnosis">
          <el-input v-model="form.tcmDiagnosis" placeholder="请输入中医诊断" />
        </el-form-item>
        <el-form-item label="中医治法" prop="tcmTreatment">
          <el-input v-model="form.tcmTreatment" placeholder="请输入中医治法" />
        </el-form-item>
        <el-form-item label="中医外治处方" prop="tcmExternalPrescription">
          <el-input v-model="form.tcmExternalPrescription" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInformation, getInformation, delInformation, addInformation, updateInformation } from "@/api/patient/information"
import request from '@/utils/request'

export default {
  name: "Information",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 鼻炎患者就诊信息主（包含文档中所有字段）表格数据
      informationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        hospital: null,
        medicalRecordNo: null,
        phone: null,
        mainSymptom: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "患者姓名不能为空", trigger: "blur" }
        ],
        gender: [
          { required: true, message: "患者性别不能为空", trigger: "blur" }
        ],
        birthDate: [
          { required: true, message: "患者出生日期不能为空", trigger: "blur" }
        ],
        visitTime: [
          { required: true, message: "就诊时间不能为空", trigger: "blur" }
        ],
        hospital: [
          { required: true, message: "就诊医院名称不能为空", trigger: "blur" }
        ],
        medicalRecordNo: [
          { required: true, message: "病历号不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "联系手机号码不能为空", trigger: "blur" }
        ],
        chiefComplaint: [
          { required: true, message: "主诉不能为空", trigger: "blur" }
        ],
        mainSymptom: [
          { required: true, message: "主证不能为空", trigger: "blur" }
        ],
        physicalExam: [
          { required: true, message: "体格检查结果不能为空", trigger: "blur" }
        ],
        tonguePulse: [
          { required: true, message: "舌脉情况不能为空", trigger: "blur" }
        ],
        tcmDiagnosis: [
          { required: true, message: "中医诊断不能为空", trigger: "blur" }
        ],
        tcmTreatment: [
          { required: true, message: "中医治法不能为空", trigger: "blur" }
        ],
        tcmExternalPrescription: [
          { required: true, message: "中医外治处方不能为空", trigger: "blur" }
        ]
      },
      // Excel 导入相关
      uploading: false,
      uploadPercent: 0
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询鼻炎患者就诊信息主（包含文档中所有字段）列表 */
    getList() {
      this.loading = true
      listInformation(this.queryParams).then(response => {
        this.informationList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        visitId: null,
        name: null,
        gender: null,
        birthDate: null,
        visitTime: null,
        hospital: null,
        medicalRecordNo: null,
        parentName: null,
        phone: null,
        pastMedication: null,
        chiefComplaint: null,
        mainSymptom: null,
        comorbidity: null,
        physicalExam: null,
        tonguePulse: null,
        allergenTotalIge: null,
        allergenSpecificIge: null,
        tcmDiagnosis: null,
        tcmTreatment: null,
        tcmExternalPrescription: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.visitId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加鼻炎患者就诊信息主（包含文档中所有字段）"
    },
    /** excel导入按钮操作 */
    handleExcelImport() {
      if (this.uploading) return
      const input = this.$refs.excelInput
      if (input) input.click()
    },
    // 选择 Excel 后上传
    async onExcelChange(e) {
      const files = e && e.target ? e.target.files : null
      const file = files && files[0]
      if (!file) return
      // 简单校验后缀
      if (!/\.xlsx$/i.test(file.name)) {
        this.$message.error('仅支持 .xlsx 文件导入')
        if (this.$refs.excelInput) this.$refs.excelInput.value = ''
        return
      }
      const formData = new FormData()
      formData.append('file', file)
      this.uploading = true
      this.uploadPercent = 0
      try {
        await request({
          url: '/patient/information/import',
          method: 'post',
          data: formData,
          headers: { 'Content-Type': 'multipart/form-data' },
          onUploadProgress: (evt) => {
            if (evt && evt.total) {
              this.uploadPercent = Math.round((evt.loaded / evt.total) * 100)
            }
          }
        })
        this.$modal.msgSuccess('导入成功')
        this.getList()
      } catch (err) {
        this.$message.error((err && err.msg) || '导入失败')
      } finally {
        this.uploading = false
        this.uploadPercent = 0
        if (this.$refs.excelInput) this.$refs.excelInput.value = ''
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const visitId = row.visitId || this.ids
      getInformation(visitId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改鼻炎患者就诊信息主（包含文档中所有字段）"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.visitId != null) {
            updateInformation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addInformation(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const visitIds = row.visitId || this.ids
      this.$modal.confirm('是否确认删除鼻炎患者就诊信息主（包含文档中所有字段）编号为"' + visitIds + '"的数据项？').then(function() {
        return delInformation(visitIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('patient/information/export', {
        ...this.queryParams
      }, `information_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
