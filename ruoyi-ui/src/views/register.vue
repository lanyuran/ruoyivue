<template>
  <div class="register">
    <div class="register-overlay"></div>
    <el-form
      ref="registerForm"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
    >
      <h3 class="title">国家中管局研究课题：</h3>
      <h3 class="title">小儿过敏性鼻炎病证研究数据库</h3>
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          type="text"
          auto-complete="off"
          placeholder="请输入账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          auto-complete="off"
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
          auto-complete="off"
          placeholder="请确认密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>

      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="registerForm.code"
          auto-complete="off"
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

      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="text-align: right; margin-top: 10px;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>

    <div class="el-register-footer">
      <span>Copyright © 2025 国家中医药管理局课题组 版权所有</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register } from "@/api/login"

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"))
      } else {
        callback()
      }
    }
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        code: "",
        uuid: ""
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: '用户账号长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
          { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { validator: equalToPassword, trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
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
          this.codeUrl = "data:image/gif;base64," + res.img
          this.registerForm.uuid = res.uuid
        }
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          register(this.registerForm).then(res => {
            const username = this.registerForm.username
            this.$alert("<font color='green'>恭喜你，账号 " + username + " 注册成功！</font>", '系统提示', {
              dangerouslyUseHTMLString: true,
              type: 'success'
            }).then(() => {
              this.$router.push("/login")
            }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) this.getCode()
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
  background: url("../assets/images/medical-background.png") no-repeat center center;
  background-size: cover;
  overflow: hidden;
}

/* 半透明淡蓝叠层，制造柔和科技感 */
.register-overlay {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(240, 248, 255, 0.65);
  backdrop-filter: blur(6px);
  z-index: 1;
}

/* 注册表单样式 */
.register-form {
  position: relative;
  z-index: 2;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  width: 400px;
  padding: 40px 30px 20px 30px;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 10px 28px rgba(0, 0, 0, 0.25);
  }

  .title {
    margin-bottom: 30px;
    text-align: center;
    color: #2a75bb;
    font-weight: 600;
    font-size: 22px;
  }

  .el-input {
    height: 42px;
    input {
      height: 42px;
      background-color: rgba(255, 255, 255, 0.9);
      border-radius: 8px;
    }
  }

  .input-icon {
    height: 42px;
    width: 16px;
    color: #2a75bb;
  }

  .el-button--primary {
    background: linear-gradient(90deg, #2a75bb, #67c1f5);
    border: none;
    border-radius: 10px;
    font-weight: 600;
    letter-spacing: 1px;
  }

  .el-button--primary:hover {
    background: linear-gradient(90deg, #318ce7, #7dd3fc);
  }

  .link-type {
    color: #2a75bb;
    font-size: 13px;
    text-decoration: underline;
  }
}

.register-code {
  width: 33%;
  float: right;
  height: 33px;

  img {
    cursor: pointer;
    border-radius: 6px;
    border: 1px solid #dbeafe;
    background-color: #f0f9ff;
  }
}

.el-register-footer {
  position: fixed;
  bottom: 10px;
  width: 100%;
  text-align: center;
  color: #e0f2fe;
  font-size: 12px;
  letter-spacing: 1px;
  z-index: 2;
}
.register-code-img {
  height: 42px;   /* 调小高度 */
  width: auto;    /* 保持比例自动缩放 */
  cursor: pointer;  /* 鼠标悬停显示为手型 */
  vertical-align: middle;
}

</style>
