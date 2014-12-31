
package org.zarroboogs.weibo.widget;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: qii Date: 13-3-4 42+6=48
 */
public class SmileyMap {

    public static final int GENERAL_EMOTION_POSITION = 0;

    public static final int EMOJI_EMOTION_POSITION = 2;

    public static final int HUAHUA_EMOTION_POSITION = 1;

    private static SmileyMap instance = new SmileyMap();
    private Map<String, String> general = new LinkedHashMap<String, String>();
    private Map<String, String> huahua = new LinkedHashMap<String, String>();

    private SmileyMap() {

        /**
         * general emotion
         */
        general.put("[挖鼻屎]", "d_wabishi.png");
        general.put("[泪]", "d_lei.png");
        general.put("[亲亲]", "d_qinqin.png");
        general.put("[晕]", "d_yun.png");
        general.put("[可爱]", "d_keai.png");
        general.put("[花心]", "d_huaxin.png");
        general.put("[汗]", "d_han.png");
        general.put("[衰]", "d_shuai.png");
        general.put("[偷笑]", "d_touxiao.png");
        general.put("[打哈欠]", "d_dahaqi.png");
        general.put("[睡觉]", "d_shuijiao.png");
        general.put("[哼]", "d_heng.png");
        general.put("[可怜]", "d_kelian.png");
        general.put("[右哼哼]", "d_youhengheng.png");
        general.put("[酷]", "d_ku.png");
        general.put("[生病]", "d_shengbing.png");

        general.put("[馋嘴]", "d_chanshui.png");
        general.put("[害羞]", "d_haixiu.png");
        general.put("[怒]", "d_nu.png");
        general.put("[闭嘴]", "d_bizui.png");
        general.put("[钱]", "d_qian.png");
        general.put("[嘻嘻]", "d_xixi.png");
        general.put("[左哼哼]", "d_zuohengheng.png");
        general.put("[委屈]", "d_weiqu.png");
        general.put("[鄙视]", "d_bishi.png");
        general.put("[吃惊]", "d_chijing.png");

        general.put("[吐]", "d_tu.png");
        general.put("[懒得理你]", "d_landelini.png");
        general.put("[思考]", "d_sikao.png");
        general.put("[怒骂]", "d_numa.png");
        general.put("[哈哈]", "d_haha.png");
        general.put("[抓狂]", "d_zhuakuang.png");
        general.put("[抱抱]", "d_baobao.png");
        general.put("[爱你]", "d_aini.png");
        general.put("[鼓掌]", "d_guzhang.png");
        general.put("[悲伤]", "d_beishang.png");
        general.put("[嘘]", "d_xu.png");
        general.put("[呵呵]", "d_hehe.png");

        general.put("[太开心]", "d_taikaixin.png");

        general.put("[感冒]", "d_ganmao.png");
        general.put("[黑线]", "d_heixian.png");
        general.put("[愤怒]", "d_fennu.png");
        general.put("[失望]", "d_shiwang.png");
        general.put("[做鬼脸]", "d_zuoguilian.png");
        general.put("[阴险]", "d_yinxian.png");
        general.put("[困]", "d_kun.png");
        general.put("[拜拜]", "d_baibai.png");
        general.put("[疑问]", "d_yiwen.png");

        general.put("[doge]", "d_doge.png");

        general.put("[喵喵]", "d_miao.png");
        general.put("[神马]", "f_shenma.png");
        general.put("[最右]", "d_zuiyou.png");

        general.put("[赞]", "h_zan.png");
        general.put("[心]", "l_xin.png");
        // general.put("[伤心]", "unheart.png");
        // general.put("[囧]", "j_org.png");
        general.put("[奥特曼]", "d_aoteman.png");
        general.put("[蜡烛]", "o_lazhu.png");
        general.put("[蛋糕]", "o_dangao.png");
        general.put("[弱]", "h_ruo.png");
        general.put("[ok]", "h_ok.png");
        general.put("[耶]", "h_ye.png");

        general.put("[威武]", "f_v5.png");
        // general.put("[猪头]", "face281.png");
        // general.put("[月亮]", "face18.png");
        // general.put("[浮云]", "face229.png");
        // general.put("[咖啡]", "face74.png");
        // general.put("[爱心传递]", "face221.png");
        general.put("[来]", "h_lai.png");

        general.put("[熊猫]", "d_xiongmao.png");
        // general.put("[帅]", "face94.png");
        // general.put("[不要]", "face274.png");

        general.put("[笑cry]", "emoji_0x1f602.png");

        /**
         * huahua emotion
         */
        huahua.put("[笑哈哈]", "lxh_xiaohaha.png");
        huahua.put("[好爱哦]", "lxh_haoaio.png");
        huahua.put("[噢耶]", "lxh_oye.png");
        huahua.put("[偷乐]", "lxh_toule.png");
        huahua.put("[泪流满面]", "lxh_leiliumanmian.png");
        huahua.put("[巨汗]", "lxh_juhan.png");
        huahua.put("[抠鼻屎]", "lxh_koubishi.png");
        huahua.put("[求关注]", "lxh_qiuguanzhu.png");
        huahua.put("[好喜欢]", "lxh_haoxihuan.png");
        huahua.put("[崩溃]", "lxh_bengkui.png");
        huahua.put("[好囧]", "lxh_haojiong.png");
        huahua.put("[震惊]", "lxh_zhenjing.png");
        huahua.put("[别烦我]", "lxh_biefanwo.png");
        huahua.put("[不好意思]", "lxh_buhaoyisi.png");
        huahua.put("[羞嗒嗒]", "lxh_xiudada.png");
        huahua.put("[得意地笑]", "lxh_deyidexiao.png");
        huahua.put("[纠结]", "lxh_jiujie.png");
        huahua.put("[给劲]", "lxh_feijin.png");
        huahua.put("[悲催]", "lxh_beicui.png");
        huahua.put("[甩甩手]", "lxh_shuaishuaishou.png");
        huahua.put("[好棒]", "lxh_haobang.png");
        huahua.put("[瞧瞧]", "lxh_qiaoqiao.png");
        huahua.put("[不想上班]", "lxh_buxiangshangban.png");
        huahua.put("[困死了]", "lxh_kunsile.png");
        huahua.put("[许愿]", "lxh_xuyuan.png");
        huahua.put("[丘比特]", "lxh_qiubite.png");
        huahua.put("[有鸭梨]", "lxh_youyali.png");
        huahua.put("[想一想]", "lxh_xiangyixiang.png");
        huahua.put("[躁狂症]", "lxh_kuangzaozheng.png");
        huahua.put("[转发]", "lxh_zhuanfa.png");
        huahua.put("[互相膜拜]", "lxh_xianghumobai.png");
        huahua.put("[雷锋]", "lxh_leifeng.png");
        huahua.put("[杰克逊]", "lxh_jiekexun.png");
        huahua.put("[玫瑰]", "lxh_meigui.png");
        huahua.put("[hold住]", "lxh_holdzhu.png");
        huahua.put("[群体围观]", "lxh_quntiweiguan.png");
        huahua.put("[推荐]", "lxh_tuijian.png");
        huahua.put("[赞啊]", "lxh_zana.png");
        huahua.put("[被电]", "lxh_beidian.png");
        huahua.put("[霹雳]", "lxh_pili.png");

    }

    public static SmileyMap getInstance() {
        return instance;
    }

    public Map<String, String> getGeneral() {
        return general;
    }

    public Map<String, String> getHuahua() {
        return huahua;
    }
}
