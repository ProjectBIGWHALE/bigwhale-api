package com.whale.web.security.cryptograph.service;

import com.whale.web.documents.imageconverter.exception.InvalidUploadedFileException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import com.whale.web.exceptions.domain.WhaleUnauthorizedException;
import com.whale.web.security.cryptograph.model.EncryptModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
public class EncryptService {

	private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";

	private byte[] encryptFile(MultipartFile formFile, String encryptionKey){

		try {
			byte[] bytesInFile = formFile.getBytes();
			byte[] keyBytes = Arrays.copyOf(encryptionKey.getBytes(StandardCharsets.UTF_8), 16); // Adjust the key size to 16 bytes
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			log.info("File encrypted");
			return cipher.doFinal(bytesInFile);
		} catch (Exception e) {
			log.error("File encrypted failed: " + e.getMessage());
			throw new WhaleRunTimeException(e.getMessage());
		}
	}

	private byte[] decryptFile(MultipartFile fileOfForm, String encryptionKey) {

		try {
			byte[] encryptedFile = fileOfForm.getBytes();
			byte[] keyBytes = Arrays.copyOf(encryptionKey.getBytes(StandardCharsets.UTF_8), 16); // Adjust the key size to 16 bytes
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

			log.info("File decrypted");
			return cipher.doFinal(encryptedFile);
		} catch (Exception e) {
			log.error("File decryption failed: " + e.getMessage());
			throw new WhaleUnauthorizedException(e.getMessage());
		}
	}

	public EncryptModel choiceEncryptService(Boolean action, String key, MultipartFile file){
		if (file.isEmpty()) {
			throw new InvalidUploadedFileException("An invalid file was sent");
		}
		if (key.isEmpty()) {
			throw new IllegalArgumentException("The key field is blank");
		}
        String fileName;
        byte[] newFile;
        if (Boolean.TRUE.equals(action)) {
            fileName = file.getOriginalFilename() + ".encrypted";
            newFile = encryptFile(file, key);

        } else {
            fileName = StringUtils.stripFilenameExtension(Objects.requireNonNull(file.getOriginalFilename()));
            newFile = decryptFile(file, key);

        }
        return new EncryptModel(fileName, newFile);
    }
}
