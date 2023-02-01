package com.expernet.corpcard.service.Impl;


import com.expernet.corpcard.dto.payhist.AtchListDTO;
import com.expernet.corpcard.dto.payhist.PayhistDTO;
import com.expernet.corpcard.dto.payhist.ListDTO;
import com.expernet.corpcard.entity.*;
import com.expernet.corpcard.repository.*;
import com.expernet.corpcard.service.PayhistService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * @param params: 제출 정보
     */
    @Override
    public HashMap<String, Object> getList(ListDTO.Request params) {
        HashMap<String, Object> result = new HashMap<>();
        String writerId = params.getUserId();
        String wrtYm = params.getWrtYm();
        String classCd = params.getClassCd();

        UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(writerId, wrtYm);

        if (submitInfo != null) {
            long submitSeq = submitInfo.getSeq();
            Sort sort = Sort.by(Sort.Direction.ASC, "useDate");
            List<CardUsehist> list = cardUsehistRepository.findAllBySubmitSeqAndClassCd(submitSeq, classCd, sort);
            if (list.size() > 0) {
                //제출 내역
                result.put("submitInfo", submitInfo);
                //사용 내역 리스트
                result.put("list", list);
                //분류별 합계
                result.put("sumByClass", cardUsehistRepository.selectSumGroupByClassSeq(submitSeq, classCd));
                //총계
                result.put("sum", cardUsehistRepository.selectTotalSumBySubmitSeq(submitSeq, classCd));
            }
        }
        return result;
    }

    /**
     * 법인카드 사용내역 저장 or 수정
     *
     * @param cardUsehist : 결제 내역 정보
     */
    @Override
    public Object saveInfo(CardUsehist cardUsehist) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String userId = userDetails.getUsername();

        //1.제출 정보 존재여부 확인
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String wrtYm = format.format(cardUsehist.getUseDate());
        UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(userId, wrtYm);

        //2.제출 정보 없을 시 생성
        if (submitInfo == null) {
            PayhistDTO.SearchListReq searchListReq = PayhistDTO.SearchListReq.builder()
                    .userId(userId)
                    .wrtYm(wrtYm)
                    .build();

            User userInfo = userRepository.findByUserId(searchListReq.getUserId());
            Dept deptInfo = userInfo.getDept();

            UsehistSubmitInfo histInfo = UsehistSubmitInfo.builder()
                    .stateInfo(StateInfo.builder().seq(1).build())
                    .writerId(userInfo.getUserId())
                    .writerDept(deptInfo.getUpper().getDeptNm())
                    .writerTeam(deptInfo.getDeptNm())
                    .writerOfcds(userInfo.getOfcds())
                    .writerNm(userInfo.getUserNm())
                    .wrtYm(searchListReq.getWrtYm())
                    .build();

            submitInfo = usehistSubmitInfoRepository.save(histInfo);
        }

        //3.결제내역 저장
        cardUsehist.setUsehistSubmitInfo(submitInfo);
        return cardUsehistRepository.save(cardUsehist);
    }

    /**
     * 법인카드 사용내역 삭제
     *
     * @param seqList: 제출 정보 & 삭제할 seq list
     */
    @Override
    public long deleteList(List<Long> seqList) {
        cardUsehistRepository.deleteAllById(seqList);
        return seqList.size();
    }

    /**
     * 첨부파일 조회
     *
     * @param params : 제출 정보
     */
    @Override
    public List<AttachmentInfo> getAtchList(AtchListDTO.Request params) {
        List<AttachmentInfo> result;
        long seq = -1;
        if (params.getSeq() != 0) {
            seq = params.getSeq();
        } else {
            UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(
                    params.getWriterId(), params.getWrtYm());
            if (submitInfo != null) {
                seq = submitInfo.getSeq();
            }
        }
        if (seq != -1) {
            result = attachmentInfoRepository.findAllByUsehistSubmitInfo_Seq(seq);
        } else {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 첨부파일 업로드
     *
     * @param params : 제출 정보
     * @param fileList : 업로드된 파일 list
     */
    @Override
    public List<AttachmentInfo> uploadAtch(HashMap<String, String> params, List<MultipartFile> fileList) {
        List<AttachmentInfo> result = new ArrayList<>();

        //1.제출 정보 조회
        UsehistSubmitInfo submitInfo = usehistSubmitInfoRepository.findByWriterIdAndWrtYm(
                params.get("writerId"), params.get("wrtYm"));

        //2.제출 정보 없을 시 생성
        if (submitInfo == null) {
            User userInfo = userRepository.findByUserId(params.get("writerId"));
            Dept deptInfo = deptRepository.findByDeptCd(userInfo.getDept().getDeptCd());
            StateInfo stateInfo = StateInfo.builder().seq(1).build();

            UsehistSubmitInfo histInfo = UsehistSubmitInfo.builder()
                    .stateInfo(stateInfo)
                    .writerId(userInfo.getUserId())
                    .writerDept(deptInfo.getUpper().getDeptNm())
                    .writerTeam(deptInfo.getDeptNm())
                    .writerOfcds(userInfo.getOfcds())
                    .writerNm(userInfo.getUserNm())
                    .wrtYm(params.get("wrtYm"))
                    .build();
            submitInfo = usehistSubmitInfoRepository.save(histInfo);
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
            String originalFilename = file.getOriginalFilename();
            long fileSize = file.getSize();

            AttachmentInfo atchInfo = AttachmentInfo.builder()
                    .usehistSubmitInfo(submitInfo)
                    .fileNm(FilenameUtils.getName(originalFilename))
                    .fileExtNm(FilenameUtils.getExtension(originalFilename))
                    .filePath(uploadPath + File.separator + submitInfo.getSeq())
                    .uploadFn(fileSize)
                    .build();
            AttachmentInfo savedAtchInfo = attachmentInfoRepository.save(atchInfo);
            result.add(savedAtchInfo);

            //2)파일 저장
            try {
                FileOutputStream fos = new FileOutputStream(path + File.separator + savedAtchInfo.getSeq());
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
     * @param seqList: 첨부파일 seq list
     */
    @Override
    public long deleteAtch(List<Long> seqList) {
        long result = 0;

        for (long seq : seqList) {
            AttachmentInfo fileInfo = attachmentInfoRepository.findById(seq).orElse(null);
            if (fileInfo != null) {
                File file = new File(fileInfo.getFilePath() + File.separator + fileInfo.getSeq());
                if (file.delete()) {
                    attachmentInfoRepository.deleteById(seq);
                    result++;
                }
            }
        }

        return result;
    }

    /**
     * 첨부파일 다운로드
     *
     * @param paramMap : 첨부파일 seq list
     * @param response : HttpServletResponse
     */
    @Override
    public void downloadAtch(HashMap<String, Object> paramMap, HttpServletResponse response) throws IOException {
        String downFileName = uploadPath + File.separator + paramMap.get("seq").toString() + File.separator
                + paramMap.get("fileSeq").toString();
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
}
