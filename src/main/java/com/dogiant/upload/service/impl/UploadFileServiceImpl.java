package com.dogiant.upload.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dogiant.upload.dao.UploadFileDao;
import com.dogiant.upload.domain.UploadFile;
import com.dogiant.upload.service.UploadFileService;

@Service("uploadService")
public class UploadFileServiceImpl implements UploadFileService {
	
	@Autowired
	private UploadFileDao uploadFileDao;

	@Override
	public UploadFile save(UploadFile uploadFile) {
		return uploadFileDao.save(uploadFile);
	}

}
