/*
  Copyright (C), 2018-2020, ZhangYuanSheng
  FileName: JaxrsHelper
  Author:   ZhangYuanSheng
  Date:     2020/5/28 21:01
  Description:
  History:
  <author>          <time>          <version>          <desc>
  作者姓名            修改时间           版本号              描述
 */
package com.github.restful.tool.utils.scanner;

import com.github.restful.tool.annotation.UnistarHttpMethodAnnotation;
import com.github.restful.tool.beans.HttpMethod;
import com.github.restful.tool.beans.Request;
import com.github.restful.tool.utils.ProjectConfigUtil;
import com.github.restful.tool.utils.RestUtil;
import com.github.restful.tool.utils.SystemUtil;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.impl.scopes.ModuleWithDependenciesScope;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author ZhangYuanSheng
 * @version 1.0
 */
public class UnistarHelper {

    @NotNull
    public static List<Request> getRequestByModule(@NotNull Project project, @NotNull Module module) {
        return getRequests(project, module);
    }

    @NotNull
    private static List<Request> getRequests(final @NotNull Project project, final @NotNull Module module) {
        List<Request> requests = new ArrayList<>();
        GlobalSearchScope moduleScope = ProjectConfigUtil.getModuleScope(module);
        Collection<PsiAnnotation> pathList = JavaAnnotationIndex.getInstance().get(
            Control.Path.getName(),
            project,
            moduleScope
        );
        // System.out.println("[LOG] project:" + project + " , module:" + module);

        // Set<HttpMethod> methods = new HashSet<>();
        // List<String> paths = new ArrayList<>();
        // 是否为隐式的path（未定义value或者path）
        pathList.forEach(annotation -> {
            List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
            for (JvmAnnotationAttribute attribute : attributes) {
                String path = null;

                String name = attribute.getAttributeName();
                // System.out.println("[LOG] name = " + name);
                if (!"value".equals(name)) {
                    continue;
                }
                Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
                if (value instanceof String) {
                    path = SystemUtil.formatPath(value);
                } else {
                    throw new RuntimeException(String.format(
                        "Scan api: %s\n" +
                            "Class: %s",
                        value,
                        value != null ? value.getClass() : null
                    ));
                }


                // two @GET @Path
                PsiModifierList psiModifierList = (PsiModifierList) annotation.getParent();

                PsiAnnotation[] annotations = psiModifierList.getAnnotations();
                if (Objects.isNull(annotations) || annotations.length != 2) {
                    continue;
                }
                HttpMethod httpMethod = null;
                if (psiModifierList.hasAnnotation(UnistarHttpMethodAnnotation.GET.getQualifiedName())) {
                    httpMethod = HttpMethod.GET;
                } else if (psiModifierList.hasAnnotation(UnistarHttpMethodAnnotation.POST.getQualifiedName())) {
                    httpMethod = HttpMethod.POST;
                } else if (psiModifierList.hasAnnotation(UnistarHttpMethodAnnotation.PUT.getQualifiedName())) {
                    httpMethod = HttpMethod.PUT;
                } else if (psiModifierList.hasAnnotation(UnistarHttpMethodAnnotation.DELETE.getQualifiedName())) {
                    httpMethod = HttpMethod.DELETE;
                } else if (psiModifierList.hasAnnotation(UnistarHttpMethodAnnotation.HEAD.getQualifiedName())) {
                    httpMethod = HttpMethod.HEAD;
                } else if (psiModifierList.hasAnnotation(UnistarHttpMethodAnnotation.PATCH.getQualifiedName())) {
                    httpMethod = HttpMethod.PATCH;
                } else {
                    httpMethod = HttpMethod.GET;
                }


                // System.out.println("[LOG] name = " + name + " , value:" + value);
                // System.out.println("[LOG] annotations.length = " + annotations.length);
                // System.out.println("[LOG] getQualifiedName = " + annotation.getQualifiedName() + " , path:" + path);

                final PsiMethod psiMethod = (PsiMethod) psiModifierList.getParent();
                requests.add(new Request(
                    httpMethod,
                    path,
                    psiMethod
                ));
            }
        });

        return requests;
    }

    public static boolean hasRestful(final PsiClass psiClass) {
        return psiClass.hasAnnotation(Control.Path.getQualifiedName());
    }

    public static List<Request> getCurrClassRequests(final PsiClass psiClass) {
        Project project = psiClass.getProject();
        GlobalSearchScope scope = psiClass.getResolveScope();
        ModuleWithDependenciesScope dependenciesScope = scope instanceof ModuleWithDependenciesScope ?
            ((ModuleWithDependenciesScope) scope) : null;
        if (dependenciesScope != null) {
            return getRequests(project, dependenciesScope.getModule());
        }
        return Collections.emptyList();
    }


    enum Control {

        /**
         * Javax.ws.rs.Path
         */
        Path("Path", "core.framework.api.web.service.Path");

        private final String name;
        private final String qualifiedName;

        Control(String name, String qualifiedName) {
            this.name = name;
            this.qualifiedName = qualifiedName;
        }

        @Nullable
        public static Control getPathByQualifiedName(String qualifiedName) {
            for (Control annotation : Control.values()) {
                if (annotation.getQualifiedName().equals(qualifiedName)) {
                    return annotation;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }
    }

    private static class PsiClassBean {

        public String rootPath;
        public PsiClass psiClass;

        public PsiClassBean(String rootPath, PsiClass psiClass) {
            this.rootPath = rootPath;
            this.psiClass = psiClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PsiClassBean that = (PsiClassBean) o;
            String name = psiClass.getName();
            if (name == null) {
                return false;
            }
            return name.equals(that.psiClass.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(psiClass.getName());
        }
    }
}
