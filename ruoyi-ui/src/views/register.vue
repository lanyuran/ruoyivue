<template>
  <div class="register">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">临床研究数据采集系统</h3>
      <p class="subtitle">手机号即登录账号</p>

      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          type="text"
          autocomplete="off"
          placeholder="请输入手机号"
        >
          <svg-icon slot="prefix" icon-class="phone" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>

      <el-form-item prop="registerRole">
        <el-radio-group v-model="registerForm.registerRole" class="register-role-group">
          <el-radio label="user">患者账号</el-radio>
          <el-radio label="doctor">医生账号</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="registerForm.registerRole === 'doctor'" prop="deptId">
        <treeselect
          v-model="registerForm.deptId"
          :options="deptOptions"
          :show-count="true"
          placeholder="请选择所属科室"
        />
        <div class="register-tip">医生账号提交后需要科室主管审批。</div>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          autocomplete="off"
          placeholder="请输入密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>

      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          autocomplete="off"
          placeholder="请再次输入密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>

      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="registerForm.code"
          autocomplete="off"
          placeholder="请输入验证码"
          style="width: 63%"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img" />
        </div>
      </el-form-item>

      <el-form-item style="width: 100%">
        <el-button :loading="loading" size="medium" type="primary" style="width: 100%" @click.native.prevent="handleRegister">
          <span v-if="!loading">注 册</span>
          <span v-else>注册中...</span>
        </el-button>
        <div style="text-align: right; margin-top: 10px;">
          <router-link class="link-type" :to="'/login'">已有账号，去登录</router-link>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getCodeImg, getRegisterDeptTree, register } from '@/api/login'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'Register',
  components: { Treeselect },
  data() {
    const mobileReg = /^1[3-9]\d{9}$/
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    const validateDept = (rule, value, callback) => {
      if (this.registerForm.registerRole === 'doctor' && !value) {
        callback(new Error('请选择所属科室'))
      } else {
        callback()
      }
    }
    return {
      codeUrl: '',
      deptOptions: [],
      registerForm: {
        username: '',
        registerRole: 'user',
        deptId: undefined,
        password: '',
        confirmPassword: '',
        code: '',
        uuid: ''
      },
      registerRules: {
        username: [
          { required: true, trigger: 'blur', message: '请输入手机号' },
          { pattern: mobileReg, trigger: 'blur', message: '请输入正确的手机号' }
        ],
        registerRole: [{ required: true, trigger: 'change', message: '请选择注册身份' }],
        deptId: [{ validator: validateDept, trigger: 'change' }],
        password: [
          { required: true, trigger: 'blur', message: '请输入密码' },
          { min: 5, max: 20, message: '密码长度必须在 5 到 20 个字符之间', trigger: 'blur' },
          { pattern: /^[^<>"'|\\]+$/, message: '密码不能包含非法字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, trigger: 'blur', message: '请再次输入密码' },
          { validator: equalToPassword, trigger: 'blur' }
        ],
        code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
      },
      loading: false,
      captchaEnabled: true
    }
  },
  watch: {
    'registerForm.registerRole'(value) {
      if (value === 'doctor' && this.deptOptions.length === 0) {
        this.getDeptTree()
      } else if (value !== 'doctor') {
        this.registerForm.deptId = undefined
      }
    }
  },
  created() {
    this.getCode()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = 'data:image/gif;base64,' + res.img
          this.registerForm.uuid = res.uuid
        }
      })
    },
    getDeptTree() {
      getRegisterDeptTree().then(res => {
        this.deptOptions = res.data || []
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (!valid) return
        this.loading = true
        if (this.registerForm.registerRole !== 'doctor') {
          this.registerForm.deptId = undefined
        }
        register(this.registerForm).then(() => {
          const username = this.registerForm.username
          this.$alert("<font color='green'>账号 " + username + " 注册成功</font>", '系统提示', {
            dangerouslyUseHTMLString: true,
            type: 'success'
          }).then(() => {
            this.$router.push('/login')
          }).catch(() => {})
        }).catch(() => {
          this.loading = false
          if (this.captchaEnabled) this.getCode()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(150deg, #dbeafe 0%, #f8fafc 100%);
  padding: 24px;
}
.register-form {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.12);
  padding: 28px 24px 12px;
}
.title {
  margin: 0;
  text-align: center;
  color: #1f2937;
}
.subtitle {
  margin: 8px 0 20px;
  color: #6b7280;
  text-align: center;
  font-size: 13px;
}
.register-role-group {
  display: flex;
  gap: 24px;
}
.register-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}
.input-icon {
  color: #409eff;
}
.link-type {
  color: #409eff;
  font-size: 13px;
}
.register-code {
  width: 33%;
  float: right;
  height: 40px;
}
.register-code-img {
  height: 40px;
  width: 100%;
  object-fit: cover;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}
</style>