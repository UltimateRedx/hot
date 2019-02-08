package com.hotelpal.service.common.vo;

import com.hotelpal.service.common.po.CoursePO;
import com.hotelpal.service.common.po.LessonPO;
import com.hotelpal.service.common.po.ListenLogPO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.Map;

/**
 * @author Redx
 */
@Getter@Setter@Accessors(chain = true)
public class UserListenLogUnit {
    private Collection<ListenLogPO> listenLogs;
    private Map<Integer, LessonPO> lessonMap;
    private Map<Integer, CoursePO> courseMap;
}
