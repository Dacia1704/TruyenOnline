package com.dacia1704.truyenonline.module.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dacia1704.truyenonline.module.media.dto.response.CloudinaryUploadResult;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private final Executor executor;

    public CloudinaryService(
            Cloudinary cloudinary, @Qualifier("cloudinaryExecutor") Executor executor) {
        this.cloudinary = cloudinary;
        this.executor = executor;
    }

    public CloudinaryUploadResult uploadImage(MultipartFile file, String folder) {
        try {
            Map uploadResult =
                    cloudinary
                            .uploader()
                            .upload(
                                    file.getBytes(),
                                    ObjectUtils.asMap("folder", folder, "resource_type", "image"));
            return CloudinaryUploadResult.builder()
                    .secureUrl(uploadResult.get("secure_url").toString())
                    .publicId(uploadResult.get("public_id").toString())
                    .width((Integer) uploadResult.get("width"))
                    .height((Integer) uploadResult.get("height"))
                    .build();
        } catch (IOException e) {
            throw new AppException(ErrorCode.UPLOAD_IMAGE_ERROR);
        }
    }

    public List<CloudinaryUploadResult> uploadImagesAsync(
            List<MultipartFile> files, String folder) {
        // 1. Chia việc cho các Thread chạy song song
        List<CompletableFuture<CloudinaryUploadResult>> futures =
                files.stream()
                        .map(
                                file ->
                                        CompletableFuture.supplyAsync(
                                                () -> uploadImage(file, folder),
                                                executor)) // <--- Ép nó chạy trên ThreadPool chuyên
                        // dụng, không dùng luồng chính
                        .toList();

        // 2. Chờ TẤT CẢ các luồng hoàn thành và gom kết quả lại
        return futures.stream().map(CompletableFuture::join).toList();
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public void deleteFolder(String folderPath) throws Exception {
        // Xóa toàn bộ file trong thư mục
        cloudinary.api().deleteResourcesByPrefix(folderPath, ObjectUtils.emptyMap());
        // Xóa vỏ thư mục rỗng
        cloudinary.api().deleteFolder(folderPath, ObjectUtils.emptyMap());
    }
}
