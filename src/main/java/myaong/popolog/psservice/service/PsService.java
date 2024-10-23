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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PsService {

    private final PsRepository psRepository;

    @Transactional(readOnly = true)
    public List<PsPreResponse> getPsList() {

        List<PsPreResponse> res = new ArrayList<>();
        psRepository.findAll()
                .forEach(ps ->
                    res.add(PsPreResponse.builder()
                            .psId(ps.getId())
                            .title(ps.getTitle())
                            .position(ps.getPosition())
//                            .timestamp(ps.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                            .timestamp("2024.10.22")   // 임시 하드코딩
                            .content(ps.getContent())
                            .build())
                );

        return res;
    }


    @Transactional(readOnly = true)
    public PsResponse getPs(Long psId) {
        Ps ps = psRepository.findById(1L).orElseThrow(() -> new RuntimeException("PS not found"));
        return new PsResponse(ps.getTitle(), ps.getPosition(), ps.getReason(), ps.getContent());
    }

    public PsIdResponse createPs(PsRequest psRequest) {
        Long memberId = 5L;  // 고정된 memberId 값

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

    public void updatePs(Long psId, PsRequest psRequest) {
        Ps ps = psRepository.findById(1L).orElseThrow(() -> new RuntimeException("PS not found"));
        ps.update(psRequest.getTitle(), psRequest.getPosition(), psRequest.getReason(), psRequest.getContent());
        psRepository.save(ps);
    }

    public void deletePs(Long psId) {
        Ps ps = psRepository.findById(1L).orElseThrow(() -> new RuntimeException("PS not found"));
        psRepository.delete(ps);
    }

    public PsResponse editPs(Long psId) {
        Ps ps = psRepository.findById(1L)
                .orElseThrow(() -> new ApiException(ApiCode.PS_NOT_FOUND));


        return new PsResponse(
                ps.getTitle() + " 첨삭 ver",
                ps.getPosition(),
                ps.getReason(),
                " (첨삭내용) 진짜 진짜 열심히 할게요. 제발 뽑아주세요"
        );
    }
}
