
package org.zarroboogs.keyboardlayout.smilepicker;


import org.zarroboogs.weibo.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmileyMap {

    public static final int GENERAL_EMOTION_POSITION = 0;

    public static final int EMOJI_EMOTION_POSITION = 2;

    public static final int HUAHUA_EMOTION_POSITION = 1;

    private static SmileyMap instance = new SmileyMap();
    private Map<String, Integer> general = new LinkedHashMap<>();
    private Map<String, Integer> huahua = new LinkedHashMap<>();

    private Map<String, Integer> sSmiles = new LinkedHashMap<>();


    private SmileyMap() {

        /**
         * general emotion
         */
        general.put("[doge]", R.drawable.d_doge);
        general.put("[馋嘴]", R.drawable.d_chanzui);

        general.put("[打脸]", R.drawable.d_dalian);
        general.put("[顶]", R.drawable.d_ding);
        general.put("[防毒面具]", R.drawable.d_fangdumianju);
        general.put("[肥皂]", R.drawable.d_feizao);
        general.put("[挤眼]", R.drawable.d_jiyan);

        general.put("[男孩儿]", R.drawable.d_nanhaier);
        general.put("[女孩儿]", R.drawable.d_nvhaier);
        general.put("[傻眼]", R.drawable.d_shayan);

        general.put("[神兽]", R.drawable.d_shenshou);
        general.put("[兔子]", R.drawable.d_tuzi);
        general.put("[笑哭]", R.drawable.d_xiaoku);
        general.put("[猪头]", R.drawable.d_zhutou);
        general.put("[给力]", R.drawable.f_geili);
        general.put("[互粉]", R.drawable.f_hufen);
        general.put("[囧]", R.drawable.f_jiong);
        general.put("[萌]", R.drawable.f_meng);
        general.put("[喜]", R.drawable.f_xi);
        general.put("[织]", R.drawable.f_zhi);
        general.put("[不要]", R.drawable.h_buyao);
        general.put("[good]", R.drawable.h_good);
        general.put("[握手]", R.drawable.h_woshou);
        general.put("[作揖]", R.drawable.h_zuoyi);
        general.put("[伤心]", R.drawable.l_shangxin);
        general.put("[飞机]", R.drawable.o_feiji);
        general.put("[干杯]", R.drawable.o_ganbei);
        general.put("[话筒]", R.drawable.o_huatong);
        general.put("[礼物]", R.drawable.o_liwu);
        general.put("[绿丝带]", R.drawable.o_lvsidai);
        general.put("[围脖]", R.drawable.o_weibo);
        general.put("[围观]", R.drawable.o_weiguan);
        general.put("[音乐]", R.drawable.o_yinyue);
        general.put("[照相机]", R.drawable.o_zhaoxiangji);
        general.put("[钟]", R.drawable.o_zhong);
        general.put("[浮云]", R.drawable.w_fuyun);
        general.put("[沙尘暴]", R.drawable.w_shachenbao);
        general.put("[太阳]", R.drawable.w_taiyang);
        general.put("[微风]", R.drawable.w_weifeng);
        general.put("[鲜花]", R.drawable.w_xianhua);
        general.put("[下雨]", R.drawable.w_xiayu);
        general.put("[月亮]", R.drawable.w_yueliang);


        general.put("[喵喵]", R.drawable.d_miao);
        general.put("[挖鼻屎]", R.drawable.d_wabishi);
        general.put("[泪]", R.drawable.d_lei);
        general.put("[亲亲]", R.drawable.d_qinqin);
        general.put("[晕]", R.drawable.d_yun);
        general.put("[可爱]", R.drawable.d_keai);
        general.put("[花心]", R.drawable.d_huaxin);
        general.put("[汗]", R.drawable.d_han);
        general.put("[衰]", R.drawable.d_shuai);
        general.put("[偷笑]", R.drawable.d_touxiao);
        general.put("[打哈欠]", R.drawable.d_dahaqi);
        general.put("[睡觉]", R.drawable.d_shuijiao);
        general.put("[哼]", R.drawable.d_heng);
        general.put("[可怜]", R.drawable.d_kelian);
        general.put("[右哼哼]", R.drawable.d_youhengheng);
        general.put("[酷]", R.drawable.d_ku);
        general.put("[生病]", R.drawable.d_shengbing);

//        general.put("[馋嘴]", R.drawable.d_chanshui);
        general.put("[害羞]", R.drawable.d_haixiu);
        general.put("[怒]", R.drawable.d_nu);
        general.put("[闭嘴]", R.drawable.d_bizui);
        general.put("[钱]", R.drawable.d_qian);
        general.put("[嘻嘻]", R.drawable.d_xixi);
        general.put("[左哼哼]", R.drawable.d_zuohengheng);
        general.put("[委屈]", R.drawable.d_weiqu);
        general.put("[鄙视]", R.drawable.d_bishi);
        general.put("[吃惊]", R.drawable.d_chijing);

        general.put("[吐]", R.drawable.d_tu);
        general.put("[懒得理你]", R.drawable.d_landelini);
        general.put("[思考]", R.drawable.d_sikao);
        general.put("[怒骂]", R.drawable.d_numa);
        general.put("[哈哈]", R.drawable.d_haha);
        general.put("[抓狂]", R.drawable.d_zhuakuang);
//        general.put("[抱抱]", R.drawable.d_baobao);
        general.put("[爱你]", R.drawable.d_aini);
        general.put("[鼓掌]", R.drawable.d_guzhang);
        general.put("[悲伤]", R.drawable.d_beishang);
        general.put("[嘘]", R.drawable.d_xu);
        general.put("[呵呵]", R.drawable.d_hehe);

        general.put("[太开心]", R.drawable.d_taikaixin);

        general.put("[感冒]", R.drawable.d_ganmao);
        general.put("[黑线]", R.drawable.d_heixian);
//        general.put("[愤怒]", R.drawable.d_fennu);
        general.put("[失望]", R.drawable.d_shiwang);
//        general.put("[做鬼脸]", R.drawable.d_zuoguilian);
        general.put("[阴险]", R.drawable.d_yinxian);
        general.put("[困]", R.drawable.d_kun);
        general.put("[拜拜]", R.drawable.d_baibai);
        general.put("[疑问]", R.drawable.d_yiwen);

        general.put("[神马]", R.drawable.f_shenma);
        general.put("[最右]", R.drawable.d_zuiyou);

        general.put("[赞]", R.drawable.h_zan);
        general.put("[心]", R.drawable.l_xin);
        // general.put("[伤心]", R.drawable.unheart);
        // general.put("[囧]", R.drawable.j_org);
        general.put("[奥特曼]", R.drawable.d_aoteman);
        general.put("[蜡烛]", R.drawable.o_lazhu);
        general.put("[蛋糕]", R.drawable.o_dangao);
        general.put("[弱]", R.drawable.h_ruo);
        general.put("[ok]", R.drawable.h_ok);
        general.put("[耶]", R.drawable.h_ye);

        general.put("[威武]", R.drawable.f_v5);
        // general.put("[猪头]", R.drawable.face281);
        // general.put("[月亮]", R.drawable.face18);
        // general.put("[浮云]", R.drawable.face229);
        // general.put("[咖啡]", R.drawable.face74);
        // general.put("[爱心传递]", R.drawable.face221);
        general.put("[来]", R.drawable.h_lai);

        general.put("[熊猫]", R.drawable.d_xiongmao);
        // general.put("[帅]", R.drawable.face94);
        // general.put("[不要]", R.drawable.face274);

        general.put("[笑cry]", R.drawable.lxh_xiaocry);

        /**
         * huahua emotion
         */
        huahua.put("[笑哈哈]", R.drawable.lxh_xiaohaha);
        huahua.put("[好爱哦]", R.drawable.lxh_haoaio);
        huahua.put("[噢耶]", R.drawable.lxh_oye);
        huahua.put("[偷乐]", R.drawable.lxh_toule);
        huahua.put("[泪流满面]", R.drawable.lxh_leiliumanmian);
        huahua.put("[巨汗]", R.drawable.lxh_juhan);
        huahua.put("[抠鼻屎]", R.drawable.lxh_koubishi);
        huahua.put("[求关注]", R.drawable.lxh_qiuguanzhu);
        huahua.put("[好喜欢]", R.drawable.lxh_haoxihuan);
        huahua.put("[崩溃]", R.drawable.lxh_bengkui);
        huahua.put("[好囧]", R.drawable.lxh_haojiong);
        huahua.put("[震惊]", R.drawable.lxh_zhenjing);
        huahua.put("[别烦我]", R.drawable.lxh_biefanwo);
        huahua.put("[不好意思]", R.drawable.lxh_buhaoyisi);
        huahua.put("[羞嗒嗒]", R.drawable.lxh_xiudada);
        huahua.put("[得意地笑]", R.drawable.lxh_deyidexiao);
        huahua.put("[纠结]", R.drawable.lxh_jiujie);
        huahua.put("[给劲]", R.drawable.lxh_feijin);
        huahua.put("[悲催]", R.drawable.lxh_beicui);
        huahua.put("[甩甩手]", R.drawable.lxh_shuaishuaishou);
        huahua.put("[好棒]", R.drawable.lxh_haobang);
        huahua.put("[瞧瞧]", R.drawable.lxh_qiaoqiao);
        huahua.put("[不想上班]", R.drawable.lxh_buxiangshangban);
        huahua.put("[困死了]", R.drawable.lxh_kunsile);
        huahua.put("[许愿]", R.drawable.lxh_xuyuan);
        huahua.put("[丘比特]", R.drawable.lxh_qiubite);
        huahua.put("[有鸭梨]", R.drawable.lxh_youyali);
        huahua.put("[想一想]", R.drawable.lxh_xiangyixiang);
//        huahua.put("[躁狂症]", R.drawable.lxh_kuangzaozheng);
        huahua.put("[转发]", R.drawable.lxh_zhuanfa);
        huahua.put("[互相膜拜]", R.drawable.lxh_xianghumobai);
        huahua.put("[雷锋]", R.drawable.lxh_leifeng);
        huahua.put("[杰克逊]", R.drawable.lxh_jiekexun);
        huahua.put("[玫瑰]", R.drawable.lxh_meigui);
        huahua.put("[hold住]", R.drawable.lxh_holdzhu);
        huahua.put("[群体围观]", R.drawable.lxh_quntiweiguan);
        huahua.put("[推荐]", R.drawable.lxh_tuijian);
        huahua.put("[赞啊]", R.drawable.lxh_zana);
        huahua.put("[被电]", R.drawable.lxh_beidian);
        huahua.put("[霹雳]", R.drawable.lxh_pili);

        sSmiles.putAll(huahua);
        sSmiles.putAll(general);
    }

    public static SmileyMap getInstance() {
        return instance;
    }

    public Map<String, Integer> getSmiles() {
        return sSmiles;
    }

    public Map<String, Integer> getGeneral() {
        return general;
    }

    public Map<String, Integer> getHuahua() {
        return huahua;
    }
}
