package com.whale.web.security.cryptograph.service;

import com.whale.web.documents.imageconverter.exception.InvalidUploadedFileException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import com.whale.web.exceptions.domain.WhaleUnauthorizedException;
import com.whale.web.security.cryptograph.model.EncryptModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class EncryptService {

	private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";

	private byte[] encryptFile(MultipartFile formFile, String encryptionKey){

		try {
			if (formFile.isEmpty() || encryptionKey.isEmpty()) {
				throw new InvalidUploadedFileException("An invalid file was sent");
			}

			byte[] bytesInFile = formFile.getBytes();
			byte[] keyBytes = Arrays.copyOf(encryptionKey.getBytes(StandardCharsets.UTF_8), 16); // Adjust the key size to 16 bytes
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

			return cipher.doFinal(bytesInFile);
		} catch (Exception e) {
			throw new WhaleRunTimeException(e.getMessage());
		}
	}

	private byte[] decryptFile(MultipartFile fileOfForm, String encryptionKey) {

		try {

			if (fileOfForm.isEmpty() || encryptionKey.isEmpty()) {
				throw new InvalidUploadedFileException("An invalid file was sent");
			}

			byte[] encryptedFile = fileOfForm.getBytes();
			byte[] keyBytes = Arrays.copyOf(encryptionKey.getBytes(StandardCharsets.UTF_8), 16); // Adjust the key size to 16 bytes
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

			return cipher.doFinal(encryptedFile);
		} catch (Exception e) {
			throw new WhaleUnauthorizedException(e.getMessage());
		}
	}

	public EncryptModel choiceEncryptService(Boolean action, String key, MultipartFile file){
		if (Boolean.TRUE.equals(action)) {
			String fileName = file.getOriginalFilename()+".encrypted";
			byte[] newFile = encryptFile(file, key);

			return new EncryptModel(fileName, newFile);
		} else {
			String fileName = StringUtils.stripFilenameExtension(Objects.requireNonNull(file.getOriginalFilename()));
			byte[] newFile = decryptFile(file, key);

			return new EncryptModel(fileName, newFile);
		}
	}
}
