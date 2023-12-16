package com.whale.web.security.cryptograph.service;

import com.whale.web.exceptions.domain.FileIsEmptyException;
import com.whale.web.exceptions.domain.WhaleCheckedException;
import com.whale.web.exceptions.domain.WhaleRunTimeException;
import com.whale.web.exceptions.domain.WhaleUnauthorizedException;
import com.whale.web.security.cryptograph.model.EncryptModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
public class EncryptService {

	private static final String CIPHER_INSTANCE = "AES/CBC/PKCS5Padding";

	public EncryptModel choiceEncryptService(Boolean action, String key, MultipartFile file) throws FileIsEmptyException, WhaleCheckedException {
		if (file.isEmpty()) throw new FileIsEmptyException("An invalid file was sent");
		if (key.isBlank()) throw new WhaleRunTimeException("The key field is blank");

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
		} catch (Exception e) {
			log.error("Failed to encrypt the file" + e.getMessage());
			throw new WhaleCheckedException("Failed to decrypt the file");
		}
	}


}
