package com.example.haiyang.common.constants;

/**
 * @Author Cbc
 * @DateTime 2024/10/15 10:28
 * @Description
 */
public class RedisConstants {

    //存储用户登录信息
    public final static String LOGIN = "user:login:";

    //根据类型获取文章
    public final static String ARTICLE_TYPE = "article:type:";

    //存储ai对话历史
    public final static String AI_HISTORY = "aiHistory:";

    //用户请求频率
    public final static String  USER_FREQUENCY_LIST = "user:frequency:list:";

    //记录上一次时间
    public final static String USER_FREQUENCY_TIME = "user:frequency:list:time";

    public final static String BLACKLIST = "blacklist:";


    public final static String HOT_POST_IDS = "post:hot:ids";

    public final static String BLOOM_USER_POST = "bloom:userPost";

    public final static String POST_IDS = "post:ids";

    public final static String HOT_POST_MAP = "post:hot:map";

    public final static String USER_LIKE_POST = "user:like:post";

    public final static String TASK_REMIND = "task_remind";
}
