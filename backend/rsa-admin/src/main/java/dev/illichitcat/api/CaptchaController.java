package dev.illichitcat.api;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Illichitcat
 * @since 2025/12/24
 */
@RestController
@Tag(name = "验证码接口", description = "滑块验证码生成和校验")
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
@Slf4j
public class CaptchaController {

    private final ImageCaptchaApplication imageCaptchaApplication;

    /**
     * 生成验证码
     */
    @Operation(
            summary = "生成验证码",
            description = "生成滑块验证码、旋转验证码、点选验证码等多种类型的验证码"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "验证码生成成功",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": true,
                                                "id": "captcha_123456789",
                                                "data": {
                                                    "backgroundImage": "base64_image_data",
                                                    "sliderImage": "base64_image_data",
                                                    "x": 150,
                                                    "y": 100
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "验证码生成失败",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": false,
                                                "msg": "验证码服务暂时不可用，请稍后重试"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/generate")
    public Object generate(
            @Parameter(description = "验证码生成参数")
            @RequestBody(required = false) GenerateCaptchaDto generateCaptchaDto) {
        try {
            // 默认使用滑块验证码
            String captchaType = "SLIDER";
            if (generateCaptchaDto != null && generateCaptchaDto.getType() != null) {
                captchaType = getCaptchaTypeByCode(generateCaptchaDto.getType());
            }

            log.info("开始生成验证码，类型: {}", captchaType);

            // 直接返回官方的ApiResponse对象
            return imageCaptchaApplication.generateCaptcha(captchaType);
        } catch (IllegalStateException e) {
            // 特殊处理资源为空的错误
            log.error("验证码资源未初始化，请检查资源文件: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>(2);
            errorResponse.put("success", false);
            errorResponse.put("msg", "验证码服务暂时不可用，请稍后重试");
            return errorResponse;
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            Map<String, Object> errorResponse = new HashMap<>(2);
            errorResponse.put("success", false);
            errorResponse.put("msg", "生成验证码失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 校验验证码
     */
    @Operation(
            summary = "校验验证码",
            description = "校验用户拖动滑块等操作的验证结果"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "验证码校验成功",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": true,
                                                "msg": "验证通过"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "验证码校验失败",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": false,
                                                "msg": "验证失败，请重试"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/check")
    public Object check(
            @Parameter(description = "验证码校验参数", required = true)
            @RequestBody CaptchaTrackDto captchaTrackDto) {
        try {
            log.info("校验验证码，请求参数: {}", captchaTrackDto);
            ImageCaptchaTrack imageCaptchaTrack = captchaTrackDto.getData();
            return imageCaptchaApplication.matching(captchaTrackDto.getId(), imageCaptchaTrack);
        } catch (Exception e) {
            log.error("校验验证码异常", e);
            Map<String, Object> errorResponse = new HashMap<>(2);
            errorResponse.put("success", false);
            errorResponse.put("msg", "校验验证码异常");
            return errorResponse;
        }
    }

    /**
     * 根据类型码获取验证码类型
     */
    private String getCaptchaTypeByCode(Integer typeCode) {
        if (typeCode == null) {
            return "SLIDER";
        }
        return switch (typeCode) {
            case 1 -> "SLIDER";
            case 2 -> "ROTATE";
            case 3 -> "CONCAT";
            case 4 -> "WORD_IMAGE_CLICK";
            default -> throw new IllegalStateException("Unexpected value: " + typeCode);
        };
    }

    /**
     * 生成验证码DTO
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Schema(description = "验证码生成请求参数")
    @Setter
    @Getter
    public static class GenerateCaptchaDto {
        @Schema(description = "验证码类型：1-滑块验证码，2-旋转验证码，3-拼接验证码，4-点选验证码",
                allowableValues = {"1", "2", "3", "4"},
                example = "1")
        private Integer type;

        @Override
        public String toString() {
            return "GenerateCaptchaDto{" +
                    "type=" + type +
                    '}';
        }
    }

    /**
     * 验证码校验DTO
     *
     * @author Illichitcat
     * @since 2025/12/24
     */
    @Schema(description = "验证码校验请求参数")
    @Setter
    @Getter
    public static class CaptchaTrackDto {
        @Schema(description = "验证码ID", example = "captcha_123456789")
        private String id;

        @Schema(description = "验证码轨迹数据")
        private ImageCaptchaTrack data;

        @Override
        public String toString() {
            return "CaptchaTrackDto{" +
                    "id='" + id + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}