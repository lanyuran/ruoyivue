<template>
  <div class="mobile-container">
    <div class="card">
      <div class="hero">
        <div>
          <h2>患者信息填写</h2>
          <p>无需注册，直接填写并提交；提交成功后会自动生成账号，并进入文档式详情页。</p>
        </div>
        <div class="hero-actions">
          <div class="hero-tip">自动生成账号：手机号 / 初始密码：123456</div>
          <el-button plain size="mini" @click="goHome">返回首页</el-button>
        </div>
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
          <el-form-item label="就诊医院" prop="hospital">
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
              <el-option label="其他医院" value="__OTHER__" />
            </el-select>
            <div v-if="isOtherHospital" class="hospital-other">
              <el-input
                v-model="form.hospitalOther"
                placeholder="请输入其他医院名称"
                clearable
                @input="handleHospitalOtherInput"
              />
            </div>
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
          <el-form-item label="主证" prop="mainSymptomSelections">
            <div class="choice-hint">请勾选存在的症状，勾选代表“是”，未勾选代表“否”。</div>
            <el-checkbox-group v-model="form.mainSymptomSelections" class="choice-grid">
              <el-checkbox v-for="item in mainSymptomOptions" :key="item" :label="item">{{ item }}</el-checkbox>
            </el-checkbox-group>
            <el-input
              v-if="form.mainSymptomSelections && form.mainSymptomSelections.includes('其他')"
              v-model="form.mainSymptomOther"
              class="choice-other"
              placeholder="请填写其他主证"
            />
          </el-form-item>
          <el-form-item label="共患病">
            <div class="choice-hint">请勾选已明确存在的共患病，如无可不选。</div>
            <el-checkbox-group v-model="form.comorbiditySelections" class="choice-grid">
              <el-checkbox v-for="item in comorbidityOptions" :key="item" :label="item">{{ item }}</el-checkbox>
            </el-checkbox-group>
            <el-input
              v-if="form.comorbiditySelections && form.comorbiditySelections.includes('其他')"
              v-model="form.comorbidityOther"
              class="choice-other"
              placeholder="请填写其他共患病"
            />
          </el-form-item>
          <el-form-item label="体格检查">
            <div class="choice-hint">请勾选检查中存在的体征，如无可不选。</div>
            <el-checkbox-group v-model="form.physicalExamSelections" class="choice-grid">
              <el-checkbox v-for="item in physicalExamOptions" :key="item" :label="item">{{ item }}</el-checkbox>
            </el-checkbox-group>
            <el-input
              v-if="form.physicalExamSelections && form.physicalExamSelections.includes('其他')"
              v-model="form.physicalExamOther"
              class="choice-other"
              placeholder="请填写其他体格检查结果"
            />
          </el-form-item>
          <el-form-item label="舌脉">
            <el-input v-model="form.tonguePulse" type="textarea" :rows="2" placeholder="选填" />
          </el-form-item>
          <el-form-item label="总IgE">
            <el-input v-model="form.allergenTotalIge" placeholder="选填，例如：120 IU/ml" />
          </el-form-item>
          <el-form-item label="特异性IgE">
            <el-input v-model="form.allergenSpecificIge" placeholder="选填" />
          </el-form-item>
          <el-form-item label="中医诊断" prop="tcmDiagnosis">
            <el-radio-group v-model="form.tcmDiagnosis" class="choice-stack">
              <el-radio v-for="item in tcmDiagnosisOptions" :key="item" :label="item">{{ item }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="中医治法" prop="tcmTreatment">
            <el-radio-group v-model="form.tcmTreatment" class="choice-stack">
              <el-radio v-for="item in tcmTreatmentOptions" :key="item" :label="item">{{ item }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="中医外治">
            <el-checkbox-group v-model="form.tcmExternalSelections" class="choice-stack">
              <el-checkbox v-for="item in tcmExternalOptions" :key="item" :label="item">{{ item }}</el-checkbox>
            </el-checkbox-group>
            <el-input
              v-model="form.tcmExternalOther"
              class="choice-other"
              placeholder="其他外治处方，选填"
            />
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
        <el-button plain @click="goHome">返回首页</el-button>
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
    const validateMainSymptom = (rule, value, callback) => {
      const selected = this.form && this.form.mainSymptomSelections ? this.form.mainSymptomSelections : []
      const selectedSymptoms = selected.filter(item => item !== '其他')
      const other = this.form && this.form.mainSymptomOther ? this.form.mainSymptomOther.trim() : ''
      if (selectedSymptoms.length || other) {
        callback()
        return
      }
      callback(new Error('请至少选择一项主证'))
    }
    const validateHospital = (rule, value, callback) => {
      const selected = this.form && this.form.hospitalDeptId && this.form.hospitalDeptId !== '__OTHER__'
      const otherSelected = this.form && this.form.hospitalDeptId === '__OTHER__'
      const other = this.form && this.form.hospitalOther ? this.form.hospitalOther.trim() : ''
      if (selected || otherSelected || !this.form || !this.form.hospitalDeptId) {
        callback()
        return
      }
      callback(new Error('请选择就诊医院'))
    }
    return {
      activeStep: 0,
      submitting: false,
      hospitalOptions: [],
      mainSymptomOptions: ['鼻塞', '鼻痒', '喷嚏', '流清涕', '流黄涕', '发热恶寒', '咽痒', '口干', '鼻腔干燥', '神疲乏力', '少气懒言', '自汗', '胃纳欠佳', '面色无华', '形寒肢冷', '腰膝酸软', '其他'],
      comorbidityOptions: ['鼻窦炎', '鼻息肉', '过敏性哮喘', '过敏性皮炎', '过敏性结膜炎', '过敏性咳嗽', '抽动障碍', '腺样体肥大', '分泌性中耳炎', '其他'],
      physicalExamOptions: ['鼻黏膜色红', '鼻黏膜色淡', '鼻甲肿胀', '鼻道水样分泌物', '其他'],
      tcmDiagnosisOptions: ['鼻鼽 风寒犯肺证', '鼻鼽 风热犯肺证', '鼻鼽 肺脾气虚证', '鼻鼽 肺肾阳虚证'],
      tcmTreatmentOptions: ['疏风散寒 宣通鼻窍', '疏风清热 宣通鼻窍', '补脾益肺 通窍散结', '温补肺肾 通窍散结'],
      tcmExternalOptions: [
        '香嗅疗法：鼻炎通窍散，每日闻香2次，每次2分钟，睡觉时放于枕边。',
        '耳穴压豆疗法：使用王不留行子耳穴贴，双耳穴位各选取内鼻、外鼻，每周二次，每次贴3天，中间休息1天，每天捏各穴位2次，每次100下。'
      ],
      form: {},
      rules: {
        name: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
        birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
        visitTime: [{ required: true, message: '请选择就诊日期', trigger: 'change' }],
        hospital: [{ validator: validateHospital, trigger: ['change', 'blur'] }],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        chiefComplaint: [{ required: true, message: '请输入主诉', trigger: 'blur' }],
        mainSymptomSelections: [{ validator: validateMainSymptom, trigger: 'change' }],
        tcmDiagnosis: [{ required: true, message: '请选择中医诊断', trigger: 'change' }],
        tcmTreatment: [{ required: true, message: '请选择中医治法', trigger: 'change' }]
      }
    }
  },
  created() {
    this.reset()
    this.loadHospitalOptions()
    this.refreshToken()
  },
  computed: {
    isOtherHospital() {
      return this.form && this.form.hospitalDeptId === '__OTHER__'
    }
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
        hospitalOther: undefined,
        medicalRecordNo: undefined,
        parentName: undefined,
        phone: undefined,
        pastMedication: undefined,
        chiefComplaint: undefined,
        mainSymptom: undefined,
        mainSymptomSelections: [],
        mainSymptomOther: undefined,
        comorbidity: undefined,
        comorbiditySelections: [],
        comorbidityOther: undefined,
        physicalExam: undefined,
        physicalExamSelections: [],
        physicalExamOther: undefined,
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
        tcmExternalPrescription: undefined,
        tcmExternalSelections: [],
        tcmExternalOther: undefined
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
      if (value === '__OTHER__') {
        this.form.hospital = this.form.hospitalOther ? this.form.hospitalOther.trim() : '其他医院'
        this.$nextTick(() => {
          this.$refs.form && this.$refs.form.validateField('hospital')
        })
        return
      }
      const selected = this.hospitalOptions.find(item => item.deptId === value)
      this.form.hospital = selected ? selected.deptName : undefined
      if (selected) {
        this.form.hospitalOther = undefined
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.validateField('hospital')
      })
    },
    handleHospitalOtherInput(value) {
      const hospitalOther = value ? value.trim() : ''
      if (hospitalOther) {
        this.form.hospital = hospitalOther
      } else {
        this.form.hospital = this.form.hospitalDeptId === '__OTHER__' ? '其他医院' : '其他医院'
      }
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.validateField('hospital')
      })
    },
    goHome() {
      this.$router.push('/index')
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
          this.activeStep = this.resolveInvalidStep()
          return
        }
        this.syncChoiceFields()
        this.submitting = true
        this.refreshToken().then(() => {
          return mobileSubmitInformation(this.buildSubmitPayload())
        }).then(res => {
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
    },
    resolveInvalidStep() {
      const hospitalOther = this.form.hospitalOther ? this.form.hospitalOther.trim() : ''
      const hasHospital = !this.form.hospitalDeptId || this.form.hospitalDeptId !== '__OTHER__' || this.form.hospitalDeptId === '__OTHER__' || !!hospitalOther
      if (!this.form.name || !this.form.birthDate || !this.form.visitTime || !hasHospital || !this.form.phone) {
        return 0
      }
      const selectedSymptoms = (this.form.mainSymptomSelections || []).filter(item => item !== '其他')
      const mainSymptomOther = this.form.mainSymptomOther ? this.form.mainSymptomOther.trim() : ''
      if (!this.form.chiefComplaint || (!selectedSymptoms.length && !mainSymptomOther) || !this.form.tcmDiagnosis || !this.form.tcmTreatment) {
        return 1
      }
      return 2
    },
    syncChoiceFields() {
      this.syncHospitalField()
      this.form.mainSymptom = this.buildSelectionText(this.form.mainSymptomSelections, this.form.mainSymptomOther, '')
      this.form.comorbidity = this.buildSelectionText(this.form.comorbiditySelections, this.form.comorbidityOther, '无')
      this.form.physicalExam = this.buildSelectionText(this.form.physicalExamSelections, this.form.physicalExamOther, '无')
      this.form.tcmExternalPrescription = this.buildSelectionText(this.form.tcmExternalSelections, this.form.tcmExternalOther, '')
    },
    syncHospitalField() {
      const hospitalOther = this.form.hospitalOther ? this.form.hospitalOther.trim() : ''
      if (this.form.hospitalDeptId === '__OTHER__') {
        this.form.hospital = hospitalOther || '其他医院'
        this.form.hospitalDeptId = undefined
        return
      }
      const selected = this.hospitalOptions.find(item => item.deptId === this.form.hospitalDeptId)
      this.form.hospital = selected ? selected.deptName : '其他医院'
    },
    buildSelectionText(selected, other, emptyText) {
      const result = (selected || []).filter(item => item !== '其他')
      const otherText = other ? other.trim() : ''
      if (otherText) {
        result.push(otherText)
      }
      return result.length ? result.join('、') : emptyText
    },
    buildSubmitPayload() {
      const payload = { ...this.form }
      delete payload.mainSymptomSelections
      delete payload.mainSymptomOther
      delete payload.comorbiditySelections
      delete payload.comorbidityOther
      delete payload.physicalExamSelections
      delete payload.physicalExamOther
      delete payload.tcmExternalSelections
      delete payload.tcmExternalOther
      delete payload.hospitalOther
      return payload
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

.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-wrap {
  margin-top: 20px;
}

.hospital-other {
  margin-top: 10px;
}

.upload-tip {
  margin-bottom: 16px;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 8px;
  color: #64748b;
  font-size: 13px;
}

.choice-hint {
  margin-bottom: 8px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}

.choice-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 6px 12px;
}

.choice-stack {
  display: flex;
  flex-direction: column;
  gap: 8px;
  line-height: 1.6;
}

.choice-other {
  margin-top: 10px;
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

  .hero-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-tip {
    white-space: normal;
  }

  .choice-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

}
</style>
