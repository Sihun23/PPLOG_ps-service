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
                "저는 웹 개발에 대한 열정과 **뛰어난** 사용자 경험을 제공하는 목표로 프론트엔드 개발자로서 커리어를 쌓아왔습니다. 웹사이트가 단순한 정보 전달을 넘어 사용자와 소통하고 **브랜드 가치를 전달하는** 중요한 매체로 자리잡는 과정에서, 프론트엔드 개발자의 역할이 매우 중요하다는 점을 깨달았습니다.\n" +
                        "그동안 React와 Vue.js**와 같은 프레임워크를 활용하여** 인터랙티브하고 반응형 웹사이트를 구축하면서, 사용자 경험을 극대화할 수 있다는 사실에 **큰 매력을 느꼈습니다.** 이러한 경험을 통해 웹 개발의 기술적 도전과 사용자 중심의 설계를 결합하여 더욱 향상된 웹 경험을 제공하는 데 기여하고자 합니다.\n" +
                        "귀사의 프로젝트는 사용자의 필요와 최신 트렌드를 반영하며, **혁신적인 기술을 통해** 더 나은 웹 경험을 제공하고자 하는 비전을 갖고 있음을 알게 되었습니다. 이러한 비전에 공감하며, **저의 경험과 기술을 통해** 귀사에 기여하고 싶어 지원하게 되었습니다."
        );
    }
}
