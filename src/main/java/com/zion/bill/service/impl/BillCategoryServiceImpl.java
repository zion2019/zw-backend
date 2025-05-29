package com.zion.bill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.zion.bill.constants.CategoryType;
import com.zion.bill.dao.BillCategoryDao;
import com.zion.bill.model.BillCategory;
import com.zion.bill.model.Bills;
import com.zion.bill.service.BillCategoryService;
import com.zion.bill.service.BillService;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.vo.bill.req.CategoryQO;
import com.zion.common.vo.bill.req.BillQO;
import com.zion.common.vo.bill.rsp.BillsExcelVO;
import com.zion.common.vo.bill.rsp.BillsVO;
import com.zion.common.vo.bill.rsp.CategoryExcelVO;
import com.zion.common.vo.bill.rsp.CategoryVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillCategoryServiceImpl implements BillCategoryService {
    @Resource
    private BillCategoryDao billCategoryDao;

    @Resource
    private BillService billService;

    // 用于区分父子级关系的特殊字符
    public static final String F_STRING = new String(new char[]{0x01});

    /**
     * 保存分类信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(CategoryQO qo) {
        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getTitle()), "分类名称不能为空！");
        Assert.isTrue(qo.getUserId() != null, "用户ID不能为空！");
        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getCode()), "分类编码不能为空！");

        String fullPath = "0";
        String fullParentName = "";
        Long parentId = 0L;

        // 检查编码是否已存在（排除自身）
        if (qo.getId() != null) {
            BillCategory old = billCategoryDao.getById(qo.getId());
            Assert.isTrue(old != null, "找不到该分类：" + qo.getTitle());

            // 如果是更新操作且parentId不同，则检查父节点路径
            if (qo.getParentId() != null && !qo.getParentId().equals(old.getParentId())) {
                if (qo.getParentId() != null && !qo.getParentId().equals(0L)) {
                    BillCategory parent = billCategoryDao.getById(qo.getParentId());
                    Assert.isTrue(!parent.getFullPath().contains(String.valueOf(qo.getId())), "禁止循环嵌套！");
                }
            }
        } else {
            Assert.isTrue(billCategoryDao.conditionCount(BillCategory.builder()
                    .code(qo.getCode()).build()) == 0, "编码" + qo.getCode() + "已存在");
        }

        int level = 1;
        if (qo.getParentId() != null && !qo.getParentId().equals(0L)) {
            parentId = qo.getParentId();
            BillCategory parent = billCategoryDao.getById(qo.getParentId());
            if (parent != null) {
                level = parent.getLevel() + 1;
                fullPath = parent.getFullPath()+F_STRING +parent.getId();
                fullParentName = CharSequenceUtil.isBlank(parent.getFullParentName()) ?
                        parent.getTitle() : parent.getFullParentName() + F_STRING + parent.getTitle();
            }
        }

        // 检查同一父级下是否存在相同名称的分类
        long sameNameCount = billCategoryDao.conditionCount(BillCategory.builder()
                .parentId(parentId).excludeId(qo.getId()).title(qo.getTitle()).userId(qo.getUserId()).build());
        Assert.isTrue(sameNameCount == 0, "在当前分类下已经存在同名分类：" + qo.getTitle());

        BillCategory category = BeanUtil.copyProperties(qo, BillCategory.class);
        category.setLevel(level);
        category.setParentId(parentId);
        // 默认类型为消费
        category.setCategoryType(qo.getCategoryType() == null ? CategoryType.OUT.getType() : qo.getCategoryType());
        category.setFullPath(fullPath);
        category.setFullParentName(CharSequenceUtil.isBlank(fullParentName) ? null : fullParentName);

        if (qo.getId() == null) {
            billCategoryDao.save(category);
            // 更新全路径，添加自己的ID
            category.setFullPath(fullPath + F_STRING + category.getId());
        } else {
            billCategoryDao.save(category);
        }
    }

    /**
     * 构建分类树结构
     */
    public List<Tree<String>> tree(Long currentUserId, Long excludeId, Boolean root) {
        List<Tree<String>> list = new ArrayList<>();
        List<BillCategory> categories = billCategoryDao.condition(BillCategory.builder()
                .userId(currentUserId).build());

        if (CollUtil.isEmpty(categories)) {
            return list;
        }

        // 定义树形结构配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("id");
        treeNodeConfig.setNameKey("label");
        treeNodeConfig.setParentIdKey("parentId");
        treeNodeConfig.setChildrenKey("children");

        List<Tree<String>> threeList = TreeUtil.build(categories, "0", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(String.valueOf(treeNode.getId()));
            tree.setParentId(String.valueOf(treeNode.getParentId()));
            tree.setName(treeNode.getTitle());
            if (excludeId != null && treeNode.getFullPath().contains(String.valueOf(excludeId))) {
                tree.putExtra("disabled", true);
            }
        });

        if (root) {
            Tree<String> parent = new Tree<>();
            list.add(parent);
            parent.setName("根分类");
            parent.putExtra("readOnly", true);
            parent.putExtra("label", "ROOT");
            parent.setId("0");
            parent.setParentId("-1");
            parent.setChildren(threeList);
        } else {
            list = threeList;
        }

        return list;
    }

    /**
     * 删除分类
     */
    public void delete(Long categoryId) {
        BillCategory condition = BillCategory.builder().build();
        condition.setId(categoryId);

        // 校验是否存在账单明细，存在则不允许删除
        BillQO billQO = new BillQO();
        billQO.setCategoryId(categoryId);
        long billCount = billService.conditionCount(billQO);
        if(billCount > 0){
            throw new ServiceException("分类下存在账单，无法删除");
        }

        billCategoryDao.delete(condition);
    }

    @Override
    public CategoryVO info(Long id, Long currentUserId) {
        BillCategory condition = BillCategory.builder().userId(currentUserId).build();
        condition.setId(id);
        BillCategory category = billCategoryDao.conditionOne(condition);
        if(category == null){
            throw new ServiceException("Unknown category");
        }

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        return categoryVO;
    }

    @Override
    public List<CategoryVO> list(CategoryQO qo) {
        BillCategory condition = BillCategory.builder().userId(qo.getUserId()).build();
        if(CharSequenceUtil.isNotBlank(qo.getTitle())){
            condition.setTitle(qo.getTitle());
        }
        if(qo.getParentId() != null){
            condition.setParentId(qo.getParentId());
        }
        Page<BillCategory> pageRsp = billCategoryDao.pageQuery(new Page<>(qo.getPageNo(), qo.getPageSize()), BillCategory.class, condition);
        if(CollUtil.isEmpty(pageRsp.getDataList())){
            return List.of();
        }
        List<CategoryVO> categoryVOS = BeanUtil.copyToList(pageRsp.getDataList(), CategoryVO.class);

        // 统计所有消费账单
        if(qo.getStats() != null && qo.getStats()){
            // 获取当前指定id下的所有子级ID
            List<CategoryVO> allCategoryVoList = listCategoryAndChildren(qo.getParentId());
            if(CollUtil.isEmpty(allCategoryVoList)){
                return categoryVOS;
            }

            // 查询所有相关账单
            BillQO billQO = new BillQO();
            billQO.setUserId(qo.getUserId());
            billQO.setCategoryIdList(allCategoryVoList.stream().map(CategoryVO::getId).toList());
            billQO.setStartDay(qo.getStatsBillSTime());
            billQO.setEndDay(qo.getStatsBillETime());
            List<BillsVO> billsVOS = billService.list(billQO);

            // 统计当前查询结果下的账单金额
            for (CategoryVO categoryVO : categoryVOS) {
                Long id = categoryVO.getId();
                // 获取当前分类下所有子类，包含当前分类
                Set<Long> allCId = allCategoryVoList.stream().filter(c -> c.getId().equals(id) || c.getFullPath().contains(String.valueOf(id))).map(CategoryVO::getId).collect(Collectors.toSet());
                categoryVO.setBillAmount(billsVOS.stream().filter(b -> allCId.contains(b.getCategoryId()))
                        .map(BillsVO::getAmount)
                        .reduce(BigDecimal.ZERO,  BigDecimal::add));
            }

            // 挂在当前父级下的账单
            if(qo.getParentId() != 0L){
                List<CategoryVO> list = allCategoryVoList.stream().filter(c -> c.getId().equals(qo.getParentId())).toList();
                if(CollUtil.isNotEmpty(list)){
                    CategoryVO parentCategory = list.get(0);
                    parentCategory.setBillAmount(billsVOS.stream()
                            .filter(b -> parentCategory.getId().equals(b.getCategoryId()))
                            .map(BillsVO::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                    categoryVOS.add(parentCategory);
                }
            }
        }

        return categoryVOS;
    }

    @Override
    public Page<BillsVO> billsPage(CategoryQO qo) {
        Assert.isTrue(qo.getParentId() != null, "parentId is required");

        // 获取当前父类下所有子分类
        List<CategoryVO> allCategoryVoList = listCategoryAndChildren(qo.getParentId());
        if(CollUtil.isEmpty(allCategoryVoList)){
            throw new ServiceException("费用类型查询子项错误");
        }

        // 查询所有相关账单
        BillQO billQO = new BillQO();
        billQO.setUserId(qo.getUserId());
        billQO.setCategoryIdList(allCategoryVoList.stream().map(CategoryVO::getId).toList());
        billQO.setStartDay(qo.getStatsBillSTime());
        billQO.setEndDay(qo.getStatsBillETime());
        Page<BillsVO> page = billService.page(billQO);
        if(page == null || CollUtil.isEmpty(page.getDataList())){
            return page;
        }

        // 填充分类名称
        Map<Long, CategoryVO> cateMap = allCategoryVoList.stream().collect(Collectors.toMap(CategoryVO::getId, Function.identity(), (c1, c2) -> c1));
        page.getDataList().forEach(billsVO ->{
            CategoryVO categoryVO = cateMap.get(billsVO.getCategoryId());
            if(categoryVO == null){
                billsVO.setCategoryDesc("UNKNOWN");
                billsVO.setCategoryColor("RED");
            }else{
                billsVO.setCategoryDesc(categoryVO.getTitle());
                billsVO.setCategoryColor(categoryVO.getColor());
            }
        });
        return page;
    }

    @Override
    public List<BillCategory> condition(BillCategory build) {
        return billCategoryDao.condition(build);
    }

    @Override
    public List<CategoryExcelVO> covertCategoryExcelVo(List<BillCategory> userCategories) {
        List<CategoryExcelVO> vos = new ArrayList<>();
        for (BillCategory category : userCategories) {
            CategoryExcelVO vo = new CategoryExcelVO();
            if(category.getId() != null){
                vo.setId(String.valueOf(category.getId()));
            }
            vo.setCreatedTime(category.getCreatedTime());
            vo.setCreatedUser(category.getCreatedUser());
            vo.setUpdatedTime(category.getUpdatedTime());
            vo.setUpdatedUser(category.getUpdatedUser());
            vo.setVersion(category.getVersion());
            vo.setDeleted(category.getDeleted());
            if(category.getUserId() != null){
                vo.setUserId(String.valueOf(category.getUserId()));
            }
            vo.setTitle(category.getTitle());
            vo.setCode(category.getCode());
            vo.setColor(category.getColor());
            if (category.getParentId() != null){
                vo.setParentId(String.valueOf(category.getParentId()));
            }
            vo.setLevel(category.getLevel());
            vo.setCategoryType(category.getCategoryType());
            vo.setFullPath(category.getFullPath());
            vo.setFullParentName(category.getFullParentName());
            vos.add(vo);
        }
        return vos;
    }


    private List<CategoryVO> listCategoryAndChildren(Long categoryId) {
        // 根据传入categoryId查询当前分类
        List<CategoryVO> categories = new ArrayList<>();
        BillCategory parentCategory = null;
        if(0L == categoryId){
            parentCategory = BillCategory.builder().build();
            parentCategory.setId(0L);
            parentCategory.setLevel(0);
            parentCategory.setTitle("总分类");
        }else{
            parentCategory = billCategoryDao.getById(categoryId);
            if(parentCategory == null){
                return categories;
            }
            categories.add(BeanUtil.copyProperties(parentCategory, CategoryVO.class));
        }

        // 获取当前分类下的所有子分类，逻辑是：获取当前分类code， like bill_category.fullPath  所有 code%
        BillCategory condition = BillCategory.builder().parentIdLike(parentCategory.getId()).build();
        condition.include("id","fullPath","title","color");
        List<BillCategory> childrenCategories = billCategoryDao.condition(condition);
        if(CollUtil.isNotEmpty(childrenCategories)){
            categories.addAll(BeanUtil.copyToList(childrenCategories, CategoryVO.class));
        }

        return categories;
    }


    /**
     * 根据账单类型取正反金额
     * @param billAmount 账单金额
     * @param categoryTypeCode 分类类型
     * @return 实际金额
     */
    private BigDecimal calculateAmount(BigDecimal billAmount,Integer categoryTypeCode){
        return categoryTypeCode == null || CategoryType.OUT.getType().equals(categoryTypeCode) ? billAmount.multiply(BigDecimal.valueOf(-1)) : billAmount;
    }


}
