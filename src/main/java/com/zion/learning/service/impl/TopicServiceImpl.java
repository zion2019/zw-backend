package com.zion.learning.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.zion.common.basic.Page;
import com.zion.common.basic.ServiceException;
import com.zion.common.utils.BaseEntityUtil;
import com.zion.common.vo.learning.request.TopicQO;
import com.zion.common.vo.learning.response.TopicStatisticVo;
import com.zion.common.vo.learning.response.TopicVO;
import com.zion.learning.dao.TopicDao;
import com.zion.learning.model.Topic;
import com.zion.learning.service.PointService;
import com.zion.learning.service.TopicService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicDao topicDao;
    @Resource
    private PointService pointService;

    /**
     * special spilt chat
     */
    public static final String F_STRING = new String(new char[]{0x01});

    @Transactional(rollbackFor = Exception.class)
    public boolean save(TopicQO qo) {

        Assert.isTrue(CharSequenceUtil.isNotBlank(qo.getTitle()),"The title is required!");
        Assert.isTrue(qo.getUserId() != null,"The userId is required!");

        String fullPath = "0";
        String fullParentName = "";

        // 更新逻辑
        if(qo.getId() != null){
            Topic old = topicDao.getById(qo.getId());
            Assert.isTrue(old != null,"The topic :"+qo.getTitle()+" is notfound!");
        }

        Integer level = 1;
        Long parentId = 0L;
        if(qo.getParentId() != null && !qo.getParentId().equals(0L)){
            parentId = qo.getParentId();
            Topic parent = topicDao.getById(qo.getParentId());
            Assert.isTrue(qo.getId() == null || !(parent.getFullPath().contains(String.valueOf(qo.getId()))),"禁止循环嵌套！");
            level = parent.getLevel()+1;
            fullPath = parent.getFullPath();
            fullParentName = CharSequenceUtil.isBlank(parent.getFullParentName())?parent.getTitle():parent.getFullParentName()+F_STRING+parent.getTitle();
        }

        // title在所属父级层级中是否存在
        long sameNameCount = topicDao.conditionCount(Topic.builder()
                .parentId(parentId).title(qo.getTitle()).userId(qo.getUserId()).excludeId(qo.getId()).build());
        if(sameNameCount > 0){
            throw new ServiceException(qo.getTitle()+"在当前主题下已经存在！");
        }

        Topic topic = BeanUtil.copyProperties(qo, Topic.class);
        topic.setLevel(level);
        if(topic.getWeight() == null){
            topic.setWeight(0L);
        }
        topic.setFullPath(fullPath);
        topic.setParentId(parentId);
        topic.setFullParentName(CharSequenceUtil.isBlank(fullParentName)?null:fullParentName);
        BaseEntityUtil.filedBasicInfo(topic);
        topic.setFullPath(fullPath+F_STRING+topic.getId());

        topicDao.save(topic);
        return true;
    }

    /**
     *定义与hutool树结构配置
     */
    private static TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
    static {
        // 自定义属性名
        treeNodeConfig.setIdKey("id"); // 默认为id可以不设置
        treeNodeConfig.setNameKey("label"); // 节点名对应名称 默认为name
        treeNodeConfig.setParentIdKey("parentId"); // 父节点 默认为parentId
        treeNodeConfig.setChildrenKey("children"); // 子点 默认为children
        treeNodeConfig.setDeep(10); // 可以配置递归深度 从0开始计算 默认此配置为空,即不限制
    }

    public List tree(Long currentUserId,Long excludeId,Boolean root) {
        List list = new ArrayList();
        List<Topic> topics = topicDao.condition(Topic.builder().userId(currentUserId).build());
        if(CollUtil.isEmpty(topics)){
            return list;
        }

        List<Tree<String>> threeList = TreeUtil.build(topics, "0", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(String.valueOf(treeNode.getId()));
            tree.setParentId(String.valueOf(treeNode.getParentId()));
            tree.setName(treeNode.getTitle());
            if(excludeId != null && treeNode.getFullPath().contains(String.valueOf(excludeId))){
                tree.putExtra("disabled",true);
            }
        });
        if(root){
            Tree<String> parent = new Tree<>();
            list.add(parent);
            parent.setName("ONE LEVEL");
            parent.putExtra("readOnly",true);
            parent.putExtra("label","ROOT");
            parent.setId("0");
            parent.setParentId("-1");
            parent.setChildren(threeList);
        }else{
            list = threeList;
        }
        return list;
    }

    public TopicVO info(Long id, Long currentUserId) {
        Topic condition = Topic.builder().userId(currentUserId).build();
        condition.setId(id);
        Topic topic = topicDao.conditionOne(condition);
        if(topic == null){
            throw new ServiceException("Unknown topic");
        }

        TopicVO topicVO = BeanUtil.copyProperties(topic, TopicVO.class);
        TopicStatisticVo statisticVo = pointService.statisticMastery(id);
        topicVO.setStatistic(statisticVo);
        return topicVO;
    }

    public boolean delete(Long topicId) {
        Topic condition = Topic.builder().build();
        condition.setId(topicId);
        Assert.isTrue(!pointService.existPointByTopicId(topicId),"该主题下有许多知识点，请先删除知识点");

        topicDao.delete(condition);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void increaseWeight(Long topicId) {
        Topic condition = Topic.builder().build();
        condition.setId(topicId);
        Topic topic = topicDao.conditionOne(condition);
        if(topic == null){
            log.error("The topic Id:{} is notfound.",topicId);
            return;
        }
        topic.setWeight(topic.getWeight()+1);
        topicDao.save(topic);
    }

    public Page list(TopicQO condition) {
        Page<TopicVO> page = topicDao.pageQuery(new Page(condition.getPageNo(), condition.getPageSize())
                , TopicVO.class
                , Topic.builder().title(condition.getTitle()).build());

        if(CollUtil.isNotEmpty(page.getDataList())){
            page.getDataList().forEach(t -> t.setFullTitle(getFullTitle(t.getTitle(),t.getFullParentName())));
        }
        return page;
    }

    @Override
    public List<TopicVO> getTitleByIds(List<Long> topicIds) {
        List<TopicVO> vos = new ArrayList<>();
        Topic topicCondition = Topic.builder().build();
        topicCondition.ids(topicIds);
        topicCondition.include("id","title","fullParentName","background");
        List<Topic> topics = topicDao.condition(topicCondition);
        if(CollUtil.isEmpty(topics)){
            return vos;
        }

        for (Topic topic : topics) {
            TopicVO topicVO = new TopicVO();
            topicVO.setId(topic.getId());
            topicVO.setBackground(topic.getBackground());
            topicVO.setTitle(topic.getTitle());
            topicVO.setFullTitle(getFullTitle(topic.getTitle(),topic.getFullParentName()));
            vos.add(topicVO);
        }

        return vos;
    }

    /**
     * generate full title
     * @param title
     * @param fullParentName
     * @return
     */
    private String getFullTitle(String title,String fullParentName) {
        return fullParentName != null
                ?fullParentName.replace(F_STRING,"/")+"/"+title
                :title;
    }
}
