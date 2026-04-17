<template>
  <div class="fill-page" v-loading="loading">
    <div class="fill-card" v-if="survey.title">
      <h1>{{ survey.title }}</h1>
      <el-alert v-if="survey.submitted" type="success" :closable="false" title="您已提交过该问卷，本页面仅可查看。" show-icon />

      <el-form ref="form" :model="answerForm" label-width="120px" class="q-form">
        <el-form-item v-for="(q, idx) in questions" :key="q.key" :label="`${idx + 1}. ${q.label}`">
          <component
            :is="q.component"
            v-model="answerForm[q.key]"
            v-bind="q.props"
            :disabled="survey.submitted || submitting"
          >
            <template v-if="q.component === 'el-radio-group'">
              <el-radio v-for="op in q.options" :key="op.value" :label="op.value">{{ op.label }}</el-radio>
            </template>
            <template v-if="q.component === 'el-checkbox-group'">
              <el-checkbox v-for="op in q.options" :key="op.value" :label="op.value">{{ op.label }}</el-checkbox>
            </template>
            <template v-if="q.component === 'el-select'">
              <el-option v-for="op in q.options" :key="op.value" :label="op.label" :value="op.value" />
            </template>
          </component>
        </el-form-item>
      </el-form>

      <div class="actions">
        <el-button @click="$router.push('/survey/list')">返回列表</el-button>
        <el-button type="primary" :disabled="survey.submitted" :loading="submitting" @click="handleSubmit">提交问卷</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getSurveyFill, submitSurveyFill } from '@/api/survey/survey'

export default {
  name: 'SurveyFill',
  data() {
    return {
      loading: false,
      submitting: false,
      surveyId: undefined,
      survey: {
        title: '',
        content: '[]',
        submitted: false
      },
      questions: [],
      answerForm: {}
    }
  },
  created() {
    this.surveyId = this.$route.params.surveyId
    this.loadSurvey()
  },
  methods: {
    loadSurvey() {
      this.loading = true
      getSurveyFill(this.surveyId).then(res => {
        this.survey = res.data || {}
        this.buildQuestions(this.survey.content)
      }).finally(() => {
        this.loading = false
      })
    },
    buildQuestions(content) {
      let raw = []
      try {
        raw = JSON.parse(content || '[]')
      } catch (e) {
        raw = []
      }
      const flat = []
      const walk = list => {
        ;(list || []).forEach(item => {
          if (!item) return
          if (Array.isArray(item.children) && item.children.length) {
            walk(item.children)
          }
          if (item.vModel && item.label) {
            flat.push(item)
          }
        })
      }
      walk(raw)
      this.questions = flat.map((item, index) => {
        const options = (item.options || []).map(op => ({
          label: op.label,
          value: op.value
        }))
        const normalized = {
          key: item.vModel || ('q_' + index),
          label: item.label,
          tag: item.tag || '',
          options,
          component: 'el-input',
          props: {
            placeholder: item.placeholder || ('请输入' + item.label),
            clearable: true
          }
        }

        if (item.tag === 'el-input') {
          normalized.component = 'el-input'
          normalized.props.type = item.type || 'text'
          if (item.type === 'textarea') {
            normalized.props.rows = item.rows || 3
          }
        } else if (item.tag === 'el-radio-group') {
          normalized.component = 'el-radio-group'
          normalized.props = {}
        } else if (item.tag === 'el-checkbox-group') {
          normalized.component = 'el-checkbox-group'
          normalized.props = {}
        } else if (item.tag === 'el-select') {
          normalized.component = 'el-select'
          normalized.props = {
            placeholder: item.placeholder || ('请选择' + item.label),
            clearable: true,
            filterable: true
          }
        } else if (item.tag === 'el-date-picker') {
          normalized.component = 'el-date-picker'
          normalized.props = {
            type: item.type || 'date',
            placeholder: item.placeholder || '请选择日期',
            valueFormat: 'yyyy-MM-dd',
            style: 'width:100%'
          }
        }

        if (item.tag === 'el-checkbox-group') {
          this.$set(this.answerForm, normalized.key, Array.isArray(item.defaultValue) ? item.defaultValue : [])
        } else {
          this.$set(this.answerForm, normalized.key, item.defaultValue === undefined ? '' : item.defaultValue)
        }
        return normalized
      })
    },
    handleSubmit() {
      const answers = this.questions.map((q, idx) => {
        const val = this.answerForm[q.key]
        const valueText = Array.isArray(val) ? val.join(',') : (val === undefined || val === null ? '' : String(val))
        return {
          questionKey: q.key,
          questionLabel: q.label,
          questionType: q.tag,
          answerValue: valueText,
          optionValue: valueText,
          sortNo: idx + 1
        }
      })

      this.submitting = true
      submitSurveyFill(this.surveyId, { answers }).then(() => {
        this.$message.success('提交成功')
        this.$router.replace({ path: '/survey/fill-success', query: { surveyId: this.surveyId } })
      }).finally(() => {
        this.submitting = false
      })
    }
  }
}
</script>

<style scoped>
.fill-page {
  min-height: calc(100vh - 84px);
  background: #f5f7fa;
  padding: 20px;
}
.fill-card {
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
.q-form {
  margin-top: 16px;
}
.actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>