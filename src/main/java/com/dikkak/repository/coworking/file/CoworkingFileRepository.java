package com.dikkak.repository.coworking.file;

import com.dikkak.entity.coworking.CoworkingFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoworkingFileRepository
        extends JpaRepository<CoworkingFile, Long>, CoworkingFileRepositoryCustom {
}
