package com.whale.web.security.cryptograph.service;

import com.whale.web.exceptions.domain.WhaleInvalidFileException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import com.whale.web.exceptions.domain.WhaleUnauthorizedException;
import com.whale.web.security.cryptograph.model.EncryptModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
public class EncryptService {

	private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";

	public EncryptModel choiceEncryptService(Boolean action, String key, MultipartFile file) throws WhaleInvalidFileException, WhaleCheckedException {
		if (file.isEmpty()) throw new WhaleInvalidFileException("The uploaded file is invalid");
		if (key.isBlank()) throw new WhaleRunTimeException("The key field is blank");

		String fileName;
		byte[] newFile;

		if (Boolean.TRUE.equals(action) && !isEncryptedFile(file)) {
			fileName = file.getOriginalFilename() + ".encrypted";
			newFile = encryptFile(file, key);
		} else if (Boolean.TRUE.equals(action) && isEncryptedFile(file)) {
			throw new WhaleInvalidFileException("The uploaded file is already encrypted");
		} else {
			fileName = StringUtils.stripFilenameExtension(Objects.requireNonNull(file.getOriginalFilename()));
			newFile = decryptFile(file, key);
		}
		return new EncryptModel(fileName, newFile);
	}
	private byte[] encryptFile(MultipartFile formFile, String encryptionKey) throws WhaleCheckedException {

		try {
			byte[] bytesInFile = formFile.getBytes();
			byte[] keyBytes = Arrays.copyOf(encryptionKey.getBytes(StandardCharsets.UTF_8), 16); // Adjust the key size to 16 bytes
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

			return cipher.doFinal(bytesInFile);
		} catch (Exception e) {
			log.error("Failed to encrypt the file " + e.getMessage());
			throw new WhaleCheckedException("Failed to encrypt the file");
		}
	}

	private byte[] decryptFile(MultipartFile fileOfForm, String encryptionKey) throws WhaleCheckedException {

		try {
			byte[] encryptedFile = fileOfForm.getBytes();
			byte[] keyBytes = Arrays.copyOf(encryptionKey.getBytes(StandardCharsets.UTF_8), 16); // Adjust the key size to 16 bytes
			SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			return cipher.doFinal(encryptedFile);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			log.error("Invalid key" + e.getMessage());
			throw new WhaleUnauthorizedException("Invalid key");
		} catch (Exception e) {
			log.error("Failed to decrypt the file" + e.getMessage());
			throw new WhaleCheckedException("Failed to decrypt the file");
		}

    }


	private static boolean isEncryptedFile(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename()).endsWith(".encrypted");
    }


}
