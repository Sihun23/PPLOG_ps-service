package myaong.popolog.psservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myaong.popolog.psservice.client.OpenAIClient;
import myaong.popolog.psservice.common.exception.ApiCode;
import myaong.popolog.psservice.common.exception.ApiException;
import myaong.popolog.psservice.dto.request.PsRequest;
import myaong.popolog.psservice.dto.response.PsEditResponse;
import myaong.popolog.psservice.dto.response.PsIdResponse;
import myaong.popolog.psservice.dto.response.PsPreResponse;
import myaong.popolog.psservice.dto.response.PsResponse;
import myaong.popolog.psservice.entity.Ps;
import myaong.popolog.psservice.repository.PsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PsService {

    private final PsRepository psRepository;
    private final OpenAIClient openAIClient;

    // 특정 회원의 자기소개서 목록 조회
    @Transactional(readOnly = true)
    public List<PsPreResponse> getPsList(Long memberId) {
        List<PsPreResponse> res = new ArrayList<>();
        psRepository.findByMemberId(memberId) // memberId로 조회
                .forEach(ps ->
                        res.add(PsPreResponse.builder()
                                .psId(ps.getId())
                                .title(ps.getTitle())
                                .position(ps.getPosition())
                                .timestamp(ps.getCreatedAt().toString())
                                .content(ps.getContent())
                                .build())
                );
        return res;
    }

    @Transactional(readOnly = true)
    public PsResponse getPs(Long psId, Long memberId) {
        Ps ps = psRepository.findById(psId)
                .orElseThrow(() -> new ApiException(ApiCode.PS_NOT_FOUND));
        validateMember(ps, memberId);
        return new PsResponse(ps.getTitle(), ps.getPosition(), ps.getReason(), ps.getContent());
    }

    public PsIdResponse createPs(PsRequest psRequest, Long memberId) {
        Ps ps = Ps.builder()
                .memberId(memberId)
                .title(psRequest.getTitle())
                .position(psRequest.getPosition())
                .reason(psRequest.getReason())
                .content(psRequest.getContent())
                .build();

        psRepository.save(ps);
        return new PsIdResponse(ps.getId());
    }

    public void updatePs(Long psId, PsRequest psRequest, Long memberId) {
        Ps ps = psRepository.findById(psId)
                .orElseThrow(() -> new ApiException(ApiCode.PS_NOT_FOUND));
        validateMember(ps, memberId);
        ps.update(psRequest.getTitle(), psRequest.getPosition(), psRequest.getReason(), psRequest.getContent());
        psRepository.save(ps);
    }

    public void deletePs(Long psId, Long memberId) {
        Ps ps = psRepository.findById(psId)
                .orElseThrow(() -> new ApiException(ApiCode.PS_NOT_FOUND));
        validateMember(ps, memberId);
        psRepository.delete(ps);
    }


    public PsEditResponse getEditedPs(Long psId, Long memberId) {
        // 1. DB에서 원본 자소서 가져오기
        Ps ps = psRepository.findById(psId)
                .orElseThrow(() -> new ApiException(ApiCode.PS_NOT_FOUND));
        String originalContent = ps.getContent();

        // 2. OpenAI API 호출로 수정된 내용 가져오기
        log.info("AI 서비스에 첨삭 요청: {}", originalContent);
        String prompt = "아래의 내용을 면접관이 보기 좋게 수정해 주세요:\n" + originalContent;
        String editedContent = openAIClient.callChatGPT("user", prompt);

        // 3. 수정된 내용과 원본을 비교하여 강조된 결과 생성
        String highlightedContent = highlightChanges(originalContent, editedContent);

        log.info("AI 서비스 응답: {}", editedContent);
        log.info("강조된 변경 내용: {}", highlightedContent);

        // 4. 응답 객체 반환
        return PsEditResponse.builder()
                .psId(ps.getId())
                .originalContent(originalContent)
                .highlightedContent(highlightedContent)
                .build();
    }

    private String highlightChanges(String original, String edited) {
        String[] originalWords = original.split("\\s+");
        String[] editedWords = edited.split("\\s+");

        StringBuilder highlighted = new StringBuilder();
        int[][] lcsMatrix = buildLCSMatrix(originalWords, editedWords);

        int i = originalWords.length;
        int j = editedWords.length;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && originalWords[i - 1].equals(editedWords[j - 1])) {
                // 같은 단어는 그대로 추가
                highlighted.insert(0, editedWords[j - 1] + " ");
                i--;
                j--;
            } else if (j > 0 && (i == 0 || lcsMatrix[i][j - 1] >= lcsMatrix[i - 1][j])) {
                // 수정된 단어는 **로 감싸서 추가
                highlighted.insert(0, "**" + editedWords[j - 1] + "** ");
                j--;
            } else {
                // 삭제된 단어는 생략 (표시하지 않음)
                i--;
            }
        }

        return highlighted.toString().trim();
    }

    // LCS Matrix 생성
    private int[][] buildLCSMatrix(String[] original, String[] edited) {
        int m = original.length;
        int n = edited.length;
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (original[i - 1].equals(edited[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp;
    }


    // 회원의 권한 확인
    private void validateMember(Ps ps, Long memberId) {
        if (!ps.getMemberId().equals(memberId)) {
            throw new ApiException(ApiCode.PS_NOT_FOUND);
        }
    }
}
