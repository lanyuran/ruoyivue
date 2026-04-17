<template>
  <div class="patient-detail-page" v-loading="loading">
    <div class="toolbar no-print">
      <el-button icon="el-icon-arrow-left" @click="$router.back()">返回</el-button>
      <el-button type="primary" icon="el-icon-printer" @click="handlePrint">打印</el-button>
    </div>

    <div class="paper" v-if="detail.visitId">
      <h1 class="title">临床研究病例详情</h1>
      <p class="sub-title">病例号：{{ detail.medicalRecordNo || '-' }} ｜ 医院：{{ detail.hospital || '-' }}</p>

      <section class="section">
        <h2>基础信息</h2>
        <div class="grid">
          <div class="item"><label>姓名</label><span>{{ detail.name || '-' }}</span></div>
          <div class="item"><label>性别</label><span>{{ detail.gender || '-' }}</span></div>
          <div class="item"><label>出生日期</label><span>{{ formatDate(detail.birthDate) }}</span></div>
          <div class="item"><label>就诊日期</label><span>{{ formatDate(detail.visitTime) }}</span></div>
          <div class="item"><label>家长姓名</label><span>{{ detail.parentName || '-' }}</span></div>
          <div class="item"><label>联系电话</label><span>{{ detail.phone || '-' }}</span></div>
        </div>
      </section>

      <section class="section">
        <h2>病情记录</h2>
        <div class="grid single">
          <div class="item"><label>既往用药</label><span>{{ detail.pastMedication || '-' }}</span></div>
          <div class="item"><label>主诉</label><span>{{ detail.chiefComplaint || '-' }}</span></div>
          <div class="item"><label>主症</label><span>{{ detail.mainSymptom || '-' }}</span></div>
          <div class="item"><label>共患病</label><span>{{ detail.comorbidity || '-' }}</span></div>
          <div class="item"><label>体格检查</label><span>{{ detail.physicalExam || '-' }}</span></div>
          <div class="item"><label>舌脉</label><span>{{ detail.tonguePulse || '-' }}</span></div>
        </div>
      </section>

      <section class="section">
        <h2>检查与检验</h2>
        <div class="grid">
          <div class="item"><label>总 IgE</label><span>{{ detail.allergenTotalIge || '-' }}</span></div>
          <div class="item"><label>特异性 IgE</label><span>{{ detail.allergenSpecificIge || '-' }}</span></div>
        </div>
        <div class="images">
          <div class="img-item" v-for="img in imageGroups" :key="img.key">
            <label>{{ img.label }}</label>
            <img v-if="img.src" :src="img.src" :alt="img.label" />
            <div v-else class="img-empty">未上传</div>
          </div>
        </div>
      </section>

      <section class="section">
        <h2>中医诊疗</h2>
        <div class="grid single">
          <div class="item"><label>中医诊断</label><span>{{ detail.tcmDiagnosis || '-' }}</span></div>
          <div class="item"><label>中医治法</label><span>{{ detail.tcmTreatment || '-' }}</span></div>
          <div class="item"><label>中医外治处方</label><span>{{ detail.tcmExternalPrescription || '-' }}</span></div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { getInformation } from '@/api/patient/information'

export default {
  name: 'PatientDetail',
  data() {
    return {
      loading: false,
      detail: {}
    }
  },
  computed: {
    imageGroups() {
      return [
        { key: 'tongueImagePath', label: '舌象照片', src: this.toImageUrl(this.detail.tongueImagePath) },
        { key: 'bloodTestImagePath', label: '血常规报告', src: this.toImageUrl(this.detail.bloodTestImagePath) },
        { key: 'inflammationImagePath', label: '炎症因子报告', src: this.toImageUrl(this.detail.inflammationImagePath) },
        { key: 'liverKidneyImagePath', label: '肝肾功能报告', src: this.toImageUrl(this.detail.liverKidneyImagePath) },
        { key: 'renalInjuryImagePath', label: '肾损伤报告', src: this.toImageUrl(this.detail.renalInjuryImagePath) },
        { key: 'tcmTreatmentImagePath', label: '中药处方图片', src: this.toImageUrl(this.detail.tcmTreatmentImagePath) }
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
        this.$message.error('缺少病例ID')
        return
      }
      this.loading = true
      getInformation(visitId).then(res => {
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
    }
  }
}
</script>

<style scoped>
.patient-detail-page {
  padding: 16px;
  background: #f5f7fb;
}
.toolbar {
  margin-bottom: 12px;
}
.paper {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}
.title {
  margin: 0;
  font-size: 24px;
}
.sub-title {
  margin: 8px 0 0;
  color: #606266;
}
.section {
  margin-top: 20px;
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}
.section h2 {
  margin: 0 0 12px;
  font-size: 18px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 20px;
}
.grid.single {
  grid-template-columns: 1fr;
}
.item label {
  display: inline-block;
  min-width: 110px;
  color: #909399;
}
.item span {
  color: #303133;
  white-space: pre-wrap;
}
.images {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}
.img-item label {
  display: block;
  margin-bottom: 6px;
  color: #606266;
}
.img-item img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
.img-empty {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
  color: #909399;
  border-radius: 4px;
}
@media print {
  .no-print {
    display: none !important;
  }
  .patient-detail-page {
    padding: 0;
    background: #fff;
  }
  .paper {
    border-radius: 0;
    padding: 0;
  }
  .images {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>