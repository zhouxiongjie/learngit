package com.shuangling.software.utils;

public class ServerInfo {







	//防城港
	public static String serviceIP="http://api-cms.fcgtvb.com";  		//测试环境

	public static String h5IP="http://cms.fcgtvb.com";

	public static String h5HttpsIP="https://cms.fcgtvb.com";

	public static String activity="https://asc.fcgtvb.com/";

	public static String scs="http://scs.fcgtvb.com";

	public static String emc="http://api-emc.fcgtvb.com";

	public static String vms="http://api-vms.fcgtvb.com";



	public static String getRecommendColumns="/v1/c_cassifies";    				//推荐栏目

	public static String getColumnContent="/v1/classify_content/";    			//栏目内容

	public static String getCityList="/v1/city";    							//城市列表

	public static String getCityService="/v1/local_service";    				//生活服务

	public static String getCityAnchors="/v1/city_anchors";    					//同城主播

	public static String getCityContent="/v1/classify_info";					//同城新闻

	public static String getArticlePage="/posts/";								//文章h5页面

	public static String getGalleriaPage="/atlas/";					    		//图集h5页面

    public static String getGalleriaDetail="/v1/c_galleries/";					//图集详情

	public static String getSpecialPage="/specials/";							//专题h5页面

    public static String getSpecialDetail="/v1/c_special/";						//专题详情

    public static String getAlbumDetail="/v1/c_album/";							//专辑详情

	public static String getArticleDetail="/v1/c_article/";						//文章详情

	public static String getAudioDetail="/v1/c_audio/";							//音频详情

	public static String getRelatedRecommend="/v1/relatedPosts";				//相关推荐

	public static String getAlbumAudios="/v1/c_audios/";						//专辑下所有音频

	//登录模块
	public static String login="/v1/consumer/app_login";						//账号密码登陆

	public static String refreshTokenLogin="/v1/consumer/login_by_token";		//APP通过RefreshToke登录

	public static String getVerifyCode="/v1/consumer/phone_captcha";			//获取验证码

	public static String resetPassword="/v1/consumer/user/password";			//重置密码

	public static String modifyPhone="/v1/consumer/user/phone";					//修改手机号

	public static String search="/v1/contents";									//搜索

	public static String anchorDetail="/v1/c_merchant/";						//主播机构详情

	public static String globalDecorate="/v1/consumer/skins";					//全局装修

	public static String indexDecorate="/v1/consumer/layouts";					//首页装修

    public static String topPost="/v1/top_posts";			            		//热门置顶

    public static String marvellousPosts="/v1/marvellousPosts";         		//热门精彩推荐

    public static String getRadioList="/v1/mobile/channel";    					//电台列表

	public static String getRadioDetail="/v1/mobile/channel/play";    			//电台详情

	public static String getRadioRecommend="/v1/mobile/channel/recommend";    	//相关推荐

	public static String getComentList="/v1/mobile/comment";   					//评论列表

	public static String getLevelTwoComentList="/v1/mobile/comment/infos";		//二级评论

	public static String praise="/v1/mobile/comment/fabulous";					//点赞

	public static String getPrograms="/v1/merchant_programs/";					//获取主播或机构下内容

	public static String getVideoDetail="/v1/c_video/";							//获取主播或机构下内容

	public static String collect="/v1/mobile/channel/collection/status";		//收藏接口（带状态） 1电台、2电视

	public static String attention="/v1/follow";								//关注

	public static String collect01="/v1/collection";							//收藏文章/视频/专题/图集

	public static String like="/v1/like";										//点赞文章/专题/视频

	public static String weather="/v1/consumer/weather";						//获取天气

	public static String feedback="/v1/feedback";								//意见反馈

	public static String history="/v1/my_history";								//历史记录

	public static String myCollect="/v1/my_collection";							//我的收藏-文章/视频/专题/图集

	public static String subscribes="/v1/subscribes";							//订阅专辑

	public static String mySubscribes="/v1/my_subscribes";						//我的订阅

	public static String myAttention="/v1/my_follows";							//我的关注

	public static String myRadioCollect="/v1/mobile/channel/collection";		//我的收藏-电台/电视台

	public static String radioHistory="/v1/mobile/channel/record";				//历史记录-电台/电视台

	public static String messages="/v1/my_messages";							//我的消息

	public static String statistics="/v1/my_statistics";						//我的关注量/未读消息量

	public static String readMessage="/v1/message/is_read/";					//读消息

	public static String appOss="/v1/app_oss";									//获取app的oss信息

	public static String modifyUserInfo="/v1/consumer/user";					//修改个人信息

	public static String guides="/v1/consumer/guides";							//导航页面

	public static String getStationInfo="/v1/consumer/configs";					//站点设置

	public static String getRecommendAnchor="/v1/recommend_merchant/"; 			//获取推荐主播、机构

	public static String getOrganizationMenus="/v1/c_menus/"; 					//获取机构菜单信息

	public static String getOrganizationRadio="/v1/mobile/channel/merchant";	//获取机构电台

	public static String getOrganizationAnchor="/v1/merchant_anchors/";			//获取机构下主播

	public static String addRadioHistory="/v1/mobile/channel/view";				//收听量记录

	public static String serviceClick="/v1/c_service/";							//服务点击

	public static String updateInfo="/v1/consumer/app_version";					//APP获取是否需要更新

	public static String updateInfoV2="/v2/consumer/app_version";

	public static String shareStatistics="/v2/share";							//分享添加来记录

    public static String bottomMenus="/v2/consumer/menus";					    //C端获取底部菜单

	public static String indexDecorateContent="/v2/cassify_posts";			    //首页装修内容（c端）

	public static String columnDecorateContent="/v2/consumer/category_layouts/";//C端分类装修详情

	public static String startAdvert="/v2/consumer/app_advertisings";			//启动广告

	public static String clickAdvert="/v2/consumer/app_advertisings/";			//C端点击广告

	public static String getAnchorOrOrganizationColumn="/v2/merchant_columns/";	//机构或者主播栏目

	public static String getAnchorOrOrganizationLive="/v2/live/";

	public static String getServices="/v1/services";

	public static String accountDetail="/v1/consumer/account";

	public static String incomeDetail="/v1/consumer/account/details";

	public static String takeCashDetail="/v1/consumer/transfers";

	public static String takeCashAccount="/v1/consumer/transfer_alipay";

	public static String bindZhifubao="/v1/consumer/transfer_alipay";			//绑定支付宝

	public static String requestCash="/v1/consumer/transfers";					//发起提现请求

	public static String getCashDetail="/v1/consumer/transfers/";				//获取提现详情

	public static String getCashRegular="/v1/consumer/transfer_config";			//获取提现设置
}
