package com.hotelpal.service.web.controller.admin;

import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.po.BannerPO;
import com.hotelpal.service.common.po.OrderPO;
import com.hotelpal.service.common.po.UserPO;
import com.hotelpal.service.common.so.BannerSO;
import com.hotelpal.service.common.so.OrderSO;
import com.hotelpal.service.common.so.PurchaseLogSO;
import com.hotelpal.service.common.utils.HttpPostUtils;
import com.hotelpal.service.common.vo.DailySalesVO;
import com.hotelpal.service.common.vo.PackVO;
import com.hotelpal.service.common.vo.PurchaseVO;
import com.hotelpal.service.common.vo.StatisticsVO;
import com.hotelpal.service.service.ContentService;
import com.hotelpal.service.service.UserService;
import com.hotelpal.service.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/content")
public class AdminContentController extends BaseController {
	@Resource
	private ContentService contentService;
	@Resource
	private UserService userService;
	
	@RequestMapping(value = "/getOrderList")
	@ResponseBody
	public PackVO<OrderPO> getOrderList(@RequestBody OrderSO so) {
		PackVO<OrderPO> res = new PackVO<>();
		res.setVoList(contentService.getOrderList(so));
		res.setPageInfo(so);
		return res;
	}

	@RequestMapping(value = "/getPurchaseOrderList")
	@ResponseBody
	public PackVO getPurchaseOrderList(@RequestBody PurchaseLogSO so) {
		PackVO<PurchaseVO> res = new PackVO<>();
		res.setVoList(contentService.getOrderList(so));
		res.setPageInfo(so);
		return res;
	}

	@RequestMapping(value = "/getBannerList")
	@ResponseBody
	public PackVO<BannerPO> getBannerList() {
		PackVO<BannerPO> res = new PackVO<>();
		res.setVoList(contentService.getMainBanner());
		return res;
	}

	@RequestMapping(value = "/updateBanner")
	@ResponseBody
	public PackVO<Void> updateBanner(@RequestBody BannerSO so) {
		contentService.updateBanner(so);
		return new PackVO<>();
	}

	@RequestMapping(value = "/removeBanner")
	@ResponseBody
	public PackVO<Void> removeBanner(Integer id) {
		contentService.removeBanner(id);
		return new PackVO<>();
	}

	@RequestMapping(value = "/createFreeCourseLink")
	@ResponseBody
	public PackVO<String> createFreeCourseLink(Integer courseNum, Integer validity) {
		PackVO<String> pack = new PackVO<>();
		String link = contentService.createFreeCourseLink(courseNum, validity);
		pack.setVo(link);
		return pack;
	}
	
	@RequestMapping(value = "/getStatisticsData")
	@ResponseBody
	public PackVO<StatisticsVO> getStatisticsData(Date from, Date to) {
		if (from == null) {
			Calendar cal = Calendar.getInstance();
			from = cal.getTime();
		}
		if (to == null) {
			to = new Date();
		}
		StatisticsVO res = contentService.getMainStatisticsData(from, to);
		PackVO<StatisticsVO> pack = new PackVO<>();
		pack.setVo(res);
		return pack;
	}
	
	@RequestMapping("/getDailySales")
	@ResponseBody
	public PackVO<DailySalesVO> getDailySales() {
		DailySalesVO res = contentService.getDailySales();
		PackVO<DailySalesVO> pack = new PackVO<>();
		pack.setVo(res);
		return pack;
	}
	

	@RequestMapping(value = "/authorizeOpenWx")
	@ResponseBody
	public PackVO authorizeOpenWx() {
//		HttpParams params = new HttpParams();
//		params.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=11_GL-pl3bq-3J-xD-VLqY4GqgXogLy_eC89FWYPYLO99hx8TVYgtV1H0y9JWemcfFQnXBWoulqWmv5KF4JSk_jSpMVx3F0hRfxA1es7o-WeG1d8MYKkAeJnG1or4zB9D5W2kabe49-oFjHPkkUEKNjAIAIOR");
//		params.setRequestEntity("{\"expire_seconds\": 28800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 1}}}");
//		System.out.println(HttpPostUtils.postMap(params));

		HttpParams params = new HttpParams();
		params.setUrl("https://api.weixin.qq.com/cgi-bin/component/api_component_token");
		params.setRequestEntity("{" +
				"\"component_appid\":\"wxfef930de3f27e265\" ," +
				"\"component_appsecret\": \"884c2eb0974d7615905e95d594cc53c9\"," +
				"\"component_verify_ticket\": \"qbFu4ZaAx56gbmmLVla3WKlNQ_Qqkx_d5mcP1MIe6dBbjUUimdoFIGoldlrSBytvTQv1awWjv_hvUYhwd2vtcQ\"" +
				"}");
		String at = HttpPostUtils.postMap(params);
		System.out.println(at);

		HttpParams params1 = new HttpParams();
		params1.setUrl("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=11_chOpjx-QTGUk1IFjW5VjbbzKgSY-Mc4xryVpmt47xuqFMA4fXrPL7r5AlgzGbzlnC49rk8gndu-D5-6UKYNJ_FvfM7V7VpBmlSXLS9SIo80fZwDPQBd0YbHjcY27ObADHTU-UdeirhpMn6OOWYShAJABDX");
		params1.setRequestEntity("{\"component_appid\":\"wxfef930de3f27e265\" }");
		System.out.println(HttpPostUtils.postMap(params1));
		return new PackVO();

	}

//	<xml><AppId><![CDATA[wxfef930de3f27e265]]></AppId>
//<CreateTime>1530538629</CreateTime>
//<InfoType><![CDATA[component_verify_ticket]]></InfoType>
//<ComponentVerifyTicket><![CDATA[ticket@@@qbFu4ZaAx56gbmmLVla3WKlNQ_Qqkx_d5mcP1MIe6dBbjUUimdoFIGoldlrSBytvTQv1awWjv_hvUYhwd2vtcQ]]></ComponentVerifyTicket>
//</xml>
//
//	{"component_access_token":"11_chOpjx-QTGUk1IFjW5VjbbzKgSY-Mc4xryVpmt47xuqFMA4fXrPL7r5AlgzGbzlnC49rk8gndu-D5-6UKYNJ_FvfM7V7VpBmlSXLS9SIo80fZwDPQBd0YbHjcY27ObADHTU-UdeirhpMn6OOWYShAJABDX","expires_in":7200}
//
//	{"pre_auth_code":"preauthcode@@@mQyg7dPQFYHZ3qqOVEo1gl5PHHwnQ8vY2Y7FchgdtsXlIa8gFqQeF0YMhxc4Khq4","expires_in":1800}


	@RequestMapping(value = "/updateStaticImg")
	@ResponseBody
	public PackVO updateStaticImg(Integer index, String url) {
		if (index != 1 && index != 2)
			throw new ServiceException("参数范围错误");
		contentService.updateStaticImg(index, url);
		return new PackVO();
	}

	@RequestMapping(value = "/getUserByPhone")
	@ResponseBody
	public PackVO getUserByPhone(@RequestBody PhonePacker phoneList) {
		PackVO<UserPO> res = new PackVO<>();
		res.setVoList(userService.getUserByPhone(phoneList.getPhoneList()));
		return res;
	}

	@RequestMapping(value = "/addCourse")
	@ResponseBody
	public PackVO addCourse(@RequestBody PhonePacker so) {
		contentService.addCourseToUser(so.getCourseId(), so.getDomainIdList());
		return new PackVO<>();
	}


}
class PhonePacker {
	List<String> phoneList;
	private Integer courseId;
	List<Integer> domainIdList;

	public List<String> getPhoneList() {
		return phoneList;
	}
	public void setPhoneList(List<String> phoneList) {
		this.phoneList = phoneList;
	}
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public List<Integer> getDomainIdList() {
		return domainIdList;
	}
	public void setDomainIdList(List<Integer> domainIdList) {
		this.domainIdList = domainIdList;
	}
}