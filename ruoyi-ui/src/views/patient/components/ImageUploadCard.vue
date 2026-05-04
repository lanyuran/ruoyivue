<template>
  <div class="image-upload-card">
    <div v-if="previewUrl" class="image-upload-card__preview">
      <el-image
        class="image-upload-card__image"
        :src="previewUrl"
        :preview-src-list="[previewUrl]"
        fit="cover"
      >
        <div slot="error" class="image-upload-card__empty">图片加载失败</div>
      </el-image>
      <el-button type="text" icon="el-icon-delete" @click="$emit('remove')">移除图片</el-button>
    </div>
    <div v-else class="image-upload-card__empty">未上传图片</div>
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :limit="1"
      :before-upload="beforeUpload"
      :on-success="response => $emit('success', response)"
      :on-exceed="handleExceed"
    >
      <el-button size="mini" type="primary">{{ previewUrl ? '替换图片' : '上传图片' }}</el-button>
      <div slot="tip" class="el-upload__tip">只能上传 jpg/png 文件，且不超过 10MB</div>
    </el-upload>
  </div>
</template>

<script>
export default {
  name: 'ImageUploadCard',
  props: {
    fileList: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    previewUrl() {
      const first = this.fileList && this.fileList.length ? this.fileList[0] : null
      return first && first.url ? first.url : ''
    },
    uploadUrl() {
      return process.env.VUE_APP_BASE_API + '/common/upload'
    },
    uploadHeaders() {
      return {
        Authorization: 'Bearer ' + this.$store.getters.token
      }
    }
  },
  methods: {
    beforeUpload(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
      const isAllowedSize = file.size / 1024 / 1024 <= 10
      if (!isImage) {
        this.$message.error('上传图片只能是 JPG/PNG 格式')
      }
      if (!isAllowedSize) {
        this.$message.error('上传图片大小不能超过 10MB')
      }
      return isImage && isAllowedSize
    },
    handleExceed() {
      this.$message.warning('只能上传一张图片')
    }
  }
}
</script>

<style scoped>
.image-upload-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  flex-wrap: wrap;
}

.image-upload-card__preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.image-upload-card__image,
.image-upload-card__empty {
  width: 148px;
  height: 148px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  background: #f5f7fa;
}

.image-upload-card__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>
