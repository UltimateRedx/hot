package com.hotelpal.service.web;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
	private static final Lock lock = new ReentrantLock();
	private static String token = "hootttelpal";
	private static String key = "hotelpal66666666hotelpal66666666hotelpal666";
	private static String appId = "wxfef930de3f27e265";
	private static String encrypt = "jZtTirIBr7BrLz0VTNLa2tEALtHOTWgV1Zvn29sjuKRxOdO2wpYc18nGoaweEyKhyGh/QRRDxMpeIKJIEIjnkiheatbtF+bVP3AFhPLPvT7Zp2TtCmlh2lTgWr0KXz6dc3CZQ2T1tyZovqQ/5tMqVW/P9bNEETzRzmxn9hSbx/4T3aKXXggabTQ0uu9wpX3mO7ebc644AQJCkHcj9S9/0vYDfMVZLJrJ+4rAWojhs9uj6afKcQvM+vRSRofBsbA/l6rSwz6qApZVzc0EXy2mIB2oNQ7lyAJ8OAWBZYOjG7ZCLV87rX0KTQHYEzE5kG5NLYAm3is72Qxr5Fj6XoPOekJU8Yr64aRq1p+y+La8sBlhx+8ZMAQSmDSqeB0svw6ACzbP9Xkxu8BZVPy4wB1HTrCRVfLvmR+ncv1Zt+dQhpoGIF5vH638X1TMlYYESyrvDmyYLd8SmMACkjTIQiNOIw==";
	private static String postData = "<xml>\n" +
			"    <AppId><![CDATA[wxfef930de3f27e265]]></AppId>\n" +
			"    <Encrypt><![CDATA[jZtTirIBr7BrLz0VTNLa2tEALtHOTWgV1Zvn29sjuKRxOdO2wpYc18nGoaweEyKhyGh/QRRDxMpeIKJIEIjnkiheatbtF+bVP3AFhPLPvT7Zp2TtCmlh2lTgWr0KXz6dc3CZQ2T1tyZovqQ/5tMqVW/P9bNEETzRzmxn9hSbx/4T3aKXXggabTQ0uu9wpX3mO7ebc644AQJCkHcj9S9/0vYDfMVZLJrJ+4rAWojhs9uj6afKcQvM+vRSRofBsbA/l6rSwz6qApZVzc0EXy2mIB2oNQ7lyAJ8OAWBZYOjG7ZCLV87rX0KTQHYEzE5kG5NLYAm3is72Qxr5Fj6XoPOekJU8Yr64aRq1p+y+La8sBlhx+8ZMAQSmDSqeB0svw6ACzbP9Xkxu8BZVPy4wB1HTrCRVfLvmR+ncv1Zt+dQhpoGIF5vH638X1TMlYYESyrvDmyYLd8SmMACkjTIQiNOIw==]]></Encrypt>\n" +
			"</xml>\n";
	public static void main(String[] args) {
		System.out.println(new Date().getTime());
		System.out.println(new Date(new Date().getTime()));
//		try {
//			System.out.println(new WXBizMsgCrypt(token, key, appId).decrypt(encrypt));
//////			System.out.println(new WXBizMsgCrypt(token, key, appId).decryptMsg("43bb9ed00de731183868f6de54dd30308cc1bf05", "1530533675", "1117155145", postData));
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		HttpParams params = new HttpParams();
//		params.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=11_GL-pl3bq-3J-xD-VLqY4GqgXogLy_eC89FWYPYLO99hx8TVYgtV1H0y9JWemcfFQnXBWoulqWmv5KF4JSk_jSpMVx3F0hRfxA1es7o-WeG1d8MYKkAeJnG1or4zB9D5W2kabe49-oFjHPkkUEKNjAIAIOR");
//		params.setRequestEntity("{\"expire_seconds\": 28800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 1}}}");
//		System.out.println(HttpPostUtils.postMap(params));

//		HttpParams params = new HttpParams();
//		params.setUrl("https://api.weixin.qq.com/cgi-bin/component/api_component_token");
//		params.setRequestEntity("{" +
//				"\"component_appid\":\"wxfef930de3f27e265\" ," +
//				"\"component_appsecret\": \"884c2eb0974d7615905e95d594cc53c9\"," +
//				"\"component_verify_ticket\": \"qbFu4ZaAx56gbmmLVla3WKlNQ_Qqkx_d5mcP1MIe6dBbjUUimdoFIGoldlrSBytvTQv1awWjv_hvUYhwd2vtcQ\"" +
//				"}");
//		System.out.println(HttpPostUtils.postMap(params));

//		HttpParams params = new HttpParams();
//		params.setUrl("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=11_chOpjx-QTGUk1IFjW5VjbbzKgSY-Mc4xryVpmt47xuqFMA4fXrPL7r5AlgzGbzlnC49rk8gndu-D5-6UKYNJ_FvfM7V7VpBmlSXLS9SIo80fZwDPQBd0YbHjcY27ObADHTU-UdeirhpMn6OOWYShAJABDX");
//		params.setRequestEntity("{\"component_appid\":\"wxfef930de3f27e265\" }");
//		System.out.println(HttpPostUtils.postMap(params));
//		BigDecimal oh = new BigDecimal(100*100*100).divide(new BigDecimal(6000), RoundingMode.HALF_UP);
//		System.out.println(oh);
//		System.out.println(oh.multiply(new BigDecimal(3)).doubleValue());
	}
}
