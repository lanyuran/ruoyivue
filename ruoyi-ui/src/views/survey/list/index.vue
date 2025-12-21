<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="问卷标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入问卷标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['survey:survey:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['survey:survey:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['survey:survey:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="surveyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="问卷ID" align="center" prop="surveyId" />
      <el-table-column label="问卷标题" align="center" prop="title" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['survey:survey:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['survey:survey:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-share"
            @click="handleShare(scope.row)"
          >分享</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 分享对话框 -->
    <el-dialog :title="shareTitle" :visible.sync="shareOpen" width="400px" append-to-body>
        <div style="text-align: center;">
            <div style="margin-bottom: 20px;">使用微信扫码填写问卷</div>
            <img :src="qrCodeUrl" style="width: 200px; height: 200px;" />
            <div style="margin-top: 10px;">
                <el-link :href="shareLink" target="_blank" type="primary">{{ shareLink }}</el-link>
            </div>
            <div style="margin-top: 20px;">
                <el-button type="primary" size="small" icon="el-icon-download" @click="downloadQrCode">下载二维码</el-button>
            </div>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="shareOpen = false">关 闭</el-button>
        </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSurvey, delSurvey } from "@/api/survey/survey";

export default {
  name: "Survey",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 问卷表格数据
      surveyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        status: null,
      },
      // 分享相关
      shareOpen: false,
      shareTitle: "问卷分享",
      qrCodeUrl: "",
      shareLink: ""
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询问卷列表 */
    getList() {
      this.loading = true;
      listSurvey(this.queryParams).then(response => {
        this.surveyList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.surveyId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push("/survey/form");
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const surveyId = row.surveyId || this.ids
      this.$router.push("/survey/form/" + surveyId);
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const surveyIds = row.surveyId || this.ids;
      this.$modal.confirm('是否确认删除问卷编号为"' + surveyIds + '"的数据项？').then(function() {
        return delSurvey(surveyIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 分享按钮操作 */
    handleShare(row) {
        this.shareTitle = "分享问卷：" + row.title;
        // 假设有一个填写的路由 /survey/fill/:id
        // 由于是演示，我们使用当前域名的HASH模式
        const baseUrl = window.location.origin + window.location.pathname + "#/survey/fill/" + row.surveyId;
        this.shareLink = baseUrl;
        // 使用 QR Server 生成二维码
        this.qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + encodeURIComponent(baseUrl);
        this.shareOpen = true;
    },
    /** 下载二维码 */
    downloadQrCode() {
        if (!this.qrCodeUrl) return;
        
        // 创建一个 Image 对象
        const image = new Image();
        image.crossOrigin = "Anonymous"; // 解决跨域问题
        image.src = this.qrCodeUrl;
        image.onload = () => {
            // 创建 canvas
            const canvas = document.createElement("canvas");
            canvas.width = image.width;
            canvas.height = image.height;
            const ctx = canvas.getContext("2d");
            ctx.drawImage(image, 0, 0);
            
            // 转换为 dataURL
            const dataURL = canvas.toDataURL("image/png");
            
            // 创建下载链接
            const link = document.createElement("a");
            link.href = dataURL;
            link.download = this.shareTitle + "_二维码.png";
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        };
        image.onerror = () => {
            this.$modal.msgError("二维码下载失败，请重试");
        };
    }
  }
};
</script>
