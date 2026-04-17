<template>
  <div class="mobile-container">
    <div class="card">
      <div class="hero">
        <div>
          <h2>患者信息填写</h2>
          <p>无需注册，直接填写并提交；提交成功后会自动生成账号，并进入文档式详情页。</p>
        </div>
        <div class="hero-tip">自动生成账号：手机号 / 初始密码：123456</div>
      </div>

      <el-steps :active="activeStep" finish-status="success" simple>
        <el-step title="基础信息" />
        <el-step title="病情信息" />
        <el-step title="图片资料" />
      </el-steps>

      <el-form ref="form" :model="form" :rules="rules" label-width="100px" class="form-wrap">
        <div v-show="activeStep === 0">
          <el-form-item label="患者姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入患者姓名" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
          <el-form-item label="就诊日期" prop="visitTime">
            <el-date-picker v-model="form.visitTime" type="date" value-format="yyyy-MM-dd" style="width: 100%" />
          </el-form-item>
          <el-form-item label="就诊医院" prop="hospitalDeptId">
            <el-select
              v-model="form.hospitalDeptId"
              clearable
              filterable
              placeholder="请选择就诊医院"
              style="width: 100%"
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
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" maxlength="11" placeholder="请输入手机号，提交后会作为登录账号" />
          </el-form-item>
          <el-form-item label="监护人姓名">
            <el-input v-model="form.parentName" placeholder="选填" />
          </el-form-item>
          <el-form-item label="病历号">
            <el-input v-model="form.medicalRecordNo" placeholder="选填，不填则自动生成" />
          </el-form-item>
        </div>

        <div v-show="activeStep === 1">
          <el-form-item label="既往用药">
            <el-input v-model="form.pastMedication" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
          <el-form-item label="主诉" prop="chiefComplaint">
            <el-input v-model="form.chiefComplaint" type="textarea" :rows="3" placeholder="请描述主要不适" />
          </el-form-item>
          <el-form-item label="主症" prop="mainSymptom">
            <el-input v-model="form.mainSymptom" type="textarea" :rows="3" placeholder="请描述主要症状" />
          </el-form-item>
          <el-form-item label="共患疾病">
            <el-input v-model="form.comorbidity" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
          <el-form-item label="体格检查">
            <el-input v-model="form.physicalExam" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
          <el-form-item label="舌脉">
            <el-input v-model="form.tonguePulse" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
        </div>

        <div v-show="activeStep === 2">
          <div class="upload-tip">图片资料均为选填，可先提交基础信息，后续再补充。</div>
          <el-form-item label="舌象照片">
            <image-upload v-model="form.tongueImagePath" :limit="1" action="/patient/information/mobile/upload" />
          </el-form-item>
          <el-form-item label="血常规报告">
            <image-upload v-model="form.bloodTestImagePath" :limit="1" action="/patient/information/mobile/upload" />
          </el-form-item>
          <el-form-item label="炎症因子">
            <image-upload v-model="form.inflammationImagePath" :limit="1" action="/patient/information/mobile/upload" />
          </el-form-item>
          <el-form-item label="肝肾功能">
            <image-upload v-model="form.liverKidneyImagePath" :limit="1" action="/patient/information/mobile/upload" />
          </el-form-item>
          <el-form-item label="肾损伤报告">
            <image-upload v-model="form.renalInjuryImagePath" :limit="1" action="/patient/information/mobile/upload" />
          </el-form-item>
          <el-form-item label="中药处方">
            <image-upload v-model="form.tcmTreatmentImagePath" :limit="1" action="/patient/information/mobile/upload" />
          </el-form-item>
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
      form: {},
      rules: {
        name: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
        birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
        visitTime: [{ required: true, message: '请选择就诊日期', trigger: 'change' }],
        hospitalDeptId: [{ required: true, message: '请选择就诊医院', trigger: 'change' }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
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
      this.form.hospital = selected ? selected.deptName : undefined
    },
    handlePrev() {
      if (this.activeStep > 0) {
        this.activeStep -= 1
      }
    },
    handleNext() {
      this.activeStep += 1
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.activeStep = 0
          return
        }
        this.submitting = true
        mobileSubmitInformation(this.form).then(res => {
          this.$message.success('提交成功，正在进入详情页')
          this.$router.replace({
            path: `/patient-mobile/detail/${res.visitId}`,
            query: {
              viewToken: res.viewToken,
              fromSubmit: '1',
              username: res.username,
              password: res.password
            }
          })
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
  min-height: 100vh;
  padding: 16px;
  background: linear-gradient(180deg, #eff6ff 0%, #f8fafc 100%);
}

.card {
  max-width: 860px;
  margin: 0 auto;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.hero {
  margin-bottom: 20px;
  padding: 16px 18px;
  border-radius: 12px;
  background: linear-gradient(135deg, #dbeafe 0%, #fef3c7 100%);
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
}

.hero h2 {
  margin: 0 0 6px;
  color: #0f172a;
}

.hero p {
  margin: 0;
  color: #334155;
}

.hero-tip {
  font-size: 13px;
  color: #92400e;
  background: rgba(255, 255, 255, 0.72);
  padding: 10px 12px;
  border-radius: 999px;
  white-space: nowrap;
}

.form-wrap {
  margin-top: 20px;
}

.upload-tip {
  margin-bottom: 16px;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 8px;
  color: #64748b;
  font-size: 13px;
}

.actions {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 768px) {
  .card {
    padding: 14px;
  }

  .hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-tip {
    white-space: normal;
  }
}
</style>
