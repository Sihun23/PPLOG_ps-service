package myaong.popolog.psservice.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.psservice.common.exception.ApiCode;
import myaong.popolog.psservice.common.exception.ApiException;
import myaong.popolog.psservice.dto.request.PsRequest;
import myaong.popolog.psservice.dto.response.PsIdResponse;
import myaong.popolog.psservice.dto.response.PsPreResponse;
import myaong.popolog.psservice.dto.response.PsResponse;
import myaong.popolog.psservice.entity.Ps;
import myaong.popolog.psservice.repository.PsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PsService {

    private final PsRepository psRepository;

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

    public PsResponse editPs(Long psId, Long memberId) {
        Ps ps = psRepository.findById(psId)
                .orElseThrow(() -> new ApiException(ApiCode.PS_NOT_FOUND));
        validateMember(ps, memberId);
        return new PsResponse(
                ps.getTitle() + " 첨삭 ver",
                ps.getPosition(),
                ps.getReason(),
                "첨삭 내용"
        );
    }

    // 회원의 권한 확인
    private void validateMember(Ps ps, Long memberId) {
        if (!ps.getMemberId().equals(memberId)) {
            throw new ApiException(ApiCode.PS_NOT_FOUND);
        }

    }
}
