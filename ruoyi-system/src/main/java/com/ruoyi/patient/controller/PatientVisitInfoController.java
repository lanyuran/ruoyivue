package com.ruoyi.patient.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.patient.domain.PatientVisitInfo;
import com.ruoyi.patient.domain.PatientVisitExportVO;
import com.ruoyi.patient.service.IPatientVisitInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.ISysDeptService;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;



@RestController
@RequestMapping("/patient/information")
public class PatientVisitInfoController extends BaseController
{
    private static final DateTimeFormatter IMAGE_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final Map<String, BiConsumer<PatientVisitInfo, String>> IMAGE_HEADER_SETTERS = new HashMap<>();
    private static final Map<Integer, BiConsumer<PatientVisitInfo, String>> IMAGE_COLUMN_SETTERS = new HashMap<>();

    static
    {
        registerImageMapping("15、请上传自然光线下舌象照片", 14, PatientVisitInfo::setTongueImagePath);
        registerImageMapping("18、血常规     RBC;WBC;PLT;Hb;EOS", 17, PatientVisitInfo::setBloodTestImagePath);
        registerImageMapping("19、炎症因子 IL-4；IL-6；IL-10；TNF-α", 18, PatientVisitInfo::setInflammationImagePath);
        registerImageMapping("20、肝肾功能 Scr；Bun；ALT；AST；TBIL", 19, PatientVisitInfo::setLiverKidneyImagePath);
        registerImageMapping("21、肾早期损伤 尿微量蛋白；尿肌酐；β2-微球蛋白；微量蛋白/尿肌酐", 20, PatientVisitInfo::setRenalInjuryImagePath);
        registerImageMapping("24、中药处方", 23, PatientVisitInfo::setTcmTreatmentImagePath);
    }

    @Autowired
    private IPatientVisitInfoService patientVisitInfoService;

    @Autowired
    private ISysDeptService deptService;

    private static final String ROLE_KEY_DEPT_DOCTOR = "dept_doctor";

    private static final String ROLE_KEY_DEPT_MANAGER = "dept_manager";

    private static final String ROLE_KEY_CAMPUS_MANAGER = "campus_manager";

    /**
     * 查询鼻炎患者就诊信息主（包含文档中所有字段）列表
     */
    @PreAuthorize("@ss.hasPermi('patient:information:list')")
    @GetMapping("/list")
    public TableDataInfo list(PatientVisitInfo patientVisitInfo)
    {
        startPage();
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(patientVisitInfo);
        return getDataTable(list);
    }

    /**
     * 查询我的鼻炎患者就诊信息列表
     */
    @PreAuthorize("@ss.hasPermi('patient:information:list')")
    @GetMapping("/listMy")
    public TableDataInfo listMy(PatientVisitInfo patientVisitInfo)
    {
        startPage();
        patientVisitInfo.setCreateBy(getUsername());
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(patientVisitInfo);
        return getDataTable(list);
    }

    /**
     * 导入鼻炎患者就诊信息（Excel）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:add')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error("上传文件不能为空");
        }
        byte[] fileBytes = file.getBytes();
        ExcelImportContext importContext = buildImportContext(fileBytes);
        List<String> imagePaths = importContext.getImageInfos().stream().map(ExcelImageInfo::getPath).collect(Collectors.toList());
        ExcelUtil<PatientVisitInfo> util = new ExcelUtil<>(PatientVisitInfo.class);
        List<PatientVisitInfo> visitInfos = util.importExcel(new ByteArrayInputStream(fileBytes));
        if (visitInfos == null || visitInfos.isEmpty())
        {
            return AjaxResult.error("导入数据为空");
        }

        fillMissingImageColumns(visitInfos, importContext);
        int successCount = 0;
        for (PatientVisitInfo info : visitInfos)
        {
            if (info == null)
            {
                continue;
            }
            info.setCreateBy(getUsername());
            successCount += patientVisitInfoService.insertPatientVisitInfo(info);
        }
        AjaxResult result = AjaxResult.success("成功导入 " + successCount + " 条就诊记录");
        result.put("imagePaths", imagePaths);
        return result;
    }

    /**
     * 获取医院下拉选项
     */
    @PreAuthorize("@ss.hasPermi('patient:information:list')")
    @GetMapping("/hospitalOptions")
    public AjaxResult hospitalOptions()
    {
        SysDept dept = new SysDept();
        dept.setStatus("0");
        List<TreeSelect> deptTree = deptService.selectDeptTreeListPublic(dept);
        TreeSelect hospitalRoot = findHospitalRootNode(deptTree);
        List<Map<String, Object>> options = new ArrayList<>();
        if (hospitalRoot != null && hospitalRoot.getChildren() != null)
        {
            for (TreeSelect child : hospitalRoot.getChildren())
            {
                if (child == null)
                {
                    continue;
                }
                Map<String, Object> option = new HashMap<>();
                option.put("deptId", child.getId());
                option.put("deptName", child.getLabel());
                options.add(option);
            }
        }
        return success(options);
    }

    /**
     * 导出鼻炎患者就诊信息主（包含文档中所有字段）列表
     */
    @PreAuthorize("@ss.hasPermi('patient:information:export')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PatientVisitInfo patientVisitInfo) {
        // 查询数据库
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(patientVisitInfo);

        // 转换为导出 VO
        List<PatientVisitExportVO> exportList = list.stream().map(p -> {
            PatientVisitExportVO vo = new PatientVisitExportVO();
            vo.setName(p.getName());
            vo.setGender(p.getGender());
            vo.setBirthDate(p.getBirthDate());
            vo.setVisitTime(p.getVisitTime());
            vo.setHospital(p.getHospital());
            vo.setMedicalRecordNo(p.getMedicalRecordNo());
            vo.setParentName(p.getParentName());
            vo.setPhone(p.getPhone());
            vo.setPastMedication(p.getPastMedication());
            vo.setChiefComplaint(p.getChiefComplaint());
            vo.setMainSymptom(p.getMainSymptom());
            vo.setComorbidity(p.getComorbidity());
            vo.setPhysicalExam(p.getPhysicalExam());
            vo.setTonguePulse(p.getTonguePulse());
//            vo.setAllergenTotalIge(p.getAllergenTotalIge()); // 报错暂时注释
//            vo.setAllergenSpecificIge(p.getAllergenSpecificIge()); // 报错暂时注释
            vo.setTcmDiagnosis(p.getTcmDiagnosis());
            vo.setTcmTreatment(p.getTcmTreatment());
            vo.setTcmExternalPrescription(p.getTcmExternalPrescription());
            return vo;
        }).collect(Collectors.toList());

        // 导出 Excel
        ExcelUtil<PatientVisitExportVO> util = new ExcelUtil<>(PatientVisitExportVO.class);
        util.exportExcel(response, exportList, "鼻炎患者就诊信息主数据");
    }



    /**
     * 获取鼻炎患者就诊信息主（包含文档中所有字段）详细信息
     */
    @PreAuthorize("@ss.hasPermi('patient:information:query')")
    @GetMapping(value = "/{visitId}")
    public AjaxResult getInfo(@PathVariable("visitId") Long visitId)
    {
        PatientVisitInfo query = new PatientVisitInfo();
        query.setVisitId(visitId);
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(query);
        if (list == null || list.isEmpty())
        {
            return error("没有权限访问该数据");
        }
        return success(list.get(0));
    }

    /**
     * 新增鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:add')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PatientVisitInfo patientVisitInfo)
    {
        patientVisitInfo.setCreateBy(getUsername());
        return toAjax(patientVisitInfoService.insertPatientVisitInfo(patientVisitInfo));
    }

    /**
     * 修改鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:edit')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PatientVisitInfo patientVisitInfo)
    {
        checkDataScope(patientVisitInfo.getVisitId());
        return toAjax(patientVisitInfoService.updatePatientVisitInfo(patientVisitInfo));
    }

    /**
     * 删除鼻炎患者就诊信息主（包含文档中所有字段）
     */
    @PreAuthorize("@ss.hasPermi('patient:information:remove')")
    @Log(title = "鼻炎患者就诊信息主（包含文档中所有字段）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{visitIds}")
    public AjaxResult remove(@PathVariable Long[] visitIds)
    {
        if (visitIds != null)
        {
            for (Long visitId : visitIds)
            {
                checkDataScope(visitId);
            }
        }
        return toAjax(patientVisitInfoService.deletePatientVisitInfoByVisitIds(visitIds));
    }

    private void checkDataScope(Long visitId)
    {
        if (visitId == null)
        {
            return;
        }
        PatientVisitInfo query = new PatientVisitInfo();
        query.setVisitId(visitId);
        List<PatientVisitInfo> list = patientVisitInfoService.selectPatientVisitInfoList(query);
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("没有权限访问该数据");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            return;
        }
        SysUser user = loginUser.getUser();
        if (user == null || user.isAdmin())
        {
            return;
        }
        if (hasRole(user, ROLE_KEY_CAMPUS_MANAGER))
        {
            return;
        }
        if (hasRole(user, ROLE_KEY_DEPT_MANAGER))
        {
            return;
        }
        PatientVisitInfo info = list.get(0);
        if (hasRole(user, ROLE_KEY_DEPT_DOCTOR) && !StringUtils.equals(info.getCreateBy(), user.getUserName()))
        {
            throw new ServiceException("只能修改本人录入的数据");
        }
    }

    private boolean hasRole(SysUser user, String roleKey)
    {
        if (user == null || StringUtils.isEmpty(roleKey) || user.getRoles() == null)
        {
            return false;
        }
        for (SysRole role : user.getRoles())
        {
            if (role == null || StringUtils.isEmpty(role.getRoleKey()))
            {
                continue;
            }
            String[] keys = role.getRoleKey().split(",");
            for (String key : keys)
            {
                if (roleKey.equals(StringUtils.trim(key)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private TreeSelect findHospitalRootNode(List<TreeSelect> nodes)
    {
        if (nodes == null)
        {
            return null;
        }
        for (TreeSelect node : nodes)
        {
            if (node == null)
            {
                continue;
            }
            if ("医院".equals(StringUtils.trim(node.getLabel())))
            {
                return node;
            }
            TreeSelect found = findHospitalRootNode(node.getChildren());
            if (found != null)
            {
                return found;
            }
        }
        return null;
    }

    private ExcelImportContext buildImportContext(byte[] fileBytes) throws IOException
    {
        List<ExcelImageInfo> imageInfos = new ArrayList<>();
        List<Integer> dataRows = new ArrayList<>();
        if (fileBytes == null || fileBytes.length == 0)
        {
            return new ExcelImportContext(imageInfos, dataRows);
        }
        String importPath = "/import/" + IMAGE_DIR_FORMATTER.format(LocalDateTime.now()) + "-" + UUID.randomUUID().toString().replace("-", "");
        String absolutePath = RuoYiConfig.getProfile() + importPath;
        File targetDirFile = new File(absolutePath);
        if (!targetDirFile.exists())
        {
            targetDirFile.mkdirs();
        }
        Path targetDir = targetDirFile.toPath();
        String relativeDir = Constants.RESOURCE_PREFIX + importPath + "/";
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(fileBytes)))
        {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null)
            {
                return new ExcelImportContext(imageInfos, dataRows);
            }
            List<String> headers = readHeaderValues(sheet.getRow(0));
            collectDataRowIndexes(sheet, 0, dataRows);
            writeSheetPictures(workbook, sheet, headers, targetDir, relativeDir, imageInfos);
        }
        return new ExcelImportContext(imageInfos, dataRows);
    }

    private void fillMissingImageColumns(List<PatientVisitInfo> visitInfos, ExcelImportContext context)
    {
        if (visitInfos == null || visitInfos.isEmpty() || context == null)
        {
            return;
        }
        List<ExcelImageInfo> imageInfos = context.getImageInfos();
        if (imageInfos.isEmpty())
        {
        }
        Map<Integer, PatientVisitInfo> rowIndexToEntity = new HashMap<>();
        List<Integer> dataRowIndexes = context.getDataRowIndexes();
        for (int i = 0; i < visitInfos.size() && i < dataRowIndexes.size(); i++)
        {
            PatientVisitInfo info = visitInfos.get(i);
            if (info != null)
            {
                rowIndexToEntity.put(dataRowIndexes.get(i), info);
            }
        }
        for (ExcelImageInfo imageInfo : imageInfos)
        {
            PatientVisitInfo target = rowIndexToEntity.get(imageInfo.getRowIndex());
            if (target == null)
            {
                continue;
            }
            applyImageToEntity(target, imageInfo);
        }
    }

    private void applyImageToEntity(PatientVisitInfo info, ExcelImageInfo imageInfo)
    {
        if (info == null || imageInfo == null || StringUtils.isEmpty(imageInfo.getPath()))
        {
            return;
        }
        BiConsumer<PatientVisitInfo, String> setter = IMAGE_HEADER_SETTERS.get(imageInfo.getHeaderName());
        if (setter == null)
        {
            setter = IMAGE_COLUMN_SETTERS.get(imageInfo.getColumnIndex());
        }
        if (setter != null)
        {
            setter.accept(info, imageInfo.getPath());
        }
    }

    private static void registerImageMapping(String header, int columnIndex, BiConsumer<PatientVisitInfo, String> setter)
    {
        IMAGE_HEADER_SETTERS.put(header, setter);
        IMAGE_COLUMN_SETTERS.put(columnIndex, setter);
    }

    private List<String> readHeaderValues(Row headerRow)
    {
        List<String> headers = new ArrayList<>();
        if (headerRow == null)
        {
            return headers;
        }
        short cellCount = headerRow.getLastCellNum();
        for (int i = 0; i < cellCount; i++)
        {
            Cell cell = headerRow.getCell(i);
            headers.add(cell == null ? null : cell.toString());
        }
        return headers;
    }

    private void collectDataRowIndexes(Sheet sheet, int headerRowIndex, List<Integer> rowIndexes)
    {
        if (sheet == null || rowIndexes == null)
        {
            return;
        }
        int lastRow = sheet.getLastRowNum();
        for (int i = headerRowIndex + 1; i <= lastRow; i++)
        {
            Row row = sheet.getRow(i);
            if (!isRowEmpty(row))
            {
                rowIndexes.add(i);
            }
        }
    }

    private boolean isRowEmpty(Row row)
    {
        if (row == null)
        {
            return true;
        }
        short lastCell = row.getLastCellNum();
        for (int i = row.getFirstCellNum(); i < lastCell; i++)
        {
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.isNotEmpty(cell.toString()))
            {
                return false;
            }
        }
        return true;
    }

    private void writeSheetPictures(Workbook workbook, Sheet sheet, List<String> headers, Path targetDir,
            String relativeDir, List<ExcelImageInfo> container) throws IOException
    {
        if (sheet instanceof XSSFSheet)
        {
            collectXssfPictures((XSSFSheet) sheet, headers, targetDir, relativeDir, container);
        }
        else if (sheet instanceof HSSFSheet)
        {
            collectHssfPictures((HSSFSheet) sheet, headers, targetDir, relativeDir, container);
        }
        else
        {
            List<? extends PictureData> pictures = workbook.getAllPictures();
            if (pictures == null || pictures.isEmpty())
            {
                return;
            }
            int[] counter = { 1 };
            for (PictureData data : pictures)
            {
                String path = writePictureBytes(data, targetDir, relativeDir, counter[0]++);
                container.add(new ExcelImageInfo(-1, -1, null, path));
            }
        }
    }

    private void collectXssfPictures(XSSFSheet sheet, List<String> headers, Path targetDir, String relativeDir,
            List<ExcelImageInfo> container) throws IOException
    {
        XSSFDrawing drawing = sheet.getDrawingPatriarch();
        if (drawing == null)
        {
            return;
        }
        int[] counter = { 1 };
        for (XSSFShape shape : drawing.getShapes())
        {
            if (!(shape instanceof XSSFPicture))
            {
                continue;
            }
            XSSFPicture picture = (XSSFPicture) shape;
            XSSFClientAnchor anchor = picture.getClientAnchor();
            if (anchor == null)
            {
                anchor = picture.getPreferredSize();
            }
            if (anchor == null)
            {
                continue;
            }
            PictureData pictureData = picture.getPictureData();
            if (pictureData == null || pictureData.getData() == null || pictureData.getData().length == 0)
            {
                continue;
            }
            String path = writePictureBytes(pictureData, targetDir, relativeDir, counter[0]++);
            container.add(new ExcelImageInfo(anchor.getRow1(), anchor.getCol1(), getHeader(headers, anchor.getCol1()), path));
        }
    }

    private void collectHssfPictures(HSSFSheet sheet, List<String> headers, Path targetDir, String relativeDir,
            List<ExcelImageInfo> container) throws IOException
    {
        org.apache.poi.hssf.usermodel.HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
        if (patriarch == null)
        {
            return;
        }
        int[] counter = { 1 };
        for (HSSFShape shape : patriarch.getChildren())
        {
            if (!(shape instanceof HSSFPicture))
            {
                continue;
            }
            HSSFPicture picture = (HSSFPicture) shape;
            HSSFClientAnchor anchor = (HSSFClientAnchor) picture.getAnchor();
            if (anchor == null)
            {
                continue;
            }
            PictureData pictureData = picture.getPictureData();
            if (pictureData == null || pictureData.getData() == null || pictureData.getData().length == 0)
            {
                continue;
            }
            String path = writePictureBytes(pictureData, targetDir, relativeDir, counter[0]++);
            container.add(new ExcelImageInfo(anchor.getRow1(), anchor.getCol1(), getHeader(headers, anchor.getCol1()), path));
        }
    }

    private String writePictureBytes(PictureData data, Path targetDir, String relativeDir, int index) throws IOException
    {
        byte[] bytes = data.getData();
        String extension = data.suggestFileExtension();
        if (StringUtils.isEmpty(extension))
        {
            extension = "img";
        }
        String fileName = String.format("%03d.%s", index, extension);
        Path destination = targetDir.resolve(fileName);
        Files.write(destination, bytes);
        return relativeDir + fileName;
    }

    private String getHeader(List<String> headers, int columnIndex)
    {
        if (headers == null || columnIndex < 0 || columnIndex >= headers.size())
        {
            return null;
        }
        return headers.get(columnIndex);
    }

    private static final class ExcelImportContext
    {
        private final List<ExcelImageInfo> imageInfos;
        private final List<Integer> dataRowIndexes;

        private ExcelImportContext(List<ExcelImageInfo> imageInfos, List<Integer> dataRowIndexes)
        {
            this.imageInfos = imageInfos;
            this.dataRowIndexes = dataRowIndexes;
        }

        public List<ExcelImageInfo> getImageInfos()
        {
            return imageInfos;
        }

        public List<Integer> getDataRowIndexes()
        {
            return dataRowIndexes;
        }
    }

    private static final class ExcelImageInfo
    {
        private final int rowIndex;
        private final int columnIndex;
        private final String headerName;
        private final String path;

        private ExcelImageInfo(int rowIndex, int columnIndex, String headerName, String path)
        {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.headerName = headerName;
            this.path = path;
        }

        public int getRowIndex()
        {
            return rowIndex;
        }

        public int getColumnIndex()
        {
            return columnIndex;
        }

        public String getHeaderName()
        {
            return headerName;
        }

        public String getPath()
        {
            return path;
        }
    }
}
