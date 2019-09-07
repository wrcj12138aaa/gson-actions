package com.one.learn;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Exclude Fields from Serialization in Gson
 * https://www.baeldung.com/gson-exclude-fields-serialization
 *
 * @author One
 * @date 2019/09/07
 */
public class ExcludeFieldsTest {
    /**
     * transient 字段不会被序列化，Java 默认行为
     */
    @Test
    void test_transient() {
        MySubClass subclass = new MySubClass(42L, "the answer", "Verbose field not to serialize");
        MyClass source = new MyClass(1L, "foo", "bar", subclass);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String s = gson.toJson(source);
        System.out.println(s);
    }

    /**
     * @Expose 指定暴露字段
     */
    @Test
    void test_expose() {
        MySubClass subclass = new MySubClass(42L, "the answer", "Verbose field not to serialize");
        MyClass source = new MyClass(1L, "foo", "bar", subclass);
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String s = gson.toJson(source);
        System.out.println(s);
    }


    /**
     * 自定义字段排除策略
     */
    @Test
    void test_exclusionStrategy() {
        ExclusionStrategy strategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                if (field.getDeclaringClass() == MyClass.class && field.getName().equals("other")) {
                    return true;
                }
                if (field.getDeclaringClass() == MySubClass.class && field.getName().equals("otherVerboseInfo")) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(strategy)
                .create();
        MySubClass subclass = new MySubClass(42L, "the answer", "Verbose field not to serialize");
        MyClass source = new MyClass(1L, "foo", "bar", subclass);
        String jsonString = gson.toJson(source);
        String expectedResult = "{\"id\":1,\"name\":\"foo\",\"subclass\":{\"id\":42,\"description\":\"the answer\"}}";
        assertEquals(expectedResult, jsonString);
    }

    @Data
    @AllArgsConstructor
    class MyClass {
        @Expose
        private long id;
        @Expose(serialize = true, deserialize = false)
        private String name;
        private transient String other;
        @Expose
        private MySubClass subclass;
    }

    @Data
    @AllArgsConstructor
    class MySubClass {
        @Expose
        private long id;
        @Expose
        private String description;
        @Expose
        private String otherVerboseInfo;
    }
}
