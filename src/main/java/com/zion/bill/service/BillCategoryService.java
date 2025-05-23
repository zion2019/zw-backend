package com.zion.bill.service;

import cn.hutool.core.lang.tree.Tree;
import com.zion.common.vo.bill.CategoryQO;

import java.util.List;

public interface BillCategoryService {
    /**
     * find and bill category tree
     * @param currentUserId current user id
     * @param excludeId need to disable
     * @param root whether to return root node
     * @return the tree of bill category
     */
    List<Tree<String>> tree(Long currentUserId, Long excludeId, Boolean root);

    /**
     * save bill category
     * @param qo bill category info
     */
    void save(CategoryQO qo);

    /**
     * delete bill category
     * @param categoryId bill category id
     */
    void delete(Long categoryId);
}
