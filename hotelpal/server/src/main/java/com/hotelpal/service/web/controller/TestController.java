package com.hotelpal.service.web.controller;

import com.hotelpal.service.service.ContentService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.service.parterner.wx.WXService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {
	@Resource
	private UserService userService;
	@Resource
	private ContentService contentService;
	@Resource
	protected JdbcTemplate dao;
	@Resource
	private WXService wxService;
	@RequestMapping(value = "/test")
	@ResponseBody
	public String test() {
		wxService.getAccessToken();
		return "Done...";
	}

	//剩余免费兑换次数写入学习卡
	private void runTask() {
		String userCourseSql = "select rela.domainId, sum(freeCourseNum) " +
				" from hp_user_course uc" +
				" inner join cc_user_rela rela on uc.userPhone=rela.phone " +
				" group by uc.userPhone";
		Map<Integer, Integer> userCourseMap = new HashMap<>();
		dao.query(userCourseSql, new Object[]{}, rch -> {
			userCourseMap.put(Integer.valueOf(String.valueOf(rch.getByte(1))), Integer.valueOf(String.valueOf(rch.getInt(2))));
		});
		String inviteSql = "select domainId from cc_user_rela where regChannel=INVITED";
		List<Integer> invitedDomainList = dao.queryForList(inviteSql, Integer.class);
		for (Integer domainId : invitedDomainList) {
			if (userCourseMap.containsKey(domainId)) {
				userCourseMap.put(domainId, userCourseMap.get(domainId) + 1);
			} else {
				userCourseMap.put(domainId, 1);
			}
		}
		//使用过的
		String used = "select domainId,count(id) from cc_purchase_log where payMethod='FREE' group by domainId";
		Map<Integer, Integer> usedMap = new HashMap<>();
		dao.query(used, new Object[]{}, rch -> {
			usedMap.put(Integer.valueOf(String.valueOf(rch.getByte(1))), Integer.valueOf(String.valueOf(rch.getInt(2))));
		});

		String sql = "insert into cc_user_coupon (createTime,updateTime,domainId,`type`,used,validity)" +
				" values(sysdate(),sysdate(),?,?,'N','9999')";
		for (Map.Entry<Integer, Integer> en : userCourseMap.entrySet()) {
			int left = en.getValue() - (usedMap.containsKey(en.getKey()) ? userCourseMap.get(en.getKey()) : 0);
			for (int i = 0; i < left; i++)
				dao.update(sql, en.getKey(), "CARD");
		}
	}
}
