package com.zion.bill.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.zion.bill.dao.BillCategoryDao;
import com.zion.bill.model.BillCategory;
import com.zion.bill.service.BillCategoryService;
import com.zion.common.vo.bill.CategoryQO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BillCategoryServiceImpl implements BillCategoryService {
    @Resource
    private BillCategoryDao billCategoryDao;

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
                fullPath = parent.getFullPath();
                fullParentName = CharSequenceUtil.isBlank(parent.getFullParentName()) ?
                        parent.getTitle() : parent.getFullParentName() + F_STRING + parent.getTitle();
            }
        }

        // 检查同一父级下是否存在相同名称的分类
        long sameNameCount = billCategoryDao.conditionCount(BillCategory.builder()
                .parentId(parentId).title(qo.getTitle()).userId(qo.getUserId()).build());
        Assert.isTrue(sameNameCount == 0, "在当前分类下已经存在同名分类：" + qo.getTitle());

        BillCategory category = BeanUtil.copyProperties(qo, BillCategory.class);
        category.setLevel(level);
        category.setParentId(parentId);
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

        // TODO: 这里可能需要添加额外的校验，比如是否有子分类或相关账单数据

        billCategoryDao.delete(condition);
    }

}
