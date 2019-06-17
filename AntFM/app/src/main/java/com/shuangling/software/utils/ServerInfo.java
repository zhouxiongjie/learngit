package com.shuangling.software.utils;

public class ServerInfo {

	



	public static String serviceIP="http://api-cms.slradio.cn";
	//测试环境
	//public static String serviceIP="http://api-cms.review.slradio.cn";

	public static String h5IP="http://www-cms-c.review.slradio.cn";

	public static String getRecommendColumns="/v1/c_cassifies";    		//推荐栏目

	public static String getColumnContent="/v1/classify_content/";    	//栏目内容

	public static String getCityList="/v1/city";    					//城市列表

	public static String getCityService="/v1/local_service";    		//生活服务

	public static String getCityAnchors="/v1/city_anchors";    			//同城主播

	public static String getCityContent="/v1/classify_info";			//同城新闻

	public static String getArticleDetail="/article-detail/";			//文章详情

	public static String getGalleriaDetail="/atlas-detail/";			//图集详情

	public static String getSpecialDetail="/topic-details/";			//专题详情

	public static String getAlbumDetail="/v1/c_album/";					//专辑详情

	public static String getRelatedRecommend="/v1/relatedPosts";		//相关推荐

	public static String getAlbumAudios="/v1/c_audios/";				//专辑下所有音频

	//登录模块
	public static String login="/v1/consumer/login";					//账号密码登陆

	public static String getVerifyCode="/v1/consumer/phone_captcha";	//获取验证码

	public static String resetPassword="/v1/consumer/user/password";	//重置密码

	public static String search="/v1/contents";							//搜索

	public static String anchorDetail="/v1/c_merchant/";				//主播机构详情

	public static String globalDecorate="/v1/consumer/skins";			//全局装修

    public static String topPost="/v1/top_posts";			            //热门置顶

    public static String marvellousPosts="/v1/marvellousPosts";         //热门精彩推荐

    public static String getRadioList="/v1/mobile/channel";    			//电台列表


}
