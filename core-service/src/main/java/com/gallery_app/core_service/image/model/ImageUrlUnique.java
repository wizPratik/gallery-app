package com.gallery_app.core_service.image.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.gallery_app.core_service.image.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the url value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ImageUrlUnique.ImageUrlUniqueValidator.class
)
public @interface ImageUrlUnique {

    String message() default "{Exists.image.url}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ImageUrlUniqueValidator implements ConstraintValidator<ImageUrlUnique, String> {

        private final ImageService imageService;
        private final HttpServletRequest request;

        public ImageUrlUniqueValidator(final ImageService imageService,
                final HttpServletRequest request) {
            this.imageService = imageService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("uuid");
            if (currentId != null && value.equalsIgnoreCase(imageService.get(UUID.fromString(currentId)).getUrl())) {
                // value hasn't changed
                return true;
            }
            return !imageService.urlExists(value);
        }

    }

}
