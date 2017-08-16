package com.dogiant.upload.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dogiant.upload.domain.UploadFile;

public interface UploadFileRepo extends JpaRepository<UploadFile, Integer>, JpaSpecificationExecutor<UploadFile> {

}
