<template>
  <div class="login">
    <div class="login-container">
      <div class="login-left">
        <div class="login-info">
          <h2 class="system-title">小儿过敏性鼻炎</h2>
          <h2 class="system-sub-title">研究数据采集平台</h2>
          <p class="system-desc">患者可直接填写问卷，医生审核通过后登录管理。</p>
          <router-link class="patient-entry" to="/patient-mobile/collect">患者直接填写</router-link>
        </div>
      </div>
      <div class="login-right">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <h3 class="title">账号登录</h3>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="请输入验证码"
              style="width: 63%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>

          <el-checkbox v-model="loginForm.rememberMe" style="margin: 0 0 25px 0;">记住密码</el-checkbox>

          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>登录中...</span>
            </el-button>
            <div class="login-links">
              <router-link v-if="register" class="link-type" :to="'/register'">医生注册</router-link>
              <router-link class="link-type" :to="'/patient-mobile/collect'">患者填写</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="el-login-footer">
      <span>Copyright © 2025 国家中医药管理局课题组 版权所有</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [{ required: true, trigger: "blur", message: "请输入账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入密码" }],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: true,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled ?? true
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username || "",
        password: password ? decrypt(password) : "",
        rememberMe: rememberMe === "true"
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(() => {})
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
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/medical-background.png");
  background-size: cover;
  background-position: center;
}

.login-container {
  display: flex;
  width: 920px;
  height: 520px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
  color: #fff;

  .system-title {
    font-size: 32px;
    margin-bottom: 10px;
    font-weight: 600;
    letter-spacing: 2px;
  }

  .system-sub-title {
    font-size: 24px;
    margin-bottom: 20px;
    font-weight: 400;
    opacity: 0.9;
  }

  .system-desc {
    font-size: 14px;
    line-height: 1.8;
    opacity: 0.9;
    margin-bottom: 24px;
  }
}

.patient-entry {
  display: inline-flex;
  width: fit-content;
  padding: 10px 18px;
  border-radius: 999px;
  color: #0f172a;
  background: #fff8dc;
  font-weight: 600;
}

.login-right {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.title {
  margin: 0 auto 30px;
  text-align: left;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.login-form {
  width: 100%;

  .el-input {
    height: 40px;

    input {
      height: 40px;
      border-radius: 4px;
      background: #f5f7fa;
      border: 1px solid transparent;

      &:focus {
        background: #fff;
        border-color: #1890ff;
      }
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}

.login-btn {
  height: 40px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 4px;
  background: linear-gradient(90deg, #1890ff, #36cfc9);
  border: none;
  transition: all 0.3s;

  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  }
}

.login-links {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}

.link-type {
  color: #409eff;
  font-size: 13px;
}

.login-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 4px;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
  background: rgba(0, 0, 0, 0.3);
}

.login-code-img {
  height: 40px;
}
</style>
