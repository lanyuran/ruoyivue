<template>
  <div class="mobile-container">
    <div class="card">
      <h2>移动端病例采集</h2>
      <el-steps :active="activeStep" finish-status="success" simple>
        <el-step title="基础信息" />
        <el-step title="病情信息" />
        <el-step title="图片上传" />
      </el-steps>

      <el-form ref="form" :model="form" :rules="rules" label-width="96px" class="form-wrap">
        <div v-show="activeStep === 0">
          <el-form-item label="患者姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="性别"><el-select v-model="form.gender" placeholder="请选择"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
          <el-form-item label="出生日期" prop="birthDate"><el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" /></el-form-item>
          <el-form-item label="就诊日期" prop="visitTime"><el-date-picker v-model="form.visitTime" type="date" value-format="yyyy-MM-dd" style="width: 100%" /></el-form-item>
          <el-form-item label="就诊医院" prop="hospitalDeptId">
            <el-select v-model="form.hospitalDeptId" clearable filterable placeholder="请选择医院" style="width: 100%" @change="handleHospitalChange">
              <el-option v-for="item in hospitalOptions" :key="item.deptId" :label="item.deptName" :value="item.deptId" />
            </el-select>
          </el-form-item>
          <el-form-item label="其他医院">
            <el-input v-model="hospitalOther" placeholder="没有可选医院时填写" @input="handleOtherHospitalInput" />
          </el-form-item>
          <el-form-item label="病例号" prop="medicalRecordNo"><el-input v-model="form.medicalRecordNo" /></el-form-item>
          <el-form-item label="联系电话" prop="phone"><el-input v-model="form.phone" maxlength="11" /></el-form-item>
          <el-form-item label="家长姓名"><el-input v-model="form.parentName" /></el-form-item>
        </div>

        <div v-show="activeStep === 1">
          <el-form-item label="既往用药"><el-input v-model="form.pastMedication" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="主诉" prop="chiefComplaint"><el-input v-model="form.chiefComplaint" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="主症" prop="mainSymptom"><el-input v-model="form.mainSymptom" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="共患病"><el-input v-model="form.comorbidity" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="体格检查"><el-input v-model="form.physicalExam" type="textarea" :rows="2" /></el-form-item>
          <el-form-item label="舌脉"><el-input v-model="form.tonguePulse" type="textarea" :rows="2" /></el-form-item>
        </div>

        <div v-show="activeStep === 2">
          <el-form-item label="舌象照片"><image-upload v-model="form.tongueImagePath" :limit="1" /></el-form-item>
          <el-form-item label="血常规报告"><image-upload v-model="form.bloodTestImagePath" :limit="1" /></el-form-item>
          <el-form-item label="炎症因子"><image-upload v-model="form.inflammationImagePath" :limit="1" /></el-form-item>
          <el-form-item label="肝肾功能"><image-upload v-model="form.liverKidneyImagePath" :limit="1" /></el-form-item>
          <el-form-item label="肾损伤报告"><image-upload v-model="form.renalInjuryImagePath" :limit="1" /></el-form-item>
          <el-form-item label="中药处方"><image-upload v-model="form.tcmTreatmentImagePath" :limit="1" /></el-form-item>
          <el-form-item label="总IgE"><el-input v-model="form.allergenTotalIge" /></el-form-item>
          <el-form-item label="特异性IgE"><el-input v-model="form.allergenSpecificIge" /></el-form-item>
          <el-form-item label="中医诊断"><el-input v-model="form.tcmDiagnosis" /></el-form-item>
          <el-form-item label="中医治法"><el-input v-model="form.tcmTreatment" /></el-form-item>
          <el-form-item label="中医外治处方"><el-input v-model="form.tcmExternalPrescription" type="textarea" :rows="2" /></el-form-item>
        </div>
      </el-form>

      <div class="actions">
        <el-button @click="handlePrev" :disabled="activeStep === 0">上一步</el-button>
        <el-button v-if="activeStep < 2" type="primary" @click="handleNext">下一步</el-button>
        <el-button v-else type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { mobileSubmitInformation, getMobileSubmitToken, listHospitalOptions } from '@/api/patient/information'

export default {
  name: 'PatientMobileCollect',
  data() {
    return {
      activeStep: 0,
      submitting: false,
      hospitalOptions: [],
      hospitalOther: '',
      form: {},
      rules: {
        name: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
        birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
        visitTime: [{ required: true, message: '请选择就诊日期', trigger: 'change' }],
        medicalRecordNo: [{ required: true, message: '请输入病例号', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        chiefComplaint: [{ required: true, message: '请输入主诉', trigger: 'blur' }],
        mainSymptom: [{ required: true, message: '请输入主症', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.reset()
    this.loadHospitalOptions()
    this.refreshToken()
  },
  methods: {
    reset() {
      this.form = {
        requestToken: '',
        name: undefined,
        gender: undefined,
        birthDate: undefined,
        visitTime: undefined,
        hospitalDeptId: undefined,
        hospital: undefined,
        medicalRecordNo: undefined,
        parentName: undefined,
        phone: undefined,
        pastMedication: undefined,
        chiefComplaint: undefined,
        mainSymptom: undefined,
        comorbidity: undefined,
        physicalExam: undefined,
        tonguePulse: undefined,
        tongueImagePath: undefined,
        allergenTotalIge: undefined,
        allergenSpecificIge: undefined,
        bloodTestImagePath: undefined,
        inflammationImagePath: undefined,
        liverKidneyImagePath: undefined,
        renalInjuryImagePath: undefined,
        tcmDiagnosis: undefined,
        tcmTreatment: undefined,
        tcmTreatmentImagePath: undefined,
        tcmExternalPrescription: undefined
      }
      this.hospitalOther = ''
    },
    loadHospitalOptions() {
      listHospitalOptions().then(res => {
        this.hospitalOptions = res.data || []
      }).catch(() => {
        this.hospitalOptions = []
      })
    },
    refreshToken() {
      return getMobileSubmitToken().then(res => {
        this.form.requestToken = res.requestToken
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
      } else if (this.hospitalOther) {
        this.form.hospital = this.hospitalOther.trim()
      }
    },
    handlePrev() {
      if (this.activeStep > 0) this.activeStep -= 1
    },
    handleNext() {
      this.activeStep += 1
    },
    handleSubmit() {
      this.normalizeHospitalInput()
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.activeStep = 0
          return
        }
        this.submitting = true
        mobileSubmitInformation(this.form).then(() => {
          this.$message.success('提交成功')
          this.reset()
          this.activeStep = 0
          return this.refreshToken()
        }).catch(() => {
          return this.refreshToken()
        }).finally(() => {
          this.submitting = false
        })
      })
    }
  }
}
</script>

<style scoped>
.mobile-container {
  padding: 12px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}
.card {
  max-width: 820px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}
h2 {
  margin: 0 0 16px;
}
.form-wrap {
  margin-top: 16px;
}
.actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
@media (max-width: 768px) {
  .card {
    padding: 12px;
  }
}
</style>