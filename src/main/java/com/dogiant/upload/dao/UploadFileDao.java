package com.dogiant.upload.dao;

import com.dogiant.upload.domain.UploadFile;

public interface UploadFileDao {
	
	public abstract UploadFile save(UploadFile uploadFile);

}
