<template>
  <div class="detail-page" v-loading="loading">
    <div class="toolbar no-print">
      <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
      <el-button type="primary" icon="el-icon-printer" @click="handlePrint">打印</el-button>
    </div>

    <el-alert
      v-if="fromSubmit"
      class="success-banner no-print"
      type="success"
      :closable="false"
      title="提交成功，以下是本次生成的就诊文档"
      show-icon
    >
      <template slot>
        <div class="success-text">
          <span>登录账号：{{ submitUsername || detail.phone || '-' }}</span>
          <span>初始密码：{{ submitPassword || '123456' }}</span>
        </div>
      </template>
    </el-alert>

    <div v-if="detail.visitId" class="paper">
      <div class="paper-header">
        <div>
          <div class="paper-title">患者就诊信息文档</div>
          <div class="paper-subtitle">病例号：{{ detail.medicalRecordNo || '-' }}</div>
        </div>
        <div class="paper-meta">
          <div>就诊医院：{{ detail.hospital || '-' }}</div>
          <div>就诊日期：{{ formatDate(detail.visitTime) }}</div>
        </div>
      </div>

      <section class="doc-section">
        <div class="section-title">一、基础信息</div>
        <div class="doc-grid">
          <div class="doc-item"><span class="label">患者姓名</span><span class="value">{{ detail.name || '-' }}</span></div>
          <div class="doc-item"><span class="label">性别</span><span class="value">{{ detail.gender || '-' }}</span></div>
          <div class="doc-item"><span class="label">出生日期</span><span class="value">{{ formatDate(detail.birthDate) }}</span></div>
          <div class="doc-item"><span class="label">联系电话</span><span class="value">{{ detail.phone || '-' }}</span></div>
          <div class="doc-item"><span class="label">监护人姓名</span><span class="value">{{ detail.parentName || '-' }}</span></div>
          <div class="doc-item"><span class="label">录入账号</span><span class="value">{{ detail.createBy || '-' }}</span></div>
        </div>
      </section>

      <section class="doc-section">
        <div class="section-title">二、病情记录</div>
        <div class="doc-block"><span class="label">既往用药</span><div class="value multiline">{{ detail.pastMedication || '-' }}</div></div>
        <div class="doc-block"><span class="label">主诉</span><div class="value multiline">{{ detail.chiefComplaint || '-' }}</div></div>
        <div class="doc-block"><span class="label">主症</span><div class="value multiline">{{ detail.mainSymptom || '-' }}</div></div>
        <div class="doc-block"><span class="label">共患疾病</span><div class="value multiline">{{ detail.comorbidity || '-' }}</div></div>
        <div class="doc-block"><span class="label">体格检查</span><div class="value multiline">{{ detail.physicalExam || '-' }}</div></div>
        <div class="doc-block"><span class="label">舌脉</span><div class="value multiline">{{ detail.tonguePulse || '-' }}</div></div>
      </section>

      <section class="doc-section">
        <div class="section-title">三、检查与化验</div>
        <div class="doc-grid">
          <div class="doc-item"><span class="label">总 IgE</span><span class="value">{{ detail.allergenTotalIge || '-' }}</span></div>
          <div class="doc-item"><span class="label">特异性 IgE</span><span class="value">{{ detail.allergenSpecificIge || '-' }}</span></div>
        </div>
        <div class="image-grid">
          <div v-for="item in imageGroups" :key="item.key" class="image-card">
            <div class="image-label">{{ item.label }}</div>
            <img v-if="item.src" :src="item.src" :alt="item.label">
            <div v-else class="image-empty">未上传</div>
          </div>
        </div>
      </section>

      <section class="doc-section">
        <div class="section-title">四、中医诊疗</div>
        <div class="doc-block"><span class="label">中医诊断</span><div class="value multiline">{{ detail.tcmDiagnosis || '-' }}</div></div>
        <div class="doc-block"><span class="label">中医治法</span><div class="value multiline">{{ detail.tcmTreatment || '-' }}</div></div>
        <div class="doc-block"><span class="label">中医外治处方</span><div class="value multiline">{{ detail.tcmExternalPrescription || '-' }}</div></div>
      </section>
    </div>

    <el-empty v-else description="未找到对应的就诊记录" />
  </div>
</template>

<script>
import { getInformation, getMobileDetailInformation } from '@/api/patient/information'

export default {
  name: 'PatientDetail',
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  computed: {
    fromSubmit() {
      return this.$route.query.fromSubmit === '1'
    },
    submitUsername() {
      return this.$route.query.username || ''
    },
    submitPassword() {
      return this.$route.query.password || ''
    },
    imageGroups() {
      return [
        { key: 'tongueImagePath', label: '舌象照片', src: this.toImageUrl(this.detail.tongueImagePath) },
        { key: 'bloodTestImagePath', label: '血常规报告', src: this.toImageUrl(this.detail.bloodTestImagePath) },
        { key: 'inflammationImagePath', label: '炎症因子', src: this.toImageUrl(this.detail.inflammationImagePath) },
        { key: 'liverKidneyImagePath', label: '肝肾功能', src: this.toImageUrl(this.detail.liverKidneyImagePath) },
        { key: 'renalInjuryImagePath', label: '肾损伤报告', src: this.toImageUrl(this.detail.renalInjuryImagePath) },
        { key: 'tcmTreatmentImagePath', label: '中药处方', src: this.toImageUrl(this.detail.tcmTreatmentImagePath) }
      ]
    }
  },
  created() {
    this.loadDetail()
  },
  methods: {
    loadDetail() {
      const visitId = this.$route.params.visitId
      if (!visitId) {
        this.$message.error('缺少病例编号')
        return
      }
      this.loading = true
      const viewToken = this.$route.query.viewToken
      const request = viewToken ? getMobileDetailInformation(visitId, viewToken) : getInformation(visitId)
      request.then(res => {
        this.detail = res.data || {}
      }).finally(() => {
        this.loading = false
      })
    },
    toImageUrl(path) {
      if (!path) return ''
      if (path.startsWith('http')) return path
      return process.env.VUE_APP_BASE_API + path
    },
    formatDate(value) {
      return value ? this.parseTime(value, '{y}-{m}-{d}') : '-'
    },
    handlePrint() {
      window.print()
    },
    goBack() {
      if (window.history.length > 1) {
        this.$router.back()
        return
      }
      this.$router.push('/index')
    }
  }
}
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  padding: 24px 16px 40px;
  background: linear-gradient(180deg, rgba(219, 234, 254, 0.75) 0%, rgba(248, 250, 252, 0.95) 100%);
}

.toolbar {
  max-width: 960px;
  margin: 0 auto 12px;
  display: flex;
  gap: 8px;
}

.success-banner {
  max-width: 960px;
  margin: 0 auto 12px;
}

.success-text {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.paper {
  max-width: 960px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  padding: 32px 36px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.1);
  border: 1px solid #e2e8f0;
}

.paper-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  padding-bottom: 20px;
  border-bottom: 2px solid #cbd5e1;
}

.paper-title {
  font-size: 30px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: 1px;
}

.paper-subtitle {
  margin-top: 8px;
  color: #475569;
}

.paper-meta {
  text-align: right;
  color: #475569;
  line-height: 1.8;
}

.doc-section {
  margin-top: 28px;
}

.section-title {
  margin-bottom: 14px;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.doc-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 22px;
}

.doc-item,
.doc-block {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #fcfdff;
}

.doc-item {
  padding: 14px 16px;
}

.doc-block {
  padding: 14px 16px;
  margin-bottom: 12px;
}

.label {
  display: inline-block;
  min-width: 88px;
  color: #64748b;
  font-weight: 600;
}

.value {
  color: #0f172a;
}

.multiline {
  margin-top: 8px;
  white-space: pre-wrap;
  line-height: 1.8;
}

.image-grid {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.image-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px;
  background: #fcfdff;
}

.image-label {
  margin-bottom: 10px;
  color: #334155;
  font-weight: 600;
}

.image-card img,
.image-empty {
  width: 100%;
  height: 240px;
  border-radius: 6px;
  border: 1px solid #dbe2ea;
}

.image-card img {
  object-fit: cover;
}

.image-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  background: #f8fafc;
}

@media (max-width: 768px) {
  .paper {
    padding: 20px 16px;
  }

  .paper-header,
  .success-text {
    flex-direction: column;
  }

  .paper-meta {
    text-align: left;
  }

  .doc-grid,
  .image-grid {
    grid-template-columns: 1fr;
  }
}

@media print {
  .detail-page {
    padding: 0;
    background: #fff;
  }

  .no-print {
    display: none !important;
  }

  .paper {
    box-shadow: none;
    border: none;
    border-radius: 0;
    max-width: none;
    padding: 0;
  }
}
</style>
