package com.ruoyi.survey.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.survey.domain.Survey;
import com.ruoyi.survey.service.ISurveyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 问卷Controller
 * 
 * @author ruoyi
 * @date 2024-05-20
 */
@RestController
@RequestMapping("/survey/survey")
public class SurveyController extends BaseController
{
    @Autowired
    private ISurveyService surveyService;

    /**
     * 查询问卷列表
     */
    @PreAuthorize("@ss.hasPermi('survey:survey:list')")
    @GetMapping("/list")
    public TableDataInfo list(Survey survey)
    {
        startPage();
        List<Survey> list = surveyService.selectSurveyList(survey);
        return getDataTable(list);
    }

    /**
     * 导出问卷列表
     */
    @PreAuthorize("@ss.hasPermi('survey:survey:export')")
    @Log(title = "问卷", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Survey survey)
    {
        List<Survey> list = surveyService.selectSurveyList(survey);
        ExcelUtil<Survey> util = new ExcelUtil<Survey>(Survey.class);
        util.exportExcel(response, list, "问卷数据");
    }

    /**
     * 获取问卷详细信息
     */
    @PreAuthorize("@ss.hasPermi('survey:survey:query')")
    @GetMapping(value = "/{surveyId}")
    public AjaxResult getInfo(@PathVariable("surveyId") Long surveyId)
    {
        return success(surveyService.selectSurveyById(surveyId));
    }

    /**
     * 新增问卷
     */
    @PreAuthorize("@ss.hasPermi('survey:survey:add')")
    @Log(title = "问卷", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Survey survey)
    {
        survey.setCreateBy(getUsername());
        return toAjax(surveyService.insertSurvey(survey));
    }

    /**
     * 修改问卷
     */
    @PreAuthorize("@ss.hasPermi('survey:survey:edit')")
    @Log(title = "问卷", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Survey survey)
    {
        return toAjax(surveyService.updateSurvey(survey));
    }

    /**
     * 删除问卷
     */
    @PreAuthorize("@ss.hasPermi('survey:survey:remove')")
    @Log(title = "问卷", businessType = BusinessType.DELETE)
	@DeleteMapping("/{surveyIds}")
    public AjaxResult remove(@PathVariable Long[] surveyIds)
    {
        return toAjax(surveyService.deleteSurveyByIds(surveyIds));
    }
}
