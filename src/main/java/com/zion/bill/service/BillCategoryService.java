package com.zion.bill.service;

import cn.hutool.core.lang.tree.Tree;
import com.zion.common.basic.Page;
import com.zion.common.vo.bill.req.BillQO;
import com.zion.common.vo.bill.req.CategoryQO;
import com.zion.common.vo.bill.rsp.BillsVO;
import com.zion.common.vo.bill.rsp.CategoryVO;

import java.io.Serializable;
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

    /**
     * get bill category info
     * @param id category id
     * @param currentUserId current user id
     * @return bill category info
     */
    CategoryVO info(Long id, Long currentUserId);

    /**
     * get bill category list
     * @param qo bill category info
     * @return bill
     * @return bill category list
     */
    List<CategoryVO> list(CategoryQO qo);

    /**
     * 基于categoryId查询分类下所有账单
     * @param qo 查询条件
     * @return 账单列表
     */
    Page<BillsVO> billsPage(CategoryQO qo);
}
