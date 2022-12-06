package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.entity.UserAddInfo;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UserAddInfoRepository;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.util.SHA512Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(value = false)
public class SchedulerServiceImplTest {
    private final Map<String, String> resultMsg = new HashMap<>();
    private String type;
    private String filePath;
    private boolean hasNoError;
    private List fittedDataList;
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImplTest.class);

    @Value("${userInfo.filePath}")
    private String userFilePath;

    @Value("${deptInfo.filePath}")
    private String deptFilePath;

    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAddInfoRepository userAddInfoRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testInsertSpeed() throws Exception {
        //given
        type = "user";
        filePath = userFilePath;
        hasNoError = true;

        //when
        getFile();
        fitFileData();
        deleteData();

        long startTime = System.currentTimeMillis();
        logger.info("### jpa insert 시작시간: " + formatTime(startTime));

        userRepository.saveAllAndFlush(fittedDataList);

        long endTime = System.currentTimeMillis();
        logger.info("### jpa insert 저장 시간: " + (endTime - startTime) / 1000.0);
        logger.info("### jpa insert 함수 종료 시간: " + formatTime(endTime));

        deleteData();

        startTime = System.currentTimeMillis();
        logger.info("### jpa insert 시작시간: " + formatTime(startTime));

        endTime = System.currentTimeMillis();
        logger.info("### jpa insert 저장 시간: " + (endTime - startTime) / 1000.0);
        logger.info("### jpa insert 함수 종료 시간: " + formatTime(endTime));

        deleteData();

        startTime = System.currentTimeMillis();
        logger.info("### jdbcTemplate 시작시간: " + formatTime(startTime));

        jdbcTemplate.batchUpdate(
                "INSERT INTO TB_USER (" +
                        "seq, user_id, ofcds, user_nm, dept_cd, chief_yn)" +
                        "VALUES (" +
                        "?, ?, ?, ?, ?, ?" +
                        ")",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        User userInfo = (User) fittedDataList.get(i);
                        ps.setLong(1, userInfo.getSeq());
//                        ps.setString(2, userInfo.getUserId());
                        ps.setString(3, userInfo.getOfcds());
                        ps.setString(4, userInfo.getUserNm());
                        ps.setString(5, userInfo.getDeptCd());
                        ps.setString(6, String.valueOf(userInfo.getChiefYn()));
                    }

                    @Override
                    public int getBatchSize() {
                        return fittedDataList.size();
                    }
                }
        );

        endTime = System.currentTimeMillis();
        logger.info("### jdbcTemplate 저장 시간: " + (endTime - startTime) / 1000.0);
        logger.info("### jdbcTemplate 함수 종료 시간: " + formatTime(endTime));


        //then

    }

    public String formatTime(long time) throws Exception {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time);
        return (ca.get(Calendar.HOUR_OF_DAY) + "시 " + ca.get(Calendar.MINUTE) + "분 " + ca.get(Calendar.SECOND) + "." + ca.get(Calendar.MILLISECOND) + "초");
    }

    @Test
    public void syncData() {
        //given
        type = "user";
        filePath = userFilePath;
        hasNoError = true;

        //when
        getFile();
        fitFileData();
        deleteData();
        insertData();
        chkAndInsertUserAddInfo();

        //then
        assertNotEquals(resultMsg.get("CODE"), "ERR");
    }

    @Test
    public void getFile() {
        //given
        File path = new File(filePath);

        //when
        if (path.exists()) {
            //1. 해당 폴더의 파일 리스트 가져오기.
            File[] FileList = path.listFiles();
            if (FileList != null) {
                //2. 파일 리스트 중 최신 파일 이름 가져오기.
                filePath += File.separator + sortFileList(FileList)[0].getName();
            } else {
                troubleShoot("noFile");
            }
        } else {
            troubleShoot("noPath");
        }

        //then
        assertNotNull(path);
    }

    public void fitFileData() {
        //given
        Map<String, Object> parsedMap = null;

        //when
        if (hasNoError) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Object fileData = new JSONParser().parse(new FileReader(filePath));
                parsedMap = objectMapper.convertValue(fileData, Map.class);

                List<HashMap<String, Object>> objects = (List<HashMap<String, Object>>) parsedMap.get("objects");
                if (type.equals("user")) {
                    pretreatUserData(objects);
                } else {
                    pretreatDeptData(objects);
                }
            } catch (IOException ex) {
                troubleShoot("noFile");
            } catch (ParseException e) {
                troubleShoot("parseErr");
            }
        }
    }

    public void pretreatUserData(List<HashMap<String, Object>> list) {
        //given
        List<User> userList = new ArrayList<>();
        long userSeq = 1;

        for (HashMap<String, Object> deptMap : list) {
            List<HashMap<String, Object>> teammateList = (List<HashMap<String, Object>>) deptMap.get("children");
            for (int j = 0; j < teammateList.size(); j++) {
                String[] label = teammateList.get(j).get("label").toString().split(" ");
                User userInfo = User.builder()
                        .seq(userSeq++)
                        .userId(teammateList.get(j).get("value").toString())
                        .deptCd(deptMap.get("value").toString())
                        .userNm(label[0])
                        .ofcds(label[1])
                        .chiefYn((j == 0) ? 'Y' : 'N')
                        .build();

                userList.add(userInfo);
            }
        }

        fittedDataList = userList;

        //then
        assertNotEquals(fittedDataList.size(), 0);
    }

    protected void pretreatDeptData(List<HashMap<String, Object>> list) {
        List<Dept> deptList = new ArrayList<>();

        for (Map<String, Object> map : list) {
            Dept deptInfo = Dept.builder()
                    .deptNm(map.get("full_name").toString())
                    .deptCd(map.get("id").toString())
                    .upperDeptCd((map.get("upper") == null) ? null : map.get("upper").toString())
                    .chiefTitle(map.get("chief_title").toString())
                    .build();
            deptList.add(deptInfo);
        }

        fittedDataList = deptList;
    }

    @Test
    public void deleteData() {
        long deletedCnt = -1;

        //1. delete 수행
        if (type.equals("user")) {
            userRepository.deleteAllInBatch();
            deletedCnt = userRepository.count();
        } else if (type.equals("dept")) {
            deptRepository.deleteAllInBatch();
            deletedCnt = deptRepository.count();
        }

        //2. delete 성공 여부 확인
        if (deletedCnt == 0) {
            writeMsg("deleted");
        } else {
            troubleShoot("delErr");
        }

        assertNotEquals(deletedCnt, -1);
    }

    @Test
    public void insertData() {
        //given
        long insertedCnt = -1;

        //when
        if (type.equals("user")) {
            userRepository.saveAllAndFlush(fittedDataList);
            insertedCnt = userRepository.count();
        } else if (type.equals("dept")) {
            deptRepository.saveAllAndFlush(fittedDataList);
            insertedCnt = deptRepository.count();
        }

        //then
        chkInsertedAndWriteMsg(insertedCnt);
        assertNotEquals(insertedCnt, -1);
    }

    protected File[] sortFileList(File[] files) {
        Arrays.sort(files,
                (Comparator<Object>) (object1, object2) -> {
                    String s1 = ((File) object1).lastModified() + "";
                    String s2 = ((File) object2).lastModified() + "";
                    return s1.compareTo(s2);
                });
        return files;
    }

    public void chkInsertedAndWriteMsg(long num) {
        if (num > 0) writeMsg("inserted");
        else troubleShoot("insErr");
    }

    @Test
    public void chkAndInsertUserAddInfo() {
        //given
        hasNoError = true;
        //when
        if (hasNoError) {
            List<User> toSaveUserAddList = userRepository.findAllByUserIdNotInAddInfoQuery();
            insertUserAddInfo(toSaveUserAddList);
        }

        //then

    }

    public void insertUserAddInfo(List<User> toSaveUserAddList) {
        List<UserAddInfo> userInfoList = new ArrayList<>();

        if (toSaveUserAddList != null) {
            try{
                for (User user : toSaveUserAddList) {
                    String password = SHA512Util.SHA512Encode(user.getUserNm());

                    UserAddInfo userAddInfo = UserAddInfo.builder()
                            .userPasswd(password)
                            .managerYn("N")
                            .user(user)
                            .build();

                    userInfoList.add(userAddInfo);
                }
                userAddInfoRepository.saveAll(userInfoList);
            }catch (Exception e){
                e.printStackTrace();
                troubleShoot("userAddErr");
            }
        }
    }

    public void troubleShoot(String message) {
        hasNoError = false;
        writeMsg(message);
    }

    public void writeMsg(String msg) {
        switch (msg) {
            case "deleted" -> {
                resultMsg.put("CODE", "SUCCESS");
                resultMsg.put("MSG", type + " data delete completed successfully");
            }
            case "inserted" -> {
                resultMsg.put("CODE", "SUCCESS");
                resultMsg.put("MSG", type + " data insert completed successfully");
            }
            case "delErr" -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + " data delete failed");
            }
            case "insErr" -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + " data insert failed");
            }
            case "noFile" -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + " file not found");
            }
            case "parseErr" -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + " data parsing failed");
            }
        }
    }
}