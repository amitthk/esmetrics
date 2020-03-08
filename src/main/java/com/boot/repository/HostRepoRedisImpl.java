package com.boot.repository;

import com.boot.model.HostSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRepoRedisImpl extends JpaRepository<HostSummary, Long> {

}