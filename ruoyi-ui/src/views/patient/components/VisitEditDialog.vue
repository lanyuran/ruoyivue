<template>
  <el-dialog
    title="修改鼻炎患者就诊信息"
    :visible.sync="dialogVisible"
    width="900px"
    append-to-body
    @close="handleClose"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="130px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="患者姓名" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="就诊日期" prop="visitTime">
            <el-date-picker v-model="form.visitTime" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="填表时间">
            <el-date-picker
              v-model="form.fillTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="不填则由系统自动处理"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="就诊医院">
            <el-select
              v-model="form.hospitalDeptId"
              clearable
              filterable
              placeholder="请选择就诊医院"
              style="width: 100%"
              @change="handleHospitalChange"
            >
              <el-option v-for="item in hospitalOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="其他医院">
            <el-input v-model="hospitalOther" placeholder="没有可选医院时填写" @input="handleOtherHospitalInput" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="病历号" prop="medicalRecordNo">
            <el-input v-model="form.medicalRecordNo" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" maxlength="11" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="监护人姓名">
            <el-input v-model="form.parentName" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="既往用药史">
        <el-input v-model="form.pastMedication" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="主诉" prop="chiefComplaint">
        <el-input v-model="form.chiefComplaint" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="主证" prop="mainSymptom">
        <el-input v-model="form.mainSymptom" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="共患疾病">
        <el-input v-model="form.comorbidity" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="体格检查" prop="physicalExam">
        <el-input v-model="form.physicalExam" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="舌脉" prop="tonguePulse">
        <el-input v-model="form.tonguePulse" type="textarea" :rows="2" />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="总 IgE">
            <el-input v-model="form.allergenTotalIge" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="特异性 IgE">
            <el-input v-model="form.allergenSpecificIge" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="舌象照片">
        <image-upload-card :file-list.sync="tongueImageList" @success="res => handleUploadSuccess(res, 'tongueImagePath')" @remove="() => handleRemove('tongueImagePath')" />
      </el-form-item>
      <el-form-item label="血常规报告">
        <image-upload-card :file-list.sync="bloodTestImageList" @success="res => handleUploadSuccess(res, 'bloodTestImagePath')" @remove="() => handleRemove('bloodTestImagePath')" />
      </el-form-item>
      <el-form-item label="炎症因子报告">
        <image-upload-card :file-list.sync="inflammationImageList" @success="res => handleUploadSuccess(res, 'inflammationImagePath')" @remove="() => handleRemove('inflammationImagePath')" />
      </el-form-item>
      <el-form-item label="肝肾功能">
        <image-upload-card :file-list.sync="liverKidneyImageList" @success="res => handleUploadSuccess(res, 'liverKidneyImagePath')" @remove="() => handleRemove('liverKidneyImagePath')" />
      </el-form-item>
      <el-form-item label="肾早期损伤">
        <image-upload-card :file-list.sync="renalInjuryImageList" @success="res => handleUploadSuccess(res, 'renalInjuryImagePath')" @remove="() => handleRemove('renalInjuryImagePath')" />
      </el-form-item>

      <el-form-item label="中医诊断" prop="tcmDiagnosis">
        <el-input v-model="form.tcmDiagnosis" />
      </el-form-item>
      <el-form-item label="中医治法" prop="tcmTreatment">
        <el-input v-model="form.tcmTreatment" />
      </el-form-item>
      <el-form-item label="中药处方照片">
        <image-upload-card :file-list.sync="tcmTreatmentImageList" @success="res => handleUploadSuccess(res, 'tcmTreatmentImagePath')" @remove="() => handleRemove('tcmTreatmentImagePath')" />
      </el-form-item>
      <el-form-item label="中医外治处方" prop="tcmExternalPrescription">
        <el-input v-model="form.tcmExternalPrescription" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" :loading="saving" @click="submitForm">保 存</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getInformation, updateInformation, listHospitalOptions } from '@/api/patient/information'
import ImageUploadCard from './ImageUploadCard'

export default {
  name: 'VisitEditDialog',
  components: {
    ImageUploadCard
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    visitId: {
      type: [Number, String],
      default: null
    }
  },
  data() {
    return {
      saving: false,
      hospitalOptions: [],
      hospitalOther: '',
      form: this.emptyForm(),
      rules: {
        name: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
        visitTime: [{ required: true, message: '请选择就诊日期', trigger: 'change' }],
        medicalRecordNo: [{ required: true, message: '请输入病历号', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        chiefComplaint: [{ required: true, message: '请输入主诉', trigger: 'blur' }],
        mainSymptom: [{ required: true, message: '请输入主证', trigger: 'blur' }],
        physicalExam: [{ required: true, message: '请输入体格检查', trigger: 'blur' }],
        tonguePulse: [{ required: true, message: '请输入舌脉情况', trigger: 'blur' }],
        tcmDiagnosis: [{ required: true, message: '请输入中医诊断', trigger: 'blur' }],
        tcmTreatment: [{ required: true, message: '请输入中医治法', trigger: 'blur' }],
        tcmExternalPrescription: [{ required: true, message: '请输入中医外治处方', trigger: 'blur' }]
      },
      tongueImageList: [],
      bloodTestImageList: [],
      inflammationImageList: [],
      liverKidneyImageList: [],
      renalInjuryImageList: [],
      tcmTreatmentImageList: []
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(value) {
        this.$emit('update:visible', value)
      }
    }
  },
  watch: {
    visible(value) {
      if (value && this.visitId) {
        this.loadDetail()
      }
    },
    visitId(value) {
      if (this.visible && value) {
        this.loadDetail()
      }
    }
  },
  methods: {
    emptyForm() {
      return {
        visitId: null,
        name: null,
        gender: null,
        birthDate: null,
        visitTime: null,
        fillTime: null,
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
      }
    },
    loadDetail() {
      Promise.all([getInformation(this.visitId), this.loadHospitalOptions()]).then(([response]) => {
        this.form = Object.assign(this.emptyForm(), response.data || {})
        this.syncHospitalSelection()
        this.initImageLists()
        this.$nextTick(() => {
          this.clearValidate()
        })
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
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      this.hospitalOther = selected ? '' : (this.form.hospital || '')
    },
    handleHospitalChange(value) {
      const selected = this.hospitalOptions.find(item => item.deptId === value)
      this.form.hospital = selected ? selected.deptName : null
      if (selected) {
        this.hospitalOther = ''
      }
    },
    handleOtherHospitalInput(value) {
      const val = value ? value.trim() : ''
      if (val) {
        this.form.hospital = val
        this.form.hospitalDeptId = null
      }
    },
    normalizeHospitalInput() {
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      if (selected) {
        this.form.hospital = selected.deptName
        return
      }
      const val = this.hospitalOther ? this.hospitalOther.trim() : ''
      this.form.hospital = val || this.form.hospital
    },
    submitForm() {
      this.normalizeHospitalInput()
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (!this.form.visitId) {
          this.$modal.msgError('缺少就诊记录ID，无法保存')
          return
        }
        this.saving = true
        updateInformation(this.form).then(() => {
          this.$modal.msgSuccess('修改成功')
          this.dialogVisible = false
          this.$emit('success')
        }).finally(() => {
          this.saving = false
        })
      })
    },
    handleUploadSuccess(response, field) {
      if (response.code === 200) {
        this.form[field] = response.fileName
        this.initImageLists()
        this.$message.success('上传成功')
        return
      }
      this.$message.error(response.msg || '上传失败')
    },
    handleRemove(field) {
      this.form[field] = ''
      this.initImageLists()
    },
    getImageUrl(path) {
      if (!path) return ''
      if (path.startsWith('http') || path.startsWith('https') || path.startsWith('data:')) {
        return path
      }
      return process.env.VUE_APP_BASE_API + path
    },
    initImageLists() {
      this.tongueImageList = this.getImageList(this.form.tongueImagePath, '舌象照片')
      this.bloodTestImageList = this.getImageList(this.form.bloodTestImagePath, '血常规报告')
      this.inflammationImageList = this.getImageList(this.form.inflammationImagePath, '炎症因子报告')
      this.liverKidneyImageList = this.getImageList(this.form.liverKidneyImagePath, '肝肾功能')
      this.renalInjuryImageList = this.getImageList(this.form.renalInjuryImagePath, '肾早期损伤')
      this.tcmTreatmentImageList = this.getImageList(this.form.tcmTreatmentImagePath, '中药处方照片')
    },
    getImageList(path, name) {
      return path ? [{ name, url: this.getImageUrl(path) }] : []
    },
    clearValidate() {
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    handleClose() {
      this.form = this.emptyForm()
      this.hospitalOther = ''
      this.initImageLists()
      this.clearValidate()
    }
  }
}
</script>

<style scoped>
::v-deep .el-upload-list__item-status-label {
  display: none !important;
}
</style>
