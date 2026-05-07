<template>
  <div class="app-container home-guide">
    <el-row :gutter="20">
      <el-col :xs="24" :md="14">
        <el-card class="guide-card">
          <div class="guide-title">使用说明</div>
          <ol class="guide-list">
            <li>患者无需注册，可直接通过患者问卷入口填写并提交信息。</li>
            <li>患者提交完成后，系统会自动以手机号生成账号，初始密码为 123456。</li>
            <li>医生需先注册账号，经管理员审核通过后方可生效并登录使用。</li>
            <li>数据上传页用于显示并管理全部导入或录入的数据。</li>
            <li>数据查询页用于显示当前权限范围内可查看的数据。</li>
          </ol>
        </el-card>

        <el-card class="guide-card">
          <div class="guide-title">患者问卷入口</div>
          <div class="questionnaire-entry">
            <div class="questionnaire-entry__row">
              <div class="questionnaire-entry__content">患者无需注册，可直接通过患者问卷入口填写并提交信息。</div>
              <div class="questionnaire-entry__action">
                <el-button type="primary" @click="openFillPage">直接填写</el-button>
              </div>
            </div>
            <div class="questionnaire-entry__row">
              <div class="questionnaire-entry__content questionnaire-entry__link">{{ fillUrl }}</div>
              <div class="questionnaire-entry__action">
                <el-button plain @click="copyFillLink">复制链接</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="10">
        <el-card class="guide-card">
          <div class="guide-title">当前角色</div>
          <el-table :data="roleList" size="small" border>
            <el-table-column label="角色" prop="role" width="110" />
            <el-table-column label="主要权限" prop="access" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      fillUrl: `${window.location.origin}/patient-mobile/collect`,
      roleList: [
        { role: '患者', access: '直接填写、查看本人提交记录' },
        { role: '医生', access: '管理本人或本院授权范围内数据' },
        { role: '主管', access: '查看本院数据并进行用户/医院相关管理' },
        { role: '管理员', access: '拥有全量配置与系统管理权限' }
      ]
    }
  },
  methods: {
    openFillPage() {
      window.open(this.fillUrl, '_blank')
    },
    copyFillLink() {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(this.fillUrl).then(() => {
          this.$modal.msgSuccess('链接已复制')
        }).catch(() => {
          this.fallbackCopy()
        })
        return
      }
      this.fallbackCopy()
    },
    fallbackCopy() {
      const input = document.createElement('input')
      input.value = this.fillUrl
      document.body.appendChild(input)
      input.select()
      document.execCommand('copy')
      document.body.removeChild(input)
      this.$modal.msgSuccess('链接已复制')
    }
  }
}
</script>

<style scoped>
.home-guide {
  color: #303133;
}

.guide-card {
  margin-bottom: 20px;
}

.guide-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}

.guide-list {
  padding-left: 18px;
  margin: 0;
  line-height: 1.9;
}

.questionnaire-entry {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.questionnaire-entry__row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 4px;
  align-items: center;
}

.questionnaire-entry__content {
  color: #606266;
  line-height: 1.8;
}

.questionnaire-entry__link {
  color: #2563eb;
  word-break: break-all;
}

.questionnaire-entry__action {
  display: flex;
  justify-content: flex-end;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .home-guide {
    padding: 0;
  }

  .guide-card {
    margin-bottom: 14px;
  }

  .guide-title {
    font-size: 17px;
    margin-bottom: 10px;
  }

  .guide-list {
    padding-left: 16px;
    line-height: 1.75;
    font-size: 14px;
  }

  .questionnaire-entry {
    gap: 12px;
  }

  .questionnaire-entry__row {
    grid-template-columns: 1fr;
    gap: 6px;
  }

  .questionnaire-entry__content {
    font-size: 14px;
    line-height: 1.7;
  }

  .questionnaire-entry__action {
    justify-content: flex-start;
  }

  .questionnaire-entry__action .el-button {
    min-width: 96px;
    height: 36px;
    padding: 0 16px;
  }
}
</style>
