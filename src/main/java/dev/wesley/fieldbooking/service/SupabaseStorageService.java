package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.dto.UploadResult;
import dev.wesley.fieldbooking.error.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseStorageService {

    private final RestClient restClient = RestClient.create();

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.serviceRoleKey}")
    private String serviceRoleKey;

    @Value("${supabase.storage.default-bucket:avatars}")
    private String defaultBucket;

    /**
     * Upload genérico (POST) para qualquer bucket/pasta.
     * Use um path tipo: "{userId}/avatar.webp" ou "stores/{storeId}/{uuid}.webp"
     */
    public UploadResult upload(String bucket, String path, MultipartFile file, boolean upsert) throws IOException {
        if (bucket == null || bucket.isBlank()) bucket = defaultBucket;
        validateFile(file);

        byte[] bytes = file.getBytes();
        String contentType = (file.getContentType() == null || file.getContentType().isBlank())
                ? "application/octet-stream"
                : file.getContentType();

        // Supabase Storage endpoint:
        // POST /storage/v1/object/{bucket}/{path}
        // Para overwrite, a forma mais estável é: deletar antes (se existir) ou usar nomes únicos.
        // Alguns setups aceitam headers para upsert; vou fazer a estratégia segura:
        if (upsert) {
            // tenta deletar antes; se não existir, ok
            safeDelete(bucket, path);
        }

        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + encodePath(path);

        restClient.put()
                .uri(url)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .contentType(MediaType.parseMediaType(contentType))
                .body(bytes)
                .retrieve()
                .toBodilessEntity();

        String publicUrl = getPublicUrl(bucket, path);
        return new UploadResult(bucket, path, publicUrl);
    }

    public void delete(String bucket, String path) {
        if (bucket == null || bucket.isBlank()) bucket = defaultBucket;

        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + encodePath(path);

        restClient.delete()
                .uri(url)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("apikey", serviceRoleKey)
                .retrieve()
                .toBodilessEntity();
    }

    public void safeDelete(String bucket, String path) {
        try {
            delete(bucket, path);
        } catch (Exception ignored) {
            // se não existir, tanto faz
        }
    }

    public String getPublicUrl(String bucket, String path) {
        if (bucket == null || bucket.isBlank()) bucket = defaultBucket;
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + encodePath(path);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Arquivo obrigatório");
        }

        long maxBytes = 5L * 1024 * 1024; // 5MB (ajuste)
        if (file.getSize() > maxBytes) {
            throw new BadRequestException("Arquivo grande demais (max 5MB)");
        }

        // Se quiser restringir por tipo (recomendado), descomente:
        /*
        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
            throw new IllegalArgumentException("Tipo de arquivo inválido");
        }
        */
    }

    private String encodePath(String path) {
        // mantém "/" e escapa o resto
        return String.join("/",
                Arrays.stream(path.split("/"))
                        .map(p -> URLEncoder.encode(p, StandardCharsets.UTF_8))
                        .toList()
        );
    }


    public UploadResult uploadAvatar(UUID userId, MultipartFile file) throws IOException {
        String bucket = "avatars";
        String path = userId + "/avatar.webp";
        return upload(bucket, path, file, true);
    }

}
