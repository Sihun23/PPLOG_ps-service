package myaong.popolog.psservice.repository;

import myaong.popolog.psservice.entity.Ps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PsRepository extends JpaRepository<Ps, Long> {

    List<Ps> findByMemberId(Long memberId);
}
