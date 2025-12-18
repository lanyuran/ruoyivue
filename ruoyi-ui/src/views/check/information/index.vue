<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="患者姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入患者姓名"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="就诊医院名称" prop="hospital">
        <el-input
          v-model="queryParams.hospital"
          placeholder="请输入就诊医院名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="病历号" prop="medicalRecordNo">
        <el-input
          v-model="queryParams.medicalRecordNo"
          placeholder="请输入病历号"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系手机号码" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系手机号码"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="informationList">
      <el-table-column label="序号" type="index" width="50" align="center">
        <template slot-scope="scope">
          <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="病历号" align="center" prop="medicalRecordNo" />
      <el-table-column label="主诉" align="center" prop="chiefComplaint" show-overflow-tooltip />
      <el-table-column label="就诊医院" align="center" prop="hospital" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详细信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" label-width="130px">
        <el-form-item label="患者姓名" prop="name" >
          <el-input v-model="form.name" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="form.birthDate" type="date" value-format="yyyy-MM-dd" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="就诊日期" prop="visitTime">
          <el-date-picker v-model="form.visitTime" type="date" value-format="yyyy-MM-dd" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="医院" prop="hospital">
          <el-input v-model="form.hospital" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="病历号" prop="medicalRecordNo">
          <el-input v-model="form.medicalRecordNo" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="家长姓名" prop="parentName">
          <el-input v-model="form.parentName" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" maxlength="11" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="既往用药史" prop="pastMedication">
          <el-input type="textarea" v-model="form.pastMedication" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input type="textarea" v-model="form.chiefComplaint" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="主证" prop="mainSymptom">
          <el-input type="textarea" v-model="form.mainSymptom" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="共患病" prop="comorbidity">
          <el-input type="textarea" v-model="form.comorbidity" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="体格检查" prop="physicalExam">
          <el-input type="textarea" v-model="form.physicalExam" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="舌脉" prop="tonguePulse">
          <el-input type="textarea" v-model="form.tonguePulse" :disabled="isReadonly"></el-input>
        </el-form-item>

        <!-- 图片展示 -->
        <el-form-item label="舌象照片">
          <div v-if="form.tongueImagePath">
             <el-image
              style="width: 148px; height: 148px; border-radius: 6px;"
              :src="getImageUrl(form.tongueImagePath)"
              :preview-src-list="[getImageUrl(form.tongueImagePath)]"
            />
          </div>
          <span v-else style="color: #909399;">未录入</span>
        </el-form-item>

        <el-form-item label="总 IgE">
          <el-input v-model="form.allergenTotalIge" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="特异性 IgE">
          <el-input v-model="form.allergenSpecificIge" :disabled="isReadonly"/>
        </el-form-item>

        <el-form-item label="血常规报告">
          <div v-if="form.bloodTestImagePath">
             <el-image
              style="width: 148px; height: 148px; border-radius: 6px;"
              :src="getImageUrl(form.bloodTestImagePath)"
              :preview-src-list="[getImageUrl(form.bloodTestImagePath)]"
            />
          </div>
          <span v-else style="color: #909399;">未录入</span>
        </el-form-item>

        <el-form-item label="炎症因子报告">
          <div v-if="form.inflammationImagePath">
             <el-image
              style="width: 148px; height: 148px; border-radius: 6px;"
              :src="getImageUrl(form.inflammationImagePath)"
              :preview-src-list="[getImageUrl(form.inflammationImagePath)]"
            />
          </div>
          <span v-else style="color: #909399;">未录入</span>
        </el-form-item>

        <el-form-item label="肝肾功能">
          <div v-if="form.liverKidneyImagePath">
             <el-image
              style="width: 148px; height: 148px; border-radius: 6px;"
              :src="getImageUrl(form.liverKidneyImagePath)"
              :preview-src-list="[getImageUrl(form.liverKidneyImagePath)]"
            />
          </div>
          <span v-else style="color: #909399;">未录入</span>
        </el-form-item>

        <el-form-item label="肾早期损伤">
          <div v-if="form.renalInjuryImagePath">
             <el-image
              style="width: 148px; height: 148px; border-radius: 6px;"
              :src="getImageUrl(form.renalInjuryImagePath)"
              :preview-src-list="[getImageUrl(form.renalInjuryImagePath)]"
            />
          </div>
          <span v-else style="color: #909399;">未录入</span>
        </el-form-item>

        <el-form-item label="中医诊断">
          <el-input v-model="form.tcmDiagnosis" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="中医治法">
          <el-input v-model="form.tcmTreatment" :disabled="isReadonly"></el-input>
        </el-form-item>

        <el-form-item label="处方照片">
          <div v-if="form.tcmTreatmentImagePath">
             <el-image
              style="width: 148px; height: 148px; border-radius: 6px;"
              :src="getImageUrl(form.tcmTreatmentImagePath)"
              :preview-src-list="[getImageUrl(form.tcmTreatmentImagePath)]"
            />
          </div>
          <span v-else style="color: #909399;">未录入</span>
        </el-form-item>

        <el-form-item label="中医外治处方">
          <el-input v-model="form.tcmExternalPrescription" type="textarea" :disabled="isReadonly"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInformation, getInformation } from "@/api/patient/information"

export default {
  name: "Information",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      informationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      isReadonly: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        hospital: null,
        medicalRecordNo: null,
        phone: null,
      },
      form: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询患者信息 */
    getList() {
      this.loading = true
      listInformation(this.queryParams).then(response => {
        this.informationList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    /** 查看详细 */
    handleDetail(row) {
      this.reset();
      const visitId = row.visitId;
      getInformation(visitId).then(response => {
        this.form = response.data;
        this.title = "患者就诊详细信息";
        this.open = true;
        this.isReadonly = true;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        visitId: null,
        name: null,
        gender: null,
        birthDate: null,
        visitTime: null,
        hospital: null,
        medicalRecordNo: null,
        parentName: null,
        phone: null,
        pastMedication: null,
        chiefComplaint: null,
        mainSymptom: null,
        comorbidity: null,
        physicalExam: null,
        tonguePulse: null,
        tongueImagePath: null,
        bloodTestImagePath: null,
        inflammationImagePath: null,
        liverKidneyImagePath: null,
        renalInjuryImagePath: null,
        tcmTreatmentImagePath: null,
        allergenTotalIge: null,
        allergenSpecificIge: null,
        tcmDiagnosis: null,
        tcmTreatment: null,
        tcmExternalPrescription: null
      };
      this.resetForm("form");
    },
    // 获取完整图片URL
    getImageUrl(path) {
      if (!path) return '';
      if (path.startsWith('http') || path.startsWith('https') || path.startsWith('data:')) {
        return path;
      }
      return process.env.VUE_APP_BASE_API + path;
    }
  }
}
</script>