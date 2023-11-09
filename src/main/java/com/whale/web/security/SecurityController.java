package com.whale.web.security;

import com.whale.web.security.cryptograph.model.CryptographyForm;
import com.whale.web.security.cryptograph.service.EncryptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


@Controller
@RequestMapping("/security")
public class SecurityController {

    private final CryptographyForm form;

    private final EncryptService encryptService;

    public SecurityController(CryptographyForm form, EncryptService encryptService) {
        this.form = form;
        this.encryptService = encryptService;
    }

    @GetMapping(value = "/cryptograph")
    public String cryptograph(Model model) {

        model.addAttribute("form", form);
        return "cryptograph";
    }

    @PostMapping("/cryptograph")
    public String cryptograph(CryptographyForm form, HttpServletResponse response) throws IOException {

        try {

            byte[] encryptedFile;
            String originalFilename = form.getFile().getOriginalFilename();
            String originalFileNameWithoutExtension = StringUtils.stripFilenameExtension(Objects.requireNonNull(originalFilename));

            if (Boolean.TRUE.equals(form.getAction())) {
                encryptedFile = encryptService.encryptFile(form);
                response.setHeader("Content-Disposition", "attachment; filename=" + originalFilename + ".encrypted");
            } else {
                encryptedFile = encryptService.decryptFile(form);
                response.setHeader("Content-Disposition", "attachment; filename=" + originalFileNameWithoutExtension);
            }

            response.setContentType("application/octet-stream");
            response.setContentLength(encryptedFile.length);
            response.setHeader("Cache-Control", "no-cache");

            try (OutputStream outputStream = response.getOutputStream()) {
                outputStream.write(encryptedFile);
                outputStream.flush();
            } catch (Exception e) {
                throw new RuntimeException("Error generating encrypted file", e);
            }
        } catch (Exception e) {
            return "redirect:/security/cryptograph";
        }

        return null;
    }
}
