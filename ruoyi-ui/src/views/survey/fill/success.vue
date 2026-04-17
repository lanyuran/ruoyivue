<template>
  <div class="success-page">
    <el-result icon="success" title="提交成功" sub-title="问卷已提交，感谢您的参与。">
      <template slot="extra">
        <el-button type="primary" @click="$router.push('/survey/list')">返回问卷列表</el-button>
        <el-button @click="$router.push('/index')">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script>
export default {
  name: 'SurveyFillSuccess'
}
</script>

<style scoped>
.success-page {
  min-height: calc(100vh - 84px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
</style>