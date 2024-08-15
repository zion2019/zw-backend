package com.zion.learning.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.excel.EasyExcel;
import com.zion.common.basic.BaseController;
import com.zion.common.basic.R;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.learning.request.PointQO;
import com.zion.learning.service.PointService;
import com.zion.learning.service.excel.PointExcelDto;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    @GetMapping("/export/template")
    public void export(HttpServletResponse response){
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        InputStream in = null;
        ServletOutputStream out = null;
        try {
            String fileName = URLEncoder.encode("PointImportTemplate", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            File file = ResourceUtils.getFile("classpath:template/PointImportTemplate.xlsx");
            in = new FileInputStream(file);
            out = response.getOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer))!=-1){
                out.write(buffer,0,len);
            }
            EasyExcel.write(response.getOutputStream(), PointExcelDto.class).sheet().doWrite(new ArrayList<>());
        } catch (Exception e) {
            log.error("export template error",e);
            throw new ServiceException(e.getMessage());
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("export template error",e);
                }
            }
            if(out != null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    log.error("export template error",e);
                }
            }
        }
    }
}
