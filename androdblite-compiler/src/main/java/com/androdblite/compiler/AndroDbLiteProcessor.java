package com.androdblite.compiler;

import android.content.ContentValues;
import android.database.Cursor;

import com.androdblite.DbColumn;
import com.androdblite.DbId;
import com.androdblite.DbIdServer;
import com.androdblite.DbIsDelete;
import com.androdblite.DbTable;
import com.androdblite.internal.DbIdBinder;
import com.androdblite.internal.DbIdServerBinder;
import com.androdblite.internal.DbTableBinder;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class AndroDbLiteProcessor extends AbstractProcessor {

    public static final String CLASS_NAME_APPENDER = "_DB";

    public static final String TABLE_NAME_FIELD = "_NAME";
    public static final String METHOD_GET_TABLE_NAME = "getTableName";

    public static final String METHOD_GET_CONTENTVALUES = "getContentValues";
    public static final String GET_CONTENT_VALUES_PARAM = "entity";

    public static final String METHOD_GET_FROM_CURSOR = "getFromCursor";
    public static final String TABLE_NAME_STATEMENT = "return " + TABLE_NAME_FIELD;

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

        final Map<Element, DbTableBinding> mapDbTable = new LinkedHashMap<>();

        final Set<? extends Element> elementDbTable = roundEnv.getElementsAnnotatedWith(DbTable.class);
        for (Element e : elementDbTable) {

            if (!ElementKind.CLASS.equals(e.getKind()))
                error(e, "@DbTable must be annotated for a class");

            if (mapDbTable.containsKey(e))
                error(e, "@DbTable put twice");

            final DbTableBinding dbTableBinding = new DbTableBinding();
            dbTableBinding.packageName = elementUtils.getPackageOf(e).toString();
            dbTableBinding.classOriginalName = e.getSimpleName().toString();
            dbTableBinding.className = dbTableBinding.classOriginalName + CLASS_NAME_APPENDER;
            mapDbTable.put(e, dbTableBinding);
        }

        // table Name
        for (Element e : elementDbTable) {

            DbTableBinding dbTableBinding = mapDbTable.get(e);
            DbTable dbTable = e.getAnnotation(DbTable.class);
            if (dbTable != null) {
                if (dbTable.value().length() == 0)
                    dbTableBinding.dbTable = new DbColumnBinding(TABLE_NAME_FIELD, e.getSimpleName().toString(), e.getSimpleName().toString());
                else
                    dbTableBinding.dbTable = new DbColumnBinding(TABLE_NAME_FIELD, e.getSimpleName().toString(), dbTable.value());
            }
        }

        Set<? extends Element> elementDbId = roundEnv.getElementsAnnotatedWith(DbId.class);
        for (Element e : elementDbId) {
            if (!mapDbTable.containsKey(e.getEnclosingElement()))
                error(e, "@DbId field must be in a @DbTable annotated class");

            if (e.getModifiers().contains(Modifier.PRIVATE))
                error(e, "@DbId field must not be private");

            DbTypes dbType = DbTypes.valueOfFullName(e.asType().toString());
            if (DbTypes.LONG != dbType)
                error(e, "@DbId field must be a long primitive");


            DbTableBinding dbTableBinding = mapDbTable.get(e.getEnclosingElement());
            dbTableBinding.dbType = dbType;
            DbId dbId = e.getAnnotation(DbId.class);
            if (dbId != null) {
                if (dbId.value().length() == 0)
                    dbTableBinding.dbColumnId = new DbColumnBinding(ID_FIELD, e.getSimpleName().toString(), e.getSimpleName().toString());
                else
                    dbTableBinding.dbColumnId = new DbColumnBinding(ID_FIELD, e.getSimpleName().toString(), dbId.value());
                dbTableBinding.dbColumnId.canSelect = true;
                dbTableBinding.dbColumnId.canInsertUpdate = false;
                break;
            }
        }

        Set<? extends Element> elementDbIdServer = roundEnv.getElementsAnnotatedWith(DbIdServer.class);
        for (Element e : elementDbIdServer) {
            if (!mapDbTable.containsKey(e.getEnclosingElement()))
                error(e, "@DbIdServer field must be in a @DbTable annotated class");

            if (e.getModifiers().contains(Modifier.PRIVATE))
                error(e, "@DbIdServer field must not be private");

            DbTypes dbType = DbTypes.valueOfFullName(e.asType().toString());
            if (dbType == null)
                error(e, "@DbIdServer is not a type manage");

            DbTableBinding dbTableBinding = mapDbTable.get(e.getEnclosingElement());
            dbTableBinding.dbType = dbType;
            DbIdServer dbIdServer = e.getAnnotation(DbIdServer.class);
            if (dbIdServer != null) {
                if (dbIdServer.value().length() == 0)
                    dbTableBinding.dbColumnIdServer = new DbColumnBinding(ID_SERVER_FIELD, e.getSimpleName().toString(), e.getSimpleName().toString());
                else
                    dbTableBinding.dbColumnIdServer = new DbColumnBinding(ID_SERVER_FIELD, e.getSimpleName().toString(), dbIdServer.value());
                dbTableBinding.dbColumnId.canSelect = true;
                dbTableBinding.dbColumnId.canInsertUpdate = true;
                break;
            }
        }

        Set<? extends Element> elementDbColumn = roundEnv.getElementsAnnotatedWith(DbColumn.class);
        for (Element e : elementDbColumn) {
            if (!mapDbTable.containsKey(e.getEnclosingElement()))
                error(e, "@DbColumn field must be in a @DbTable annotated class");

            if (e.getModifiers().contains(Modifier.PRIVATE))
                error(e, "@DbColumn field must not be private");

            DbTypes dbType = DbTypes.valueOfFullName(e.asType().toString());
            if (dbType == null)
                error(e, "@DbIdServer is not a type manage");

            DbTableBinding dbTableBinding = mapDbTable.get(e.getEnclosingElement());
            dbTableBinding.dbType = dbType;
            DbColumn dbIdServer = e.getAnnotation(DbColumn.class);
            if (dbIdServer != null) {
                DbColumnBinding e1;
                if (dbIdServer.value().length() == 0) {
                    e1 = new DbColumnBinding(getFieldName(e), e.getSimpleName().toString(), e.getSimpleName().toString());
                } else
                    e1 = new DbColumnBinding(getFieldName(e), e.getSimpleName().toString(), dbIdServer.value());

                e1.canSelect = dbIdServer.select();
                e1.canInsertUpdate = dbIdServer.insertUpdate();
                dbTableBinding.dbColumnBindings.add(e1);
            }
        }

        // Create Class
        try {
            for (Element e : mapDbTable.keySet()) {
                DbTableBinding dbTableBinding = mapDbTable.get(e);
                Set<FieldSpec> fieldSpecs = new LinkedHashSet<>(10);
                Set<MethodSpec> methodSpecs = new LinkedHashSet<>(10);
                TypeElement typeElement = (TypeElement) e;

                Name entityName = e.getSimpleName();
                TypeVariableName typeVariableNameE = TypeVariableName.get("E");
                ParameterizedTypeName dbTableParamVariable = ParameterizedTypeName.get(ClassName.get(DbTableBinder.class), TypeVariableName.get("E"));
                TypeVariableName typeVariableNameEextends = TypeVariableName.get(typeVariableNameE.name, TypeName.get(typeElement.asType()));
                ParameterizedTypeName parameterizedTypeNameE = ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("E"));

                // public class EntitySimple_DB_<E extends EntitySimple> extends EntitySimple
                TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(entityName + "_DB")
                        .addOriginatingElement(e)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(ClassName.get(e.asType()))
                        .addTypeVariable(typeVariableNameEextends);

                // public EntitySimple_DB_(Class<E> clazz) { this.clazz = clazz; }
                MethodSpec constructorSpec = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterizedTypeNameE, "clazz")
                        .addStatement("this.clazz = clazz")
                        .build();
                typeBuilder.addMethod(constructorSpec);

                //  public Class<E> clazz;
                fieldSpecs.add(FieldSpec.builder(parameterizedTypeNameE, "clazz", Modifier.PUBLIC).build());

                // implements DbTableBinder<E>
                typeBuilder = typeBuilder.addSuperinterface(dbTableParamVariable);

                // public static final String _NAME = "EntitySimple";
                addPublicStaticFinalField(fieldSpecs, TABLE_NAME_FIELD, dbTableBinding.dbTable.fieldValue);

                // @Override public String getTableName() { return _NAME; }
                methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_TABLE_NAME)
                        .returns(String.class)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(TABLE_NAME_STATEMENT)
                        .build());

                // Interface ID
                if (dbTableBinding.dbColumnId != null) {
                    addPublicStaticFinalField(fieldSpecs, ID_FIELD, dbTableBinding.dbColumnId.fieldValue);
                    typeBuilder = typeBuilder.addSuperinterface(ClassName.get(DbIdBinder.class));
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SELECTION_NAME)
                            .returns(String.class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbTableBinding.dbColumnId.fieldValue)
                            .build());
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SELECTION_ARGS_NAME)
                            .returns(String[].class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_ARGS_STATEMENT, dbTableBinding.dbColumnId.fieldValue)
                            .build());
                }

                // Interface ID_SERVER
                if (dbTableBinding.dbColumnIdServer != null) {
                    addPublicStaticFinalField(fieldSpecs, ID_SERVER_FIELD, dbTableBinding.dbColumnIdServer.fieldValue);
                    typeBuilder = typeBuilder.addSuperinterface(ClassName.get(DbIdServerBinder.class));
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SERVER_SELECTION_NAME)
                            .returns(String.class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbTableBinding.dbColumnIdServer.fieldValue)
                            .build());
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_ID_SERVER_SELECTION_ARGS_NAME)
                            .returns(String[].class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_ARGS_STATEMENT, dbTableBinding.dbColumnIdServer.fieldValue)
                            .build());
                }

                // Interface IS_DELETE
                if (dbTableBinding.dbColumnIsDelete != null) {
                    addPublicStaticFinalField(fieldSpecs, IS_DELETE_FIELD, dbTableBinding.dbColumnIsDelete.fieldValue);
                    typeBuilder = typeBuilder.addSuperinterface(ClassName.get(DbIdBinder.class));
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_IS_DELETE_SELECTION_NAME)
                            .returns(String[].class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbTableBinding.dbColumnIsDelete.fieldValue)
                            .build());
                    methodSpecs.add(MethodSpec.methodBuilder(METHOD_GET_IS_DELETE_SELECTION_ARGS_NAME)
                            .returns(String.class)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_ARGS_STATEMENT, dbTableBinding.dbColumnIsDelete.fieldValue)
                            .build());
                }

                // Field Name in BDD
                for (DbColumnBinding dbFieldBinding : dbTableBinding.dbColumnBindings) {
                    addPublicStaticFinalField(fieldSpecs, dbFieldBinding.fieldName, dbFieldBinding.fieldValue);
                    methodSpecs.add(MethodSpec.methodBuilder(String.format(METHOD_GET_NAME, getMethodName(dbFieldBinding.fieldOriginalName)))
                            .returns(String.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement(WHERE_STATEMENT, dbFieldBinding.fieldValue)
                            .build());
                }

                // getContentValues
                MethodSpec.Builder cvBuilder = MethodSpec.methodBuilder(METHOD_GET_CONTENTVALUES)
                        .returns(ContentValues.class)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(typeVariableNameE, GET_CONTENT_VALUES_PARAM)
                        .addStatement("final ContentValues cv = new ContentValues(15)");

                cvBuilder = addStatementPutIntoContentValue(dbTableBinding.dbColumnIdServer, cvBuilder);

                for (DbColumnBinding dbColumnBinding : dbTableBinding.dbColumnBindings) {
                    cvBuilder = addStatementPutIntoContentValue(dbColumnBinding, cvBuilder);
                }
                cvBuilder.addStatement("return cv");
                methodSpecs.add(cvBuilder.build());

                // getFromCursor
                ClassName className = ClassName.get(dbTableBinding.packageName, dbTableBinding.classOriginalName);
                MethodSpec.Builder builder = MethodSpec.methodBuilder(METHOD_GET_FROM_CURSOR)
                        .returns(typeVariableNameE)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(Cursor.class, "cursor")
                        .addStatement("E obj")
                        .addCode("try { \n")
                        .addStatement(" obj = clazz.newInstance()");

                // TODO

                builder.addCode("} catch ($T e) {\n", InstantiationException.class)
                        .addCode("  throw new $T(\"" + dbTableBinding.classOriginalName + " must have an empty public Constructor\"); \n", RuntimeException.class)
                        .addCode("} catch ($T e) {\n", IllegalAccessException.class)
                        .addCode("  throw new $T(\"" + dbTableBinding.classOriginalName + " must have an empty public Constructor\"); \n", RuntimeException.class)
                        .addCode("} \n")
                        .addStatement("return obj");

                addStatementGetFromCursor(dbTableBinding.dbColumnId, cvBuilder);


                MethodSpec fromCursorMethod = builder.build();
                methodSpecs.add(fromCursorMethod);

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


    private MethodSpec.Builder addStatementPutIntoContentValue(DbColumnBinding dbColumnBinding, MethodSpec.Builder cvBuilder) {
        if (dbColumnBinding != null && dbColumnBinding.canInsertUpdate) {
            if (dbColumnBinding.isPrimitiveType) {
                cvBuilder = cvBuilder.addStatement("cv.put(\"$L\", $L.$L)",
                        dbColumnBinding.fieldValue, GET_CONTENT_VALUES_PARAM, dbColumnBinding.fieldOriginalName);
            } else {
                cvBuilder.beginControlFlow("if( $L == null ) {\n")
                        .addStatement("cv.putNull($S)", dbColumnBinding.fieldOriginalName)
                        .nextControlFlow("} else {\n")
                        .addStatement("cv.put(\"$L\", $L.$L)",
                                dbColumnBinding.fieldValue, GET_CONTENT_VALUES_PARAM, dbColumnBinding.fieldOriginalName)
                        .endControlFlow();
            }
        }
        return cvBuilder;
    }

    private void addStatementGetFromCursor(DbColumnBinding dbColumn, MethodSpec.Builder cvBuilder) {
        if (dbColumn != null && dbColumn.canSelect) {

        }
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
