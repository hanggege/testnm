package com.mei.base.util.glide;


import com.mei.wood.R;

/**
 * Created by joker on 16/6/30.
 * modify by Ling
 * 所有默认图的集合，注释上大小，同大小的可直接使用，或接近值使用，有特殊的请以文件名命名，方便寻找，
 * 并自觉备注大小：宽*高，及圆形头像除大小外需特殊格式：default_avatar_28
 */
public interface GlideDisPlayDefaultID {

    /**
     * ------------头像-----------
     **/
    int default_male_head = R.drawable.default_male_head;//男性默认头像
    int default_female_head = R.drawable.default_female_head;//女性默认头像
    int default_mentor_cover_head = R.drawable.bg_personal_page_head;//导师个人主页默认封面
    int reco_default_male_head = R.drawable.recommend_male_default;//推荐页男性默认头像
    int reco_default_female_head = R.drawable.recommend_female_default;//推荐页女性默认头像
    int default_avatar_28 = R.drawable.default_avatar_28;//28*28，头像
    int default_avatar_40 = R.drawable.default_avatar_40;//40*40, 头像
    int default_avatar_50 = R.drawable.default_avatar_50;//50*50，头像
    int default_avatar_72 = R.drawable.default_avatar_72;//72*72，头像，现用于我的页面消息头像
    int default_avatar_90 = R.drawable.default_avatar_90;//90*90，头像，现用于关注的导师列表
    int default_avatar_100 = R.drawable.default_avatar_100;//100*100，头像，现用于修改资料页
    int default_avatar_108 = R.drawable.default_avatar_108;//108*108头像，现用于搜索综合分类下导师头像
    int default_avatar_120 = R.drawable.default_avatar_120;//120*120头像，现用于搜索导师分类下导师头像
    int default_avatar_130 = R.drawable.default_avatar_130;//130*130，头像
    int default_avatar_150 = R.drawable.default_avatar_150;//150*150，头像
    /**
     * ------------正方形-----------
     **/
    int default_square_200 = R.drawable.default_square_200;//200*200，正方形
    int default_recommend_broadcast = R.drawable.default_recommend_broadcast;//230*230，现用于首页直播条
    int default_square_720 = R.drawable.default_error;//720*720，现用于教学专区特色导师背景

    int default_recommend_banner = R.drawable.default_recommend_banner;//690*220，现用于首页banner,学堂页面banner
    int default_recommend_elite = R.drawable.default_recommend_elite;//240*160，现用于首页/搜索的文章图片,情感测评列表
    /**
     * ------------导师形象照-----------
     **/
    int default_title_holder = R.drawable.default_title_holder;
    int default_gift = R.drawable.default_gift;//礼物默认加载图
}
