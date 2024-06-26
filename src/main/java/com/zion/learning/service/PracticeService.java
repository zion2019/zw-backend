package com.zion.learning.service;

import com.zion.common.basic.Page;
import com.zion.common.vo.learning.request.PracticeQO;
import com.zion.common.vo.learning.response.PractiseVO;

public interface PracticeService {

    Page<PractiseVO> todayList(PracticeQO qo);

    boolean practise(PracticeQO qo);

    PractiseVO nextPoint(PracticeQO qo);

    boolean saveNext(PracticeQO qo);

    boolean deleteByPointId(Long pointId);
}
