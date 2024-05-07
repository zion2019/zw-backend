package com.zion.learning.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.learning.service.PointService;
import com.zion.learning.service.excel.PointExcelDto;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/learn/point")
public class PointController extends BaseController {

    @Resource
    private PointService pointService;

    @GetMapping
    public R list(PointQO pointQO){
        return R.ok(pointService.list(pointQO));
    }

    @GetMapping("/info")
    public R info(@RequestParam("pointId")Long pointId){
        return R.ok(pointService.info(pointId));
    }

    @PostMapping
    public R save(@RequestBody PointQO pointQO){
        pointQO.setUserId(getCurrentUserId());
        return R.ok(pointService.save(pointQO));
    }

    @DeleteMapping
    public R delete(@RequestParam("pointId")Long pointId){
        return R.ok(pointService.delete(pointId));
    }

    @PostMapping("/import/excel")
    public R importByExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response){
        List<PointExcelDto> pointExcels = pointService.importByExcel(file,getCurrentUserId());
        if(CollUtil.isNotEmpty(pointExcels)){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = null;
            try {
                fileName = URLEncoder.encode(file.getName()+"_ERR", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
                EasyExcel.write(response.getOutputStream(), PointExcelDto.class).sheet("error").doWrite(pointExcels);
            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
        }
        return R.ok();
    }
}
