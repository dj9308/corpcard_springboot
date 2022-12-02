package com.expernet.corpcard;

import com.expernet.corpcard.service.SchedulerService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * Scheduler Class
 *
 * @author (주)엑스퍼넷 설동재
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.11.21	설동재	최초 생성
 *
 * </pre>
 * @since 2022.11.21
 */
@Component
public class Scheduler extends BaseController {

    final String TYPE_USER = "user";
    final String TYPE_DEPT = "dept";


    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    /**
     * Scheduler Service
     */
    @Resource(name = "SchedulerService")
    private SchedulerService schedulerService;

    /**
     * 매일 02:00 사용자 데이터 동기화
     */
    @Scheduled(cron = "${userInfo.sync.dateTime}")
    public void syncUserInfo() throws Exception {
        Map<String, String> result = schedulerService.syncData(TYPE_USER);
        makeLog(result);
    }

    /**
     * 매일 03:00 부서 데이터 동기화
     */
    @Scheduled(cron = "${deptInfo.sync.dateTime}")
    public void syncDeptInfo() throws Exception {
        Map<String, String> result = schedulerService.syncData(TYPE_DEPT);
        makeLog(result);
    }

    private void makeLog(Map<String, String> resultMap) {
        if (resultMap.get("CODE").equals("ERR")) {
            logger.error(resultMap.get("MSG"));
        } else if (resultMap.get("CODE").equals("SUCCESS")) {
            logger.info(resultMap.get("MSG"));
        }
    }
}
