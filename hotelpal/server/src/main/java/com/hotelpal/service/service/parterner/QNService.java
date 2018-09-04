package com.hotelpal.service.service.parterner;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.common.exception.ServiceException;
import com.hotelpal.service.common.mo.QNMetaDataMO;
import com.hotelpal.service.web.handler.PropertyHolder;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QNService {
	private static final Logger logger = LoggerFactory.getLogger(QNService.class);
	public static final String BUCKET_IMG = "hotelpalimgbucket";
	public static final String IMG_DOMAIN = "//img.hotelpal.cn/";
	public static final String BUCKET_AUDIO = "hotelpalaudiobucket";
	public static final String AUDIO_DOMAIN = "//audio.hotelpal.cn/";

	private static final String ACCESS_KEY = "QC0A1LD93q3bt4BrBlQLNSZkn3Y5Li1plQ8D7Gow";
	private static final String SECRET_KEY = "dVdYNKJm8q00KjrBF3XTe7PVJeZRRxNRuMd8ehE-";
	private static final String AUDIO_PIPELINE = "audioPipeline";
	private static final String persistentNotifyUrl = PropertyHolder.getProperty("QN_PERSIST_NOTIFY_URL");
	private static final String METADATA_URL = "http://rs.qiniu.com/stat/";

	public static void uploadToBucket(String bucketName, byte[] bytes, String key, boolean doCompress) {
		try {
			Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
			String upToken;
			if(doCompress) {
				String urlbase64 = UrlSafeBase64.encodeToString(bucketName + ":" + key);
				upToken = auth.uploadToken(bucketName, key, 10 * 60, new StringMap().put("persistentOps", "avthumb/mp3/aq/7|saveas/" + urlbase64).
						putNotEmpty("persistentPipeline", AUDIO_PIPELINE).put("persistentNotifyUrl", persistentNotifyUrl).put("force", 1));
			} else {
				upToken = auth.uploadToken(bucketName, key);
			}
			Configuration conf = new Configuration(Zone.autoZone());
			UploadManager uploadManager = new UploadManager(conf);
			Response res = uploadManager.put(bytes, key, upToken);
			if (!res.isOK()) {
				throw new ServiceException(res.bodyString());
			}
		} catch (Exception e) {
			throw new ServiceException(ServiceException.FILE_SERVER_FAILED, e);
		}
	}
	private static void deleteResource(String bucket, String key) {
		Configuration cfg = new Configuration(Zone.autoZone());
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
			bucketManager.delete(bucket, key);
		} catch (QiniuException ex) {
			logger.error("QN resource deletion failed... ", ex);
		}
	}
	public static QNMetaDataMO getMetaData(String bucketName, String key){
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		String code = UrlSafeBase64.encodeToString(bucketName + ":" + key);
		StringMap header = auth.authorization(METADATA_URL + code);
		Client client = new Client();
		QNMetaDataMO request = null;
		try {
			Response res = client.get(METADATA_URL + code, header);
			request = JSON.parseObject(res.bodyString(), QNMetaDataMO.class);
			return request;
		} catch (QiniuException e) {
			throw new ServiceException(e);
		}
	}
}
