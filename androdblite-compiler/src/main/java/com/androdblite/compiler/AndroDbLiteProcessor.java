package com.androdblite.compiler;

import com.androdblite.DbColumn;
import com.androdblite.DbId;
import com.androdblite.DbIdServer;
import com.androdblite.DbIsDelete;
import com.androdblite.DbTable;
import com.androdblite.internal.DbIdBinder;
import com.androdblite.internal.DbIdServerBinder;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class AndroDbLiteProcessor extends AbstractProcessor {

    public static final String CLASS_NAME_APPENDER = "_DB";
    public static final String TABLE_NAME_FIELD = "_NAME";

    public static final String ID_FIELD = "ID";
    public static final String METHOD_GET_ID_SELECTION_NAME = "getIdSelection";
    public static final String METHOD_GET_ID_SELECTION_ARGS_NAME = "getIdSelectionArgs";

    public static final String ID_SERVER_FIELD = "ID_SERVER";
    public static final String METHOD_GET_ID_SERVER_SELECTION_NAME = "getIdServerSelection";
    public static final String METHOD_GET_ID_SERVER_SELECTION_ARGS_NAME = "getIdServerSelectionArgs";

    public static final String IS_DELETE_FIELD = "IS_DELETE";
    public static final String METHOD_GET_IS_DELETE_SELECTION_NAME = "getIsDeleteSelection";
    public static final String METHOD_GET_IS_DELETE_SELECTION_ARGS_NAME = "getIsDeleteSelectionArgs";

    public static final String METHOD_GET_NAME = "get%sSelection";

    public static final String WHERE_STATEMENT = "return \"$L=?\"";
    public static final String WHERE_ARGS_STATEMENT = "return new String[] { String.valueOf( $L ) }";
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
        messager = env.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(DbTable.class.getCanonicalName());
        types.add(DbId.class.getCanonicalName());
        types.add(DbIdServer.class.getCanonicalName());
        types.add(DbIsDelete.class.getCanonicalName());
        types.add(DbColumn.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Map<Element, DbFieldBinding> mapDbTable = new LinkedHashMap<>();

        final Set<? extends Element> elementDbTable = roundEnv.getElementsAnnotatedWith(DbTable.class);
        for (Element e : elementDbTable) {

            if (!ElementKind.CLASS.equals(e.getKind()))
                error(e, "@DbTable must be annotated for a class");

            if (mapDbTable.containsKey(e))
                error(e, "@DbTable put twice");

            final DbFieldBinding dbFieldBinding = new DbFieldBinding();
            dbFieldBinding.packageName = elementUtils.getPackageOf(e).toString();
            dbFieldBinding.className = e.getSimpleName() + CLASS_NAME_APPENDER;
            mapDbTable.put(e, dbFieldBinding);
        }

        // table Name
        for (Element e : elementDbTable) {

            DbFieldBinding dbFieldBinding = mapDbTable.get(e);
            DbTable dbTable = e.getAnnotation(DbTable.class);
            if (dbTable != null) {
                if (dbTable.value().length() == 0)
                    dbFieldBinding.dbTable = new DbColumnBinding(TABLE_NAME_FIELD, e.getSimpleName().toString(), e.getSimpleName().toString());
                else
                    dbFieldBinding.dbTable = new DbColumnBinding(TABLE_NAME_FIELD, e.getSimpleName().toString(), dbTable.value());
            }
        }

        Set<? extends Element> elementDbId = roundEnv.getElementsAnnotatedWith(DbId.class);
        for (Element e : elementDbId) {
            if (!mapDbTable.containsKey(e.getEnclosingElement()))
                error(e, "@DbId field must be in a @DbTable annotated class");

            if (e.getModifiers().contains(Modifier.PRIVATE))
                error(e, "@DbId field must not be private");

            DbFieldBinding dbFieldBinding = mapDbTable.get(e.getEnclosingElement());
            DbId dbId = e.getAnnotation(DbId.class);
            if (dbId != null) {
                if (dbId.value().length() == 0)
                    dbFieldBinding.dbFieldId = new DbColumnBinding(ID_FIELD, e.getSimpleName().toString(), e.getSimpleName().toString());
                else
                    dbFieldBinding.dbFieldId = new DbColumnBinding(ID_FIELD, e.getSimpleName().toString(), dbId.value());
                break;
            }
        }

        Set<? extends Element> elementDbIdServer = roundEnv.getElementsAnnotatedWith(DbIdServer.class);
        for (Element e : elementDbIdServer) {
            if (!mapDbTable.containsKey(e.getEnclosingElement()))
                error(e, "@DbIdServer field must be in a @DbTable annotated class");

            if (e.getModifiers().contains(Modifier.PRIVATE))
                error(e, "@DbIdServer field must not be private");

            DbFieldBinding dbFieldBinding = mapDbTable.get(e.getEnclosingElement());
            DbIdServer dbIdServer = e.getAnnotation(DbIdServer.class);
            if (dbIdServer != null) {
                if (dbIdServer.value().length() == 0)
                    dbFieldBinding.dbFieldIdServer = new DbColumnBinding(ID_SERVER_FIELD, e.getSimpleName().toString(), e.getSimpleName().toString());
                else
                    dbFieldBinding.dbFieldIdServer = new DbColumnBinding(ID_SERVER_FIELD, e.getSimpleName().toString(), dbIdServer.value());
                break;
            }
        }

        Set<? extends Element> elementDbColumn = roundEnv.getElementsAnnotatedWith(DbColumn.class);
        for (Element e : elementDbColumn) {
            if (!mapDbTable.containsKey(e.getEnclosingElement()))
                error(e, "@DbColumn field must be in a @DbTable annotated class");

            if (e.getModifiers().contains(Modifier.PRIVATE))
                error(e, "@DbColumn field must not be private");

            DbFieldBinding dbFieldBinding = mapDbTable.get(e.getEnclosingElement());
            DbColumn dbIdServer = e.getAnnotation(DbColumn.class);
            if (dbIdServer != null) {
                DbColumnBinding e1;
                if (dbIdServer.value().length() == 0) {
                    e1 = new DbColumnBinding(getFieldName(e), e.getSimpleName().toString(), e.getSimpleName().toString());
                } else
                    e1 = new DbColumnBinding(getFieldName(e), e.getSimpleName().toString(), dbIdServer.value());
                dbFieldBinding.fieldBindings.add(e1);
            }
        }

        // Create Class
        try {
            for (Element e : mapDbTable.keySet()) {
                DbFieldBinding dbTableBinding = mapDbTable.get(e);
                Set<FieldSpec> fieldSpecs = new LinkedHashSet<>(10);
                Set<MethodSpec> methodSpecs = new LinkedHashSet<>(10);

                TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(e.getSimpleName() + "_DB")
                        .addOriginatingElement(e)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(ClassName.get(e.asType()));


                addPublicStaticFinalField(fieldSpecs, TABLE_NAME_FIELD, dbTableBinding.dbTable.fieldValue);

                // Interface ID
                if (dbTableBinding.dbFieldId != null) {
                    addPublicStaticFinalField(fieldSpecs, ID_FIELD, dbTableBinding.dbFieldId.fieldValue);
                    typeBuilder = typeBuilder.addSuperinterface(ClassName.get(DbIdBinder.class));
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SELECTION_NAME)
                            .returns(String.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbTableBinding.dbFieldId.fieldValue)
                            .build());
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SELECTION_ARGS_NAME)
                            .returns(String[].class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_ARGS_STATEMENT, dbTableBinding.dbFieldId.fieldValue)
                            .build());
                }

                // Interface ID_SERVER
                if (dbTableBinding.dbFieldIdServer != null) {
                    addPublicStaticFinalField(fieldSpecs, ID_SERVER_FIELD, dbTableBinding.dbFieldIdServer.fieldValue);
                    typeBuilder = typeBuilder.addSuperinterface(ClassName.get(DbIdServerBinder.class));
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SERVER_SELECTION_NAME)
                            .returns(String.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbTableBinding.dbFieldIdServer.fieldValue)
                            .build());
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SERVER_SELECTION_ARGS_NAME)
                            .returns(String[].class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_ARGS_STATEMENT, dbTableBinding.dbFieldIdServer.fieldValue)
                            .build());
                }

                // Interface IS_DELETE
                if (dbTableBinding.dbFieldIsDelete != null) {
                    addPublicStaticFinalField(fieldSpecs, IS_DELETE_FIELD, dbTableBinding.dbFieldIsDelete.fieldValue);
                    typeBuilder = typeBuilder.addSuperinterface(ClassName.get(DbIdBinder.class));
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_IS_DELETE_SELECTION_NAME)
                            .returns(String[].class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbTableBinding.dbFieldIsDelete.fieldValue)
                            .build());
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_IS_DELETE_SELECTION_ARGS_NAME)
                            .returns(String.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_ARGS_STATEMENT, dbTableBinding.dbFieldIsDelete.fieldValue)
                            .build());
                }

                // Field Name in BDD
                for (DbColumnBinding dbFieldBinding : dbTableBinding.fieldBindings) {
                    addPublicStaticFinalField(fieldSpecs, dbFieldBinding.fieldName, dbFieldBinding.fieldValue);
                    methodSpecs.add(MethodSpec.methodBuilder(String.format(METHOD_GET_NAME, getMethodName(dbFieldBinding.fieldOriginalName)))
                            .returns(String.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbFieldBinding.fieldValue)
                            .build());
                }

                TypeSpec typeSpec = typeBuilder
                        .addFields(fieldSpecs)
                        .addMethods(methodSpecs)
                        .build();
                JavaFile javaFile = JavaFile.builder(
                        elementUtils.getPackageOf(e).toString(), typeSpec)
                        .build();
                javaFile.writeTo(filer);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

//        for (TypeElement annotation : annotations) {
//
//            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
//            Set<FieldSpec> fieldSpecs = new LinkedHashSet<>(10);
//
//            for (Element element : elements) {
//                try {
//                    addTableNameField(fieldSpecs, element);
//                    addIdFieldIfPresent(element, fieldSpecs);
//                    addIdServerFieldIfPresent(element, fieldSpecs);
//
//                    TypeSpec typeSpec = TypeSpec.classBuilder(element.getSimpleName() + "_DB")
//                            .addModifiers(Modifier.PUBLIC)
//                            .addFields(fieldSpecs)
//                            .build();
//                    JavaFile javaFile = JavaFile.builder(
//                            elementUtils.getPackageOf(element).toString(), typeSpec)
//                            .build();
//                    javaFile.writeTo(filer);
//
//                } catch (Exception ex) {
//                    error(element, "Unable to write generated class for type %s: %s", element,
//                            ex.getMessage());
//                }
//            }
//        }

        return false;
    }

    private String getFieldName(Element e) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, e.getSimpleName().toString());
    }

    private String getMethodName(String method) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, method);
    }

    private void addTableNameField(Set<FieldSpec> fieldSpecs, Element element) {

        String value = null;
        final List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        for (AnnotationMirror annot : annotationMirrors) {
            if (annot.getAnnotationType() instanceof DbTable) {
                DbTable dbTable = (DbTable) annot;
                if (dbTable.value().length() == 0)
                    value = element.getSimpleName().toString();
                else
                    value = dbTable.value();
                break;
            }
        }
        addPublicStaticFinalField(fieldSpecs, value, TABLE_NAME_FIELD);
    }

    private void addIdFieldIfPresent(Element element, Set<FieldSpec> fieldSpecs) {
        String value = null;
        // Annot Value
        List<? extends Element> fields = element.getEnclosedElements();
        for (Element field : fields) {
            DbId dbId = field.getAnnotation(DbId.class);
            if (dbId != null) {
                if (value != null) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Several @DbId in %s.class", element.getSimpleName().toString()),
                            element);
                }
                if (dbId.value().length() == 0)
                    value = field.getSimpleName().toString();
                else
                    value = dbId.value();
            }
        }
        addPublicStaticFinalField(fieldSpecs, value, ID_FIELD);
    }

    private void addIdServerFieldIfPresent(Element element, Set<FieldSpec> fieldSpecs) {
        String idColumnName = null;
        // Annot Value
        List<? extends Element> fields = element.getEnclosedElements();
        for (Element field : fields) {
            DbIdServer dbIdServer = field.getAnnotation(DbIdServer.class);
            if (dbIdServer != null) {
                if (idColumnName != null) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Several @DbIdServer in %s.class", element.getSimpleName().toString()),
                            element);
                }
                if (dbIdServer.value().length() == 0)
                    idColumnName = field.getSimpleName().toString();
                else
                    idColumnName = dbIdServer.value();
            }
        }
        addPublicStaticFinalField(fieldSpecs, idColumnName, ID_SERVER_FIELD);
    }

    private void addPublicStaticFinalField(Set<FieldSpec> fieldSpecs, String fieldName, String fieldValue) {
        if (fieldValue != null) {
            fieldSpecs.add(FieldSpec.builder(String.class, fieldName,
                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", fieldValue)
                    .build());
        }
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }
}
