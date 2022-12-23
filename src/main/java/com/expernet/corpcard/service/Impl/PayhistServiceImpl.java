package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.controller.PayhistController;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.PayhistService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * OJT 프로젝트 – 법인카드 내역 결재 시스템
 * 결제내역 Service Implement Class
 *
 * @author (주)엑스퍼넷 설동재
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일 		수정자	수정내용
 * ----------	----	------------------
 * 2022.12.00	설동재	최초 생성
 *
 * </pre>
 * @since 2022.12.00
 */
@Transactional
@Service("PayhistService")
public class PayhistServiceImpl implements PayhistService {
    /**
     * 사용자 정보 Repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 부서 정보 Repository
     */
    @Autowired
    private DeptRepository deptRepository;

    /**
     * 사용내역 제출정보 Repository
     */
    @Autowired
    private UsehistSubmitInfoRepository usehistSubmitInfoRepository;

    /**
     * 법인카드 사용내역 Repository
     */
    @Autowired
    private CardUsehistRepository cardUsehistRepository;

    /**
     * 첨부파일 Repository
     */
    @Autowired
    private AttachmentInfoRepository attachmentInfoRepository;

    /**
     * Upload file path
     */
    @Value("${upload.filePath}")
    private String uploadPath;

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PayhistServiceImpl.class);

    /**
     * 법인카드 사용내역 조회
     *
     * @param paramMap: 제출 seq
     */
    @Override
    public HashMap<String, Object> searchCardUsehistList(HashMap<String, Object> paramMap) {
        HashMap<String, Object> result = new HashMap<>();
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        if (submitInfo != null) {
            List<CardUsehist> list = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq());
            if (list.size() > 0) {
                //제출 상태
                result.put("stateInfo", submitInfo.getStateInfo());
                //사용내역 리스트
                result.put("list", list);
                //분류별 합계
                result.put("sumByClass", cardUsehistRepository.selectSumGroupByClassSeq(submitInfo.getSeq()));
                //총계
                result.put("sum", cardUsehistRepository.selectTotalSumBySubmitSeq(submitInfo.getSeq()));
            }
        }
        return result;
    }

    /**
     * 법인카드 결제 내역 딘일 정보 조회
     *
     * @param paramMap: 결제 내역 seq
     */
    @Override
    public CardUsehist searchCardUsehistInfo(HashMap<String, Object> paramMap) {
        return cardUsehistRepository.findById(Long.valueOf(String.valueOf(paramMap.get("seq"))))
                .orElse(null);
    }

    /**
     * 법인카드 사용내역 저장
     *
     * @param cardUsehist : 결제 내역 정보
     * @param userId      : 사용자 ID
     */
    @Override
    public Object saveCardUsehistInfo(CardUsehist cardUsehist, String userId) {
        //1.제출 정보 존재여부 확인
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String wrtYm = format.format(cardUsehist.getUseDate());
        UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(userId, wrtYm);

        //2.제출 정보 없을 시 생성
        if (submitInfo == null) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("WRITER_ID", userId);
            paramMap.put("WRT_YM", wrtYm);
            submitInfo = saveSubmitInfo(paramMap);
        }

        //3.결제내역 저장
        cardUsehist.setUsehistSubmitInfo(submitInfo);
        return cardUsehistRepository.save(cardUsehist);
    }

    /**
     * 법인카드 사용내역 삭제
     *
     * @param paramMap: 제출 정보 & 삭제할 seq list
     */
    @Override
    public long deleteCardUsehistInfo(HashMap<String, Object> paramMap) {
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        String seqJSON = paramMap.get("SEQ_LIST").toString();
        long result = 0;
        List<Long> list;
        try {
            list = new ObjectMapper().readValue(seqJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return 0;
        }
        long preCnt = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq()).size();
        cardUsehistRepository.deleteAllById(list);
        long curCnt = cardUsehistRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq()).size();

        if (list.size() == preCnt - curCnt) {
            result = list.size();
        }
        return result;
    }

    /**
     * 법인카드 사용내역 수정
     *
     * @param cardUsehist: 사용내역
     */
    @Override
    public Object updateCardUsehistInfo(CardUsehist cardUsehist) {
        return cardUsehistRepository.save(cardUsehist);
    }

    /**
     * 법인카드 결제 내역 제출
     *
     * @param paramMap : 제출 정보
     */
    @Override
    public Object updateStateSeq(HashMap<String, Object> paramMap) {
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        submitInfo.setStateInfo(StateInfo.builder().seq(2).build());
        return usehistSubmitInfoRepository.save(submitInfo);
    }

    /**
     * 첨부파일 조회
     *
     * @param paramMap : 제출 정보
     */
    @Override
    public List<AttachmentInfo> searchAtchList(HashMap<String, Object> paramMap) {
        List<AttachmentInfo> result;
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);
        if(submitInfo != null){
            result = attachmentInfoRepository.findAllByUsehistSubmitInfo_Seq(submitInfo.getSeq());
        }else{
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 첨부파일 업로드
     *
     * @param paramMap : 제출 정보
     * @param fileList : 업로드된 파일 list
     */
    @Override
    public List<AttachmentInfo> uploadAtch(HashMap<String, Object> paramMap, List<MultipartFile> fileList) {
        List<AttachmentInfo> result = new ArrayList<>();

        //1.제출 정보 조회
        UsehistSubmitInfo submitInfo = searchSubmitInfo(paramMap);

        //2.제출 정보 없을 시 생성
        if (submitInfo == null) {
            submitInfo = saveSubmitInfo(paramMap);
        }

        //3.첨부파일 저장
        //1)제출 번호별 디렉토리 생성
        String path = uploadPath + File.separator + submitInfo.getSeq();
        File directory = new File(path);
        if (directory.mkdirs()) {
            logger.info("제출번호 " + submitInfo.getSeq() + "번의 디렉토리 생성 성공");
        }
        for (MultipartFile file : fileList) {
            //1)파일 정보 db 저장
            SimpleDateFormat sdfCurrent = new SimpleDateFormat("yyyyMMddhhmmssSSS", Locale.KOREA);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String filePropNm = sdfCurrent.format(ts.getTime());
            String originalFilename = file.getOriginalFilename();
            long fileSize = file.getSize();

            AttachmentInfo atchInfo = AttachmentInfo.builder()
                    .usehistSubmitInfo(submitInfo)
                    .fileNm(FilenameUtils.getName(originalFilename))
                    .fileExtNm(FilenameUtils.getExtension(originalFilename))
                    .filePropNm(filePropNm)
                    .filePath(uploadPath + File.separator + submitInfo.getSeq())
                    .uploadFn(fileSize)
                    .build();

            result.add(attachmentInfoRepository.save(atchInfo));

            //2)파일 저장
            try {
                FileOutputStream fos = new FileOutputStream(path + File.separator + filePropNm);
                fos.write(file.getBytes());
                fos.close();
            } catch (IOException e) {
                attachmentInfoRepository.deleteById(result.get(result.size() - 1).getSeq());
                result.remove(result.size() - 1);
                logger.error(originalFilename + " 파일 업로드 실패");
            }
        }
        return result;
    }

    /**
     * 업로드된 파일 삭제
     *
     * @param paramMap: 첨부파일 seq list
     */
    @Override
    public long deleteAtch(HashMap<String, Object> paramMap) {
        //1.seq 리스트 데이터 설정
        String seqJSON = paramMap.get("SEQ_LIST").toString();
        List<Long> list;
        long result = 0;
        try {
            list = new ObjectMapper().readValue(seqJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return result;
        }
        //2.파일 및 첨부파일 정보 삭제
        for (long seq : list) {
            AttachmentInfo fileInfo = attachmentInfoRepository.findById(seq).orElse(null);
            if (fileInfo != null) {
                File file = new File(fileInfo.getFilePath() + File.separator + fileInfo.getFilePropNm());
                if (file.delete()) {
                    attachmentInfoRepository.deleteById(seq);
                    result++;
                }
            }
        }
        if (list.size() != result) {
            result = 0;
        }
        return result;
    }

    /**
     * 첨부파일 다운로드
     * @param paramMap  : 첨부파일 seq list
     * @param response  : HttpServletResponse
     */
    @Override
    public void downloadAtch(HashMap<String, Object> paramMap, HttpServletResponse response) throws IOException {
        String downFileName = uploadPath+File.separator+paramMap.get("seq").toString()+File.separator
                +paramMap.get("filePropNm").toString();
        String orgFileName = paramMap.get("fileNm").toString();

        File file = new File(downFileName);

        if (!file.exists()) {
            throw new FileNotFoundException(downFileName);
        }

        if (!file.isFile()) {
            throw new FileNotFoundException(downFileName);
        }

        int fSize = (int) file.length();
        if (fSize > 0) {
            byte[] buffer = new byte[2048];

            String fileName = URLEncoder.encode(orgFileName, StandardCharsets.UTF_8);

            Path fileFullPath = Paths.get(downFileName);
            response.setContentType(Files.probeContentType(fileFullPath));
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            try (BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file)); BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream())) {
                int read = 0;

                while ((read = fin.read(buffer)) != -1) {
                    outs.write(buffer, 0, read);
                }

                outs.flush();
            }
        }
    }

    /**
     * 제출 내역 조회
     *
     * @param paramMap: 사용자 정보 및 작성년월
     */
    private UsehistSubmitInfo searchSubmitInfo(HashMap<String, Object> paramMap) {
        String writerId = paramMap.get("WRITER_ID").toString();
        String wrtYm = paramMap.get("WRT_YM").toString();
        return usehistSubmitInfoRepository.findByWriterIdAndWrtYm(writerId, wrtYm);
    }

    /**
     * 제출 내역 저장
     *
     * @param paramMap: 사용자 정보 및 작성년월
     */
    private UsehistSubmitInfo saveSubmitInfo(HashMap<String, Object> paramMap) {
        User userInfo = userRepository.findByUserId(paramMap.get("WRITER_ID").toString());
        Dept deptInfo = deptRepository.findByDeptCd(userInfo.getDeptCd());
        StateInfo stateInfo = StateInfo.builder().seq(1).build();

        UsehistSubmitInfo histInfo = UsehistSubmitInfo.builder()
                .stateInfo(stateInfo)
                .writerId(userInfo.getUserId())
                .writerDept(deptInfo.getDeptNm())
                .writerOfcds(userInfo.getOfcds())
                .writerNm(userInfo.getUserNm())
                .wrtYm(paramMap.get("WRT_YM").toString())
                .build();
        return usehistSubmitInfoRepository.save(histInfo);
    }
}
