<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="患者姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入患者姓名"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="就诊医院名称" prop="hospital">
        <el-input
          v-model="queryParams.hospital"
          placeholder="请输入就诊医院名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="病历号" prop="medicalRecordNo">
        <el-input
          v-model="queryParams.medicalRecordNo"
          placeholder="请输入病历号"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系手机号码" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系手机号码"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
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
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-view"
          size="mini"
          @click="handleDetail"
          v-hasPermi="['patient:information:query']"
        >详细</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="informationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center">
        <template slot-scope="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="病历号" align="center" prop="medicalRecordNo" />
      <el-table-column label="主诉" align="center" prop="chiefComplaint" show-overflow-tooltip />
      <el-table-column label="就诊医院" align="center" prop="hospital" />
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
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详细信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="患者姓名" prop="name" >
          <el-input v-model="form.name" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="就诊日期" prop="visitTime">
          <el-date-picker v-model="form.visitTime" type="date" value-format="yyyy-MM-dd" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="就诊医院" prop="hospital">
          <el-select
            v-model="form.hospitalDeptId"
            placeholder="请选择就诊医院"
            clearable
            :disabled="isReadonly"
            @change="handleHospitalChange"
          >
            <el-option
              v-for="item in hospitalOptions"
              :key="item.deptId"
              :label="item.deptName"
              :value="item.deptId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="其他医院">
          <el-input
            v-model="hospitalOther"
            placeholder="请输入其他医院"
            :disabled="isReadonly"
            @input="handleOtherHospitalInput"
          />
        </el-form-item>

        <el-form-item label="病历号" prop="medicalRecordNo">
          <el-input v-model="form.medicalRecordNo" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="家长姓名" prop="parentName">
          <el-input v-model="form.parentName" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" maxlength="11" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="既往用药史" prop="pastMedication">
          <el-input type="textarea" v-model="form.pastMedication" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input type="textarea" v-model="form.chiefComplaint" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="主证" prop="mainSymptom">
          <el-input type="textarea" v-model="form.mainSymptom" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="共患病" prop="comorbidity">
          <el-input type="textarea" v-model="form.comorbidity" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="体格检查" prop="physicalExam">
          <el-input type="textarea" v-model="form.physicalExam" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="舌脉" prop="tonguePulse">
          <el-input type="textarea" v-model="form.tonguePulse" :disabled="isReadonly"></el-input>
        </el-form-item>

        <!-- 图片展示/上传 -->
        <el-form-item label="舌象照片">
          <el-upload
            v-if="!isReadonly"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'tongueImagePath')"
            :on-remove="() => handleRemove('tongueImagePath')"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="tongueImageList"
            :limit="1"
            :on-exceed="handleExceed"
            :disabled="isReadonly">
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-else>
            <el-image
              v-if="form.tongueImagePath && form.tongueImagePath !== ''"
              style="width: 148px; height: 148px; border-radius: 6px; margin-right: 10px;"
              :src="getImageUrl(form.tongueImagePath)"
              :preview-src-list="[getImageUrl(form.tongueImagePath)]"
            >
              <div slot="error" class="image-slot">
                <span style="display: flex; justify-content: center; align-items: center; height: 100%; color: #909399; background: #f5f7fa;">未录入</span>
              </div>
            </el-image>
            <span v-else style="color: #909399;">未录入</span>
          </div>
        </el-form-item>

        <el-form-item label="总 IgE">
          <el-input v-model="form.allergenTotalIge" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="特异性 IgE">
          <el-input v-model="form.allergenSpecificIge" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="血常规报告">
          <el-upload
            v-if="!isReadonly"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'bloodTestImagePath')"
            :on-remove="() => handleRemove('bloodTestImagePath')"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="bloodTestImageList"
            :limit="1"
            :on-exceed="handleExceed"
            :disabled="isReadonly">
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-else>
            <el-image
              v-if="form.bloodTestImagePath && form.bloodTestImagePath !== ''"
              style="width: 148px; height: 148px; border-radius: 6px; margin-right: 10px;"
              :src="getImageUrl(form.bloodTestImagePath)"
              :preview-src-list="[getImageUrl(form.bloodTestImagePath)]"
            >
              <div slot="error" class="image-slot">
                <span style="display: flex; justify-content: center; align-items: center; height: 100%; color: #909399; background: #f5f7fa;">未录入</span>
              </div>
            </el-image>
            <span v-else style="color: #909399;">未录入</span>
          </div>
        </el-form-item>

        <el-form-item label="炎症因子报告">
          <el-upload
            v-if="!isReadonly"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'inflammationImagePath')"
            :on-remove="() => handleRemove('inflammationImagePath')"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="inflammationImageList"
            :limit="1"
            :on-exceed="handleExceed"
            :disabled="isReadonly">
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-else>
            <el-image
              v-if="form.inflammationImagePath && form.inflammationImagePath !== ''"
              style="width: 148px; height: 148px; border-radius: 6px; margin-right: 10px;"
              :src="getImageUrl(form.inflammationImagePath)"
              :preview-src-list="[getImageUrl(form.inflammationImagePath)]"
            >
              <div slot="error" class="image-slot">
                <span style="display: flex; justify-content: center; align-items: center; height: 100%; color: #909399; background: #f5f7fa;">未录入</span>
              </div>
            </el-image>
            <span v-else style="color: #909399;">未录入</span>
          </div>
        </el-form-item>

        <el-form-item label="肝肾功能">
          <el-upload
            v-if="!isReadonly"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'liverKidneyImagePath')"
            :on-remove="() => handleRemove('liverKidneyImagePath')"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="liverKidneyImageList"
            :limit="1"
            :on-exceed="handleExceed"
            :disabled="isReadonly">
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-else>
            <el-image
              v-if="form.liverKidneyImagePath && form.liverKidneyImagePath !== ''"
              style="width: 148px; height: 148px; border-radius: 6px; margin-right: 10px;"
              :src="getImageUrl(form.liverKidneyImagePath)"
              :preview-src-list="[getImageUrl(form.liverKidneyImagePath)]"
            >
              <div slot="error" class="image-slot">
                <span style="display: flex; justify-content: center; align-items: center; height: 100%; color: #909399; background: #f5f7fa;">未录入</span>
              </div>
            </el-image>
            <span v-else style="color: #909399;">未录入</span>
          </div>
        </el-form-item>

        <el-form-item label="肾早期损伤">
          <el-upload
            v-if="!isReadonly"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'renalInjuryImagePath')"
            :on-remove="() => handleRemove('renalInjuryImagePath')"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="renalInjuryImageList"
            :limit="1"
            :on-exceed="handleExceed"
            :disabled="isReadonly">
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-else>
            <el-image
              v-if="form.renalInjuryImagePath && form.renalInjuryImagePath !== ''"
              style="width: 148px; height: 148px; border-radius: 6px; margin-right: 10px;"
              :src="getImageUrl(form.renalInjuryImagePath)"
              :preview-src-list="[getImageUrl(form.renalInjuryImagePath)]"
            >
              <div slot="error" class="image-slot">
                <span style="display: flex; justify-content: center; align-items: center; height: 100%; color: #909399; background: #f5f7fa;">未录入</span>
              </div>
            </el-image>
            <span v-else style="color: #909399;">未录入</span>
          </div>
        </el-form-item>

        <el-form-item label="中医诊断">
          <el-input v-model="form.tcmDiagnosis" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="中医治法">
          <el-input v-model="form.tcmTreatment" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="处方照片">
          <el-upload
            v-if="!isReadonly"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="(res) => handleUploadSuccess(res, 'tcmTreatmentImagePath')"
            :on-remove="() => handleRemove('tcmTreatmentImagePath')"
            :before-upload="beforeAvatarUpload"
            list-type="picture-card"
            :file-list="tcmTreatmentImageList"
            :limit="1"
            :on-exceed="handleExceed"
            :disabled="isReadonly">
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-upload>
          <div v-else>
            <el-image
              v-if="form.tcmTreatmentImagePath && form.tcmTreatmentImagePath !== ''"
              style="width: 148px; height: 148px; border-radius: 6px; margin-right: 10px;"
              :src="getImageUrl(form.tcmTreatmentImagePath)"
              :preview-src-list="[getImageUrl(form.tcmTreatmentImagePath)]"
            >
              <div slot="error" class="image-slot">
                <span style="display: flex; justify-content: center; align-items: center; height: 100%; color: #909399; background: #f5f7fa;">未录入</span>
              </div>
            </el-image>
            <span v-else style="color: #909399;">未录入</span>
          </div>
        </el-form-item>

        <el-form-item label="中医外治处方">
          <el-input v-model="form.tcmExternalPrescription" type="textarea" :disabled="isReadonly"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">{{ isReadonly ? '关 闭' : '取 消' }}</el-button>
        <el-button v-if="!isReadonly" type="primary" @click="submitForm">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInformation, getInformation, delInformation, updateInformation, listHospitalOptions } from "@/api/patient/information"

export default {
  name: "Information",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      informationList: [],
      hospitalOptions: [],
      hospitalOther: "",
      ids: [],
      single: true,
      multiple: true,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      isReadonly: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        hospital: null,
        medicalRecordNo: null,
        phone: null,
      },
      form: {},
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
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + this.$store.getters.token
      },
      tongueImageList: [],
      bloodTestImageList: [],
      inflammationImageList: [],
      liverKidneyImageList: [],
      renalInjuryImageList: [],
      tcmTreatmentImageList: []
    }
  },
  created() {
    this.getList()
    this.loadHospitalOptions()
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
    loadHospitalOptions() {
      return listHospitalOptions().then(response => {
        this.hospitalOptions = response.data || []
      }).catch(() => {
        this.hospitalOptions = []
      })
    },
    syncHospitalSelection() {
      if (!this.form) {
        return
      }
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      if (selected) {
        this.form.hospital = selected.deptName
        this.hospitalOther = ""
        return
      }
      if (this.form.hospital) {
        this.hospitalOther = this.form.hospital
        this.form.hospitalDeptId = null
      } else {
        this.hospitalOther = ""
      }
    },
    handleHospitalChange(value) {
      const selected = this.hospitalOptions.find(item => item.deptId === value)
      if (selected) {
        this.form.hospital = selected.deptName
      }
      if (value) {
        this.hospitalOther = ""
      } else if (!this.hospitalOther) {
        this.form.hospital = null
      }
    },
    handleOtherHospitalInput(value) {
      const trimmed = value ? value.trim() : ""
      if (trimmed) {
        this.form.hospital = trimmed
        this.form.hospitalDeptId = null
        return
      }
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      this.form.hospital = selected ? selected.deptName : null
    },
    normalizeHospitalInput() {
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      if (selected) {
        this.form.hospital = selected.deptName
        this.hospitalOther = ""
        return
      }
      const trimmed = this.hospitalOther ? this.hospitalOther.trim() : ""
      this.form.hospital = trimmed || null
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.visitId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      let visitId = row && row.visitId ? row.visitId : null
      if (!visitId && this.ids.length === 1) {
        visitId = this.ids[0]
      }
      if (!visitId) {
        this.$modal.alertWarning("请至少选择一条记录")
        return
      }
      getInformation(visitId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改鼻炎患者就诊信息主（包含文档中所有字段）"
        this.initImageLists()
        this.loadHospitalOptions().finally(() => {
          this.syncHospitalSelection()
        })
        this.$nextTick(() => {
          this.setFormReadonly(false)
        })
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.normalizeHospitalInput()
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (!this.form.visitId) {
            this.$modal.msgError("缺少就诊记录ID，无法保存")
            return
          }
          updateInformation(this.form).then(() => {
            this.$modal.msgSuccess("修改成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const visitIds = row && row.visitId ? row.visitId : this.ids
      if (!visitIds || (Array.isArray(visitIds) && visitIds.length === 0)) {
        this.$modal.alertWarning("请至少选择一条记录")
        return
      }
      this.$modal.confirm('是否确认删除鼻炎患者就诊信息主（包含文档中所有字段）编号为"' + visitIds + '"的数据项？').then(() => {
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
    },
    /** 查看详细 */
    handleDetail(row) {
      const visitId = row && row.visitId ? row.visitId : (this.ids.length > 0 ? this.ids[0] : null)
      if (!visitId) {
        this.$modal.alertWarning("请至少选择一条记录")
        return
      }
      this.reset()
      getInformation(visitId).then(response => {
        this.form = response.data
        this.title = "患者就诊详细信息"
        this.open = true
        this.initImageLists()
        this.loadHospitalOptions().finally(() => {
          this.syncHospitalSelection()
        })
        this.setFormReadonly(true)
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
      this.$nextTick(() => {
        this.setFormReadonly(false)
      })
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
        hospitalDeptId: null,
        medicalRecordNo: null,
        parentName: null,
        phone: null,
        pastMedication: null,
        chiefComplaint: null,
        mainSymptom: null,
        comorbidity: null,
        physicalExam: null,
        tonguePulse: null,
        tongueImagePath: null,
        bloodTestImagePath: null,
        inflammationImagePath: null,
        liverKidneyImagePath: null,
        renalInjuryImagePath: null,
        tcmTreatmentImagePath: null,
        allergenTotalIge: null,
        allergenSpecificIge: null,
        tcmDiagnosis: null,
        tcmTreatment: null,
        tcmExternalPrescription: null
      };
      this.hospitalOther = ""
      this.resetForm("form");
    },
    setFormReadonly(readonly) {
      this.isReadonly = readonly
    },
    // 获取完整图片URL
    getImageUrl(path) {
      if (!path) return '';
      if (path.startsWith('http') || path.startsWith('https') || path.startsWith('data:')) {
        return path;
      }
      return process.env.VUE_APP_BASE_API + path;
    },
    // 初始化图片列表
    initImageLists() {
      this.tongueImageList = this.getImageList(this.form.tongueImagePath, '舌象照片')
      this.bloodTestImageList = this.getImageList(this.form.bloodTestImagePath, '血常规报告')
      this.inflammationImageList = this.getImageList(this.form.inflammationImagePath, '炎症因子报告')
      this.liverKidneyImageList = this.getImageList(this.form.liverKidneyImagePath, '肝肾功能')
      this.renalInjuryImageList = this.getImageList(this.form.renalInjuryImagePath, '肾早期损伤')
      this.tcmTreatmentImageList = this.getImageList(this.form.tcmTreatmentImagePath, '处方照片')
    },
    getImageList(path, name) {
      return path ? [{ name: name, url: this.getImageUrl(path) }] : []
    },
    // 图片上传前的处理
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    // 处理上传成功
    handleUploadSuccess(response, field) {
      if (response.code === 200) {
        this.form[field] = response.fileName
        this.$message.success('上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
      }
    },
    // 处理移除图片
    handleRemove(field) {
      this.form[field] = ''
    },
    // 处理超出文件限制
    handleExceed() {
      this.$message.warning('只能上传一张图片')
    }
  }
}
</script>
<style scoped>
::v-deep .el-upload-list__item-status-label {
  display: none !important;
}
</style>
