package com.github.restful.tool.annotation;


import com.github.restful.tool.beans.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ZhangYuanSheng
 */
@SuppressWarnings("ALL")
public enum UnistarHttpMethodAnnotation {

    /**
     * GET
     */
    GET("core.framework.api.web.service.GET", HttpMethod.GET),
    /**
     * POST
     */
    POST("core.framework.api.web.service.POST", HttpMethod.POST),
    /**
     * PUT
     */
    PUT("core.framework.api.web.service.PUT", HttpMethod.PUT),
    /**
     * DELETE
     */
    DELETE("core.framework.api.web.service.DELETE", HttpMethod.DELETE),
    /**
     * HEAD
     */
    HEAD("core.framework.api.web.service.HEAD", HttpMethod.HEAD),
    /**
     * PATCH
     */
    PATCH("core.framework.api.web.service.PATCH", HttpMethod.PATCH);

    private String qualifiedName;
    private HttpMethod method;

    UnistarHttpMethodAnnotation(String qualifiedName, HttpMethod method) {
        this.qualifiedName = qualifiedName;
        this.method = method;
    }

    @Nullable
    public static UnistarHttpMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (UnistarHttpMethodAnnotation springRequestAnnotation : UnistarHttpMethodAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @NotNull
    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") - 1);
    }
}