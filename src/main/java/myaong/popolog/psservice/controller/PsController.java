package myaong.popolog.psservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaong.popolog.psservice.common.exception.ApiResponse;
import myaong.popolog.psservice.dto.request.PsRequest;
import myaong.popolog.psservice.dto.response.PsIdResponse;
import myaong.popolog.psservice.dto.response.PsPreResponse;
import myaong.popolog.psservice.dto.response.PsResponse;
import myaong.popolog.psservice.service.PsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ps")
@RequiredArgsConstructor
public class PsController {

    private final PsService psService;

    @Operation(summary = "API 명세서 v0.4 line 66", description = "자기소개서 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PsPreResponse>>> getPsList(
            @RequestHeader("memberId") Long memberId) {
        List<PsPreResponse> response = psService.getPsList(memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.4 line 67", description = "자기소개서 상세조회")
    @GetMapping("/{psId}")
    public ResponseEntity<ApiResponse<PsResponse>> getPs(
            @RequestHeader("memberId") Long memberId,
            @PathVariable Long psId) {
        PsResponse response = psService.getPs(psId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.4 line 68", description = "자기소개서 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<PsIdResponse>> createPs(
            @RequestHeader("memberId") Long memberId,
            @Valid @RequestBody PsRequest psRequest) {
        PsIdResponse response = psService.createPs(psRequest, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "API 명세서 v0.4 line 69", description = "자기소개서 수정")
    @PutMapping("/{psId}")
    public ResponseEntity<ApiResponse<Object>> updatePs(
            @RequestHeader("memberId") Long memberId,
            @PathVariable Long psId,
            @Valid @RequestBody PsRequest psRequest) {
        psService.updatePs(psId, psRequest, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "API 명세서 v0.4 line 70", description = "자기소개서 목록 삭제")
    @DeleteMapping("/{psId}")
    public ResponseEntity<ApiResponse<Object>> deletePs(
            @RequestHeader("memberId") Long memberId,
            @PathVariable Long psId) {
        psService.deletePs(psId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @Operation(summary = "API 명세서 v0.4 line 71", description = "자기소개서 목록 첨삭")
    @GetMapping("/{psId}/editing")
    public ResponseEntity<ApiResponse<PsResponse>> editPs(
            @RequestHeader("memberId") Long memberId,
            @PathVariable Long psId) {
        PsResponse psResponse = psService.editPs(psId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(psResponse));
    }
}
