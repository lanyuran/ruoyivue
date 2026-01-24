<template>
  <div class="app-container home-guide">
    <el-row :gutter="20">
      <el-col :xs="24" :md="14">
        <el-card class="guide-card">
          <div class="guide-title">使用说明</div>
          <ol class="guide-list">
            <li>注册账号：默认用户级，选择医生级会进入科室主管审批流程。</li>
            <li>数据上传：录入/导入自己的病例数据，支持图片上传与修改。</li>
            <li>数据查询：医生及以上角色可在权限范围内检索与查看数据。</li>
            <li>系统管理：主管及以上角色可进行用户与医院管理。</li>
          </ol>
        </el-card>
        <el-card class="guide-card">
          <div class="guide-title">操作流程</div>
          <div class="guide-steps">
            <div class="guide-step">
              <div class="step-index">1</div>
              <div class="step-body">
                <div class="step-title">注册/登录</div>
                <div class="step-desc">选择身份并完成注册，登录后进入系统。</div>
              </div>
            </div>
            <div class="guide-step">
              <div class="step-index">2</div>
              <div class="step-body">
                <div class="step-title">数据上传</div>
                <div class="step-desc">在数据上传页录入或导入病例信息，上传图片资料。</div>
              </div>
            </div>
            <div class="guide-step">
              <div class="step-index">3</div>
              <div class="step-body">
                <div class="step-title">数据查询</div>
                <div class="step-desc">医生及以上角色可按姓名/病历号等条件查询。</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card class="guide-card">
          <div class="guide-title">权限等级</div>
          <el-table :data="roleList" size="small" border>
            <el-table-column label="等级" prop="role" width="110" />
            <el-table-column label="可访问页面" prop="access" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      roleList: [
        { role: "用户级", access: "首页、数据上传" },
        { role: "科室医生级", access: "首页、数据上传、数据查询" },
        { role: "科室主管级", access: "首页、数据上传、数据查询、用户管理" },
        { role: "院区主管级", access: "首页、数据上传、数据查询、用户管理、医院管理" },
        { role: "管理员级", access: "全部功能与权限控制" }
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
  line-height: 1.8;
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
