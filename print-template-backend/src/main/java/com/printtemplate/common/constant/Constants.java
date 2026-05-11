package com.printtemplate.common.constant;

public class Constants {
    public static final class Status {
        public static final Integer ENABLE = 1;
        public static final Integer DISABLE = 0;
    }

    public static final class DefaultFlag {
        public static final Integer YES = 1;
        public static final Integer NO = 0;
    }

    public static final class TemplateStatus {
        public static final Integer DRAFT = 0;
        public static final Integer PUBLISHED = 1;
    }

    public static final class DataSourceType {
        public static final String API = "api";
        public static final String SQL = "sql";
        public static final String STATIC = "static";
    }

    public static final class VariableType {
        public static final String STRING = "string";
        public static final String NUMBER = "number";
        public static final String DATE = "date";
        public static final String ARRAY = "array";
    }

    public static final class PrintStatus {
        public static final Integer PENDING = 0;
        public static final Integer PRINTING = 1;
        public static final Integer COMPLETED = 2;
        public static final Integer FAILED = 3;
    }

    public static final class OperationModule {
        public static final String TEMPLATE = "template";
        public static final String DATASOURCE = "datasource";
        public static final String PRINT = "print";
    }

    public static final class OperationType {
        public static final String CREATE = "create";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String PRINT = "print";
    }

    public static final class PaperSize {
        public static final String A4 = "A4";
        public static final String A5 = "A5";
        public static final String A3 = "A3";
        public static final String LETTER = "letter";
    }

    public static final class Orientation {
        public static final String PORTRAIT = "portrait";
        public static final String LANDSCAPE = "landscape";
    }

    public static final class ElementType {
        public static final String TEXT = "text";
        public static final String IMAGE = "image";
        public static final String TABLE = "table";
        public static final String BARCODE = "barcode";
        public static final String QRCODE = "qrcode";
        public static final String LINE = "line";
        public static final String RECT = "rect";
        public static final String CIRCLE = "circle";
    }
}
