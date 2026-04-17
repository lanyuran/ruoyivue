<template>
  <div class="app-container home-guide">
    <el-row :gutter="20">
      <el-col :xs="24" :md="14">
        <el-card class="guide-card">
          <div class="guide-title">使用说明</div>
          <ol class="guide-list">
            <li>患者无需注册，直接打开患者填写链接提交信息。</li>
            <li>患者提交完成后，系统会自动以手机号生成账号，初始密码为 `123456`。</li>
            <li>医生需要先注册账号，审核通过后再登录系统。</li>
            <li>主管和管理员可在权限范围内查看数据与进行管理。</li>
          </ol>
        </el-card>

        <el-card class="guide-card">
          <div class="guide-title">推荐流程</div>
          <div class="guide-steps">
            <div class="guide-step">
              <div class="step-index">1</div>
              <div class="step-body">
                <div class="step-title">患者填写</div>
                <div class="step-desc">通过问卷式页面直接填写病例信息并上传图片资料。</div>
              </div>
            </div>
            <div class="guide-step">
              <div class="step-index">2</div>
              <div class="step-body">
                <div class="step-title">系统建号</div>
                <div class="step-desc">提交成功后自动生成患者账号，便于后续登录查看自己的提交记录。</div>
              </div>
            </div>
            <div class="guide-step">
              <div class="step-index">3</div>
              <div class="step-body">
                <div class="step-title">医生管理</div>
                <div class="step-desc">医生或主管登录后在权限范围内查看、审核和管理数据。</div>
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
      roleList: [
        { role: '患者', access: '直接填写、查看本人提交记录' },
        { role: '医生', access: '管理本人或本院授权范围内数据' },
        { role: '主管', access: '查看本院数据并进行用户/医院相关管理' },
        { role: '管理员', access: '拥有全量配置与系统管理权限' }
      ]
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

.guide-steps {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.guide-step {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.step-index {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.step-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.step-desc {
  color: #606266;
  font-size: 13px;
}
</style>
