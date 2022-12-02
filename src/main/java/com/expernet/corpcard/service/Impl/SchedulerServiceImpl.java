package com.expernet.corpcard.service.Impl;

import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.User;
import com.expernet.corpcard.repository.DeptRepository;
import com.expernet.corpcard.repository.UserRepository;
import com.expernet.corpcard.service.SchedulerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * User Scheduler Service Implement Class
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
@Transactional
@Service("SchedulerService")
public class SchedulerServiceImpl implements SchedulerService {
    private final Map<String, String> resultMsg = new HashMap<>();
    private boolean hasNoError = true;
    private String type;
    private String filePath;
    private List fittedDataList;

    /**
     * 부서 Repository
     */
    @Autowired
    private DeptRepository deptRepository;

    /**
     * 사용자 Repository
     */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * user txt file path
     */
    @Value("${userInfo.filePath}")
    private String userFilePath;

    /**
     * dept txt file path
     */
    @Value("${deptInfo.filePath}")
    private String deptFilePath;

    /**
     * 데이터 동기화
     *
     * @param type : user / dept
     */
    @Override
    public Map<String, String> syncData(String type) throws Exception {
        this.type = type;
        filePath = (type.equals("user")) ? userFilePath : deptFilePath;
        syncData();
        return resultMsg;
    }

    private void syncData() throws Exception {
        getFile();
        fitFileData();
        deleteData();
        insertData();
    }

    /**
     * 파일 가져오기
     */
    private void getFile() {
        File path = new File(filePath);

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
    }

    /**
     * 생성날짜별 파일 정렬(내림차순)
     *
     * @param files : File List
     * @return File[]
     */
    private File[] sortFileList(File[] files) {
        Arrays.sort(files,
                (Comparator<Object>) (object1, object2) -> {
                    String s1 = ((File) object1).lastModified() + "";
                    String s2 = ((File) object2).lastModified() + "";
                    return s1.compareTo(s2);
                });
        return files;
    }

    /**
     * 파일 규격화
     */
    private void fitFileData() {
        if (hasNoError) {
            Map<String, Object> parsedMap = parseData();
            pretreatData(parsedMap);
        }
    }

    /**
     * 데이터 파싱
     */
    private Map<String, Object> parseData() {
        Map<String, Object> parsedMap = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object fileData = new JSONParser().parse(new FileReader(filePath));
            parsedMap = objectMapper.convertValue(fileData, Map.class);
        } catch (IOException ex) {
            troubleShoot("noFile");
        } catch (ParseException e) {
            troubleShoot("parseErr");
        }

        return parsedMap;
    }

    /**
     * 데이터 전처리
     *
     * @param parsedMap : 파싱된 데이터
     */
    private void pretreatData(Map<String, Object> parsedMap) {
        if (hasNoError) {
            List<HashMap<String, Object>> objects = (List<HashMap<String, Object>>) parsedMap.get("objects");
            if (type.equals("user")) {
                pretreatUserData(objects);
            } else {
                pretreatDeptData(objects);
            }
        }
    }

    /**
     * 사용자 데이터 전처리
     *
     * @param list : dept list
     */
    private void pretreatUserData(List<HashMap<String, Object>> list) {
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
    }

    /**
     * 부서 데이터 전처리
     *
     * @param list : dept list
     */
    private void pretreatDeptData(List<HashMap<String, Object>> list) {
        List<Dept> deptList = new ArrayList<>();
        long deptSeq = 1;

        for (Map<String, Object> map : list) {
            Dept deptInfo = Dept.builder()
                    .seq(deptSeq++)
                    .deptNm(map.get("full_name").toString())
                    .deptCd(map.get("id").toString())
                    .upperDeptCd((map.get("upper") == null) ? null : map.get("upper").toString())
                    .chiefTitle(map.get("chief_title").toString())
                    .build();
            deptList.add(deptInfo);
        }

        fittedDataList = deptList;
    }

    /**
     * 데이터 삭제
     */
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
    }

    /**
     * 데이터 삽입
     */
    public void insertData() {

        long insertedCnt = -1;
        if (type.equals("user")) {
            userRepository.saveAll(fittedDataList);
            insertedCnt = userRepository.count();
        } else if (type.equals("dept")) {
            deptRepository.saveAll(fittedDataList);
            insertedCnt = deptRepository.count();
        }

        if (insertedCnt > 0) {
            writeMsg("inserted");
        } else {
            troubleShoot("insErr");
        }
    }

    private void troubleShoot(String message) {
        hasNoError = false;
        writeMsg(message);
    }

    private void writeMsg(String msg) {
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
            case "noPath" -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + " invalid file path");
            }
            case "parseErr" -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + " data parsing failed");
            }
            default -> {
                resultMsg.put("CODE", "ERR");
                resultMsg.put("MSG", type + "unknown error");
            }
        }
    }
}
