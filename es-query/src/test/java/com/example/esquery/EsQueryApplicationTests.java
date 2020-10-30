package com.example.esquery;

import com.example.esquery.dao.MessageRepository;
import com.example.esquery.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class EsQueryApplicationTests {

    @Autowired
    MessageRepository msgRep;

    //清空数据并验证数据条数是否已为0
    @Order(0)
    @Test
    @DisplayName("清空所有数据")
    void clearIndex() {
        msgRep.deleteAll();
        assertEquals(0, msgRep.count(), "数据已清空完毕");
    }

    @ParameterizedTest
    @Order(1)
    @CsvSource({
            "陶渊明,盛年不重来，一日难再晨。及时宜自勉，岁月不待人。,陶渊明,1,https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1593064379767&di=c51c7e5e4cb68be7f81362efe43090de&imgtype=0&src=http%3A%2F%2Fp1.meituan.net%2Favatar%2F1d5b7593a0679ddc03240b9b6d7630fa77146.jpg",
            "老子,千里之行，始于足下。,老子,2,https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1593064379767&di=c51c7e5e4cb68be7f81362efe43090de&imgtype=0&src=http%3A%2F%2Fp1.meituan.net%2Favatar%2F1d5b7593a0679ddc03240b9b6d7630fa77146.jpg",
            "庄子,君子之交淡若水，小人之交甘若醴，君子淡以亲，小人甘以绝。,庄子,3,https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1593064379767&di=c51c7e5e4cb68be7f81362efe43090de&imgtype=0&src=http%3A%2F%2Fp1.meituan.net%2Favatar%2F1d5b7593a0679ddc03240b9b6d7630fa77146.jpg",
            "2015年11月15日XX版本强势来袭！,亲爱的小伙伴，全新版本“逐鹿中原”将于2015年11月15日更新，将新增“逐鹿中原”功能，五大神将祝君争霸天下！,运营,4,https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1593064379767&di=c51c7e5e4cb68be7f81362efe43090de&imgtype=0&src=http%3A%2F%2Fp1.meituan.net%2Favatar%2F1d5b7593a0679ddc03240b9b6d7630fa77146.jpg",
            "服务器停机维护公告,亲爱的各位小伙伴，为了给大家一个更好的游戏体验，服务器将于XX停服维护，预计维护时间1个小时，服务器开启时间将根据实际操作情况进行提前或者延顺，给您带来的不便请您谅解，感谢您对我们的理解与支持，祝您游戏愉快！,运营,5,https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1593064379767&di=c51c7e5e4cb68be7f81362efe43090de&imgtype=0&src=http%3A%2F%2Fp1.meituan.net%2Favatar%2F1d5b7593a0679ddc03240b9b6d7630fa77146.jpg",
            "整治任务,全力整治县城和蔡家坡地区城镇环境卫生。对各路段、背街小巷和城乡结合部环境卫生以及乱搭乱建、乱停乱放、乱贴乱画、乱发传单广告等 “五乱”现象彻底整治，取缔所有马路市场、占道经营等行为，坚决消除乱倒垃圾、乱泼污水现象。,干部,6,https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1593064379767&di=c51c7e5e4cb68be7f81362efe43090de&imgtype=0&src=http%3A%2F%2Fp1.meituan.net%2Favatar%2F1d5b7593a0679ddc03240b9b6d7630fa77146.jpg"
    })
    @DisplayName("初始化数据")
    void initTest(String title, String msg, String sender, Integer type, String icon) {
        String uuid = UUID.randomUUID().toString();
        Instant time = Instant.now();
        Message message = new Message(uuid, title, msg, sender, type, time, icon);
        //用响应式保存一条新的message并获取返回数据
        Message savedMsg = msgRep.save(message);
        //对比存入后返回的数据与传入参数的诗句title是否一致
        assertEquals(message.getTitle(), savedMsg.getTitle(), "Title一致");
    }

    @DisplayName("获取消息条数")
    @Order(2)
    @Test
    void getMsgQuantity() {
        // 通过响应式方式获取所有消息
        Iterable<Message> msg = msgRep.findAll();
        List<Message> messages= new ArrayList<>();
        msg.iterator().forEachRemaining(messages::add);
        // 通过一般方式获取消息条数
        long msgCount = msgRep.count();
        System.out.println("共有消息，msg.count() ： " +messages.size());
        System.out.println("共有消息，msgCount ： " + msgCount);
        // 如果数据量特别多这个断言是会失败的，因为findAll如果不加分页，则其会默认最多1000条
        assertTrue(
                (messages.size() == msgCount), "消息条数符合预期");
    }


    final String tstId = "test-id";

    @DisplayName("保存新的消息")
    @Order(3)
    @Test
    void saveDoc() {
        Message msg =
                new Message(
                        tstId,
                        "who博士",
                        "和dalex对战了很多年",
                        "时间领主",
                        2,
                        Instant.now(),
                        "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3919334208,37253891&fm=26&gp=0.jpg");
        Message savedMsg = msgRep.save(msg);
        assertAll(
                "msg",
                () -> assertEquals(savedMsg.getTitle(), msg.getTitle()),
                () -> assertEquals(savedMsg.getSender(), msg.getSender()));
    }

    @DisplayName("更新消息")
    @Order(4)
    @Test
    void updateMsg() {
        Optional<Message> optionalMessage = msgRep.findById(tstId);
        Message msg = optionalMessage.get();
        String title = msg.getTitle();
        String newTitle = "胡博士与宋江";
        msg.setTitle(newTitle);
        Message savedMsg = msgRep.save(msg);
        assertAll(
                "msg",
                () -> assertEquals(savedMsg.getTitle(), newTitle),
                () -> assertNotEquals(savedMsg.getTitle(), title),
                () -> assertEquals(savedMsg.getId(), tstId));
    }

    @DisplayName("删除消息")
    @Order(5)
    @Test
    void delMsg() {
        // 看看该消息是否存在
        boolean existMsgBeforeDel = msgRep.existsById(tstId);
        // 删除该消息
        msgRep.deleteById(tstId);
        // 看看该消息是否还存在
        boolean existMsgAfterDel = msgRep.existsById(tstId);
        assertAll(
                "exist msg", () -> assertTrue(existMsgBeforeDel), () -> assertFalse(existMsgAfterDel));
    }

    @DisplayName("获取运营发的消息")
    @Order(7)
    @Test
    void getMesFromSenderOps() {
        String sender = "运营";
        Iterable<Message> msgs =
                msgRep.findBySender(sender, PageRequest.of(0, 3, Sort.by("type").descending()));
        List<Message> messages= new ArrayList<>();
        msgs.iterator().forEachRemaining(messages::add);
        System.out.println("消息共有: " + messages.size());

        messages.forEach(
                        message -> {
                            System.out.println("发送者是: " + message.getSender());
                            System.out.println("消息是: " + message.getMsg());
                            System.out.println("类型是: " + message.getType());
                            System.out.println("tmp类型是: " + message.getType());
                            assertEquals(sender, message.getSender(), "发送者是: " + message.getSender());
                        });
    }
}
