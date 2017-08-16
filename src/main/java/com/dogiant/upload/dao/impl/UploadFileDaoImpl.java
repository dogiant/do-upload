package com.dogiant.upload.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dogiant.upload.dao.UploadFileDao;
import com.dogiant.upload.domain.UploadFile;
import com.dogiant.upload.repo.UploadFileRepo;

@Service("uploadFileDao")
public class UploadFileDaoImpl implements UploadFileDao {

	@Autowired
	private UploadFileRepo uploadFileRepo;

	@Override
	public UploadFile save(UploadFile uploadFile) {
		return uploadFileRepo.save(uploadFile);
	}

}
