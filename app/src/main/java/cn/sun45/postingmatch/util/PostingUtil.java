package cn.sun45.postingmatch.util;

import java.util.regex.Pattern;

/**
 * Created by Sun45 on 2022/4/29
 * 求职方法类
 */
public class PostingUtil {
    private static final String number = "([0-9]+|一|二|两|三|四|五|六|七|八|九)(万|W|w|千|K|k|百)?[0-9]*)";
    private static final String firstDes = "(B站|b服|B服|pcr|PCR|bcr|BCR|国服|B服母猪|公主连结官服|公主链接|公主连结|开服|老牌|自动|转型自动|auto|AUTO|半自动|半auto|退档auto|自理三刀|不排刀|躺|休闲|体闲|闲|睡饱饱|咸鱼|养老|摸鱼公会|全是二次元的" +
            "|白羊座|金牛座|双子座|巨蟹座|狮子座|处女座|天秤座|天蝎座|射手座|魔羯座|水瓶座|双鱼座)";
    private static final String secondDes = "(稳|稳定|随缘|随缘冲|随缘保|持续稳保|不排不筛乱保|下期保|冲|望|保|保底|稳|稳保|只想保|排名|会战|上期|本期|这期|长期)";
    private static final String thirdDes = "(行会|公会|会战|公会战|工会战)";
    private static final String fourthDes = "(招募|招人|收人|纳新)";

    private static String[] recruitRegexList = new String[]{
            ".*(在招人了|招人啦|招人了|想招人|招人喽|招人咯|招人辣|招人嘞|招人~|吃人|收人啦|长期招人|悬赏招募|招募令|招募啦|招点人|招管理|招人招人|招人招人招人|求求来点人吧|有意者加群|不进来看一下吗|进来看看吧|合会|公会招募|渠道服|来个人).*",
            ".*(来|求|缺|招|差|招收|收)([0-9]|几|一|二|两|三|四|五)(人|位|个|来|鸭).*",
            ".*公会名(:|：).*",
            ".*[0-9]+个位置.*",
            ".*(\\(|（|【)[0-9]{1,2}(=|/|／)[0-9]{1,2}(\\)|）|】).*",
            "^(［招人］|【公会招人】|\\[国服招募\\]).*",
            "^" + firstDes + "?" + thirdDes + "?" + fourthDes + "$",
            "(^|.*( |,|，|\\?|？|!|！|\\)|）|］|】|\\]|_))(" + firstDes + "((前)?" + number + "?|" + secondDes + "((前)?" + number + "|((前)?" + number + "档?)((冲|随缘)[0-9]+)?(((公会|休闲快乐|休闲|养老|咸鱼养生|睡饱饱|自动刀|凹凸)" + thirdDes + "?)|" + thirdDes + ")?" + fourthDes + "?.*",
    };

    public static void main(String[] args) {
        String title = "公主连结600会招人";
        boolean match = title.matches(recruitRegexList[7]);
        System.out.println("match:" + match);
    }

    /**
     * 匹配招募信息
     *
     * @param title
     * @return
     */
    public static boolean matchRecruit(String title) {
        for (String regex : recruitRegexList) {
            if (Pattern.matches(regex, title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 匹配求职信息
     *
     * @param title
     * @return
     */
    public static boolean matchPosting(String title) {
        boolean matchRecruit = matchRecruit(title);
        if (!matchRecruit) {
            return true;
        } else {
            return false;
        }
    }
}
