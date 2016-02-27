package com.sys1yagi.fragmentcreator;

import com.google.testing.compile.JavaFileObjects;

import com.sys1yagi.fragmentcreator.testtool.AssetsUtils;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class FragmentCreatorProcessorTest {


    @Test
    public void illegalTypeException() throws Exception {
        String javaFile = AssetsUtils.readString("InvalidMainFragment.java");

        JavaFileObject exampleFragment = JavaFileObjects
                .forSourceString("InvalidMainFragment", javaFile);

        assert_().about(javaSource())
                .that(exampleFragment)
                .processedWith(new FragmentCreatorProcessor())
                .failsToCompile()
                .withErrorContaining(
                        "@FragmentCreator can be defined only if the base class is android.app.Fragment or android.support.v4.app.Fragment. : java.lang.Object");
    }

    @Test
    public void optionalOnly() throws Exception {
        String javaFile = AssetsUtils.readString("OptionalOnlyMainFragment.java");

        JavaFileObject exampleFragment = JavaFileObjects
                .forSourceString("MainFragment", javaFile);

        assert_().about(javaSource())
                .that(exampleFragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects
                        .forSourceString("MainFragmentCreator",
                                AssetsUtils.readString("expected/OptionalOnlyMainFragmentCreator.expected")));
    }

    @Test
    public void requireOnly() throws Exception {
        String javaFile = AssetsUtils.readString("RequireOnlyMainFragment.java");

        JavaFileObject exampleFragment = JavaFileObjects
                .forSourceString("MainFragment", javaFile);

        assert_().about(javaSource())
                .that(exampleFragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(
                        JavaFileObjects.forSourceString("MainFragmentCreator",
                                AssetsUtils.readString("expected/RequireOnlyMainFragmentCreator.expected"))
                );
    }

    @Test
    public void compileSuccessSupportV4() throws Exception {
        String javaFile = AssetsUtils.readString("SupportV4MainFragment.java");

        JavaFileObject exampleFragment = JavaFileObjects
                .forSourceString("SupportV4MainFragment", javaFile);

        assert_().about(javaSource())
                .that(exampleFragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError();
    }

    @Test
    public void allType() throws Exception {
        String javaFile = AssetsUtils.readString("AllTypeMainFragment.java");

        JavaFileObject exampleFragment = JavaFileObjects
                .forSourceString("MainFragment", javaFile);

        assert_().about(javaSource())
                .that(exampleFragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects
                        .forSourceString("MainFragmentCreator",
                                AssetsUtils.readString("expected/AllTypeMainFragmentCreator.expected")));

        //TODO
        //    public  void putParcelableArray(java.lang.String key, android.os.Parcelable[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putParcelableArrayList(java.lang.String key, java.util.ArrayList<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
        //    public  void putSparseParcelableArray(java.lang.String key, android.util.SparseArray<? extends android.os.Parcelable> value) { throw new RuntimeException("Stub!"); }
        //    public  void putIntegerArrayList(java.lang.String key, java.util.ArrayList<java.lang.Integer> value) { throw new RuntimeException("Stub!"); }
        //    public  void putStringArrayList(java.lang.String key, java.util.ArrayList<java.lang.String> value) { throw new RuntimeException("Stub!"); }
        //    public  void putCharSequenceArrayList(java.lang.String key, java.util.ArrayList<java.lang.CharSequence> value) { throw new RuntimeException("Stub!"); }
        //    public  void putBooleanArray(java.lang.String key, boolean[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putByteArray(java.lang.String key, byte[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putShortArray(java.lang.String key, short[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putCharArray(java.lang.String key, char[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putIntArray(java.lang.String key, int[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putLongArray(java.lang.String key, long[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putFloatArray(java.lang.String key, float[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putDoubleArray(java.lang.String key, double[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putStringArray(java.lang.String key, java.lang.String[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putCharSequenceArray(java.lang.String key, java.lang.CharSequence[] value) { throw new RuntimeException("Stub!"); }
        //    public  void putBundle(java.lang.String key, android.os.Bundle value) { throw new RuntimeException("Stub!"); }

    }

    @Test
    public void complexPattern() throws Exception {
        String javaFile = AssetsUtils.readString("ComplexSerializableMainFragment.java");

        JavaFileObject exampleFragment = JavaFileObjects.forSourceString("MainFragment", javaFile);

        assert_().about(javaSource())
                .that(exampleFragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects
                        .forSourceString("MainFragmentCreator",
                                AssetsUtils.readString("expected/ComplexSerializableMainFragmentCreator.expected")));
    }

    @Test
    public void defaultValue() throws Exception {
        String javaFile = AssetsUtils.readString("DefaultValueMainFragment.java");
        JavaFileObject fragment = JavaFileObjects.forSourceString("MainFragment", javaFile);

        assert_().about(javaSource())
                .that(fragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects
                        .forSourceString("MainFragmentCreator",
                                AssetsUtils.readString("expected/DefaultValueMainFragmentCreator.expected")));
    }

    //TODO type serializer

    @Test
    public void typeSerializer() throws Exception {
        String javaFile = AssetsUtils.readString("TypeSerializerFragment.java");
        JavaFileObject fragment = JavaFileObjects.forSourceString("MainFragment", javaFile);

        assert_().about(javaSource())
                .that(fragment)
                .processedWith(new FragmentCreatorProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects
                        .forSourceString("MainFragmentCreator",
                                AssetsUtils.readString("expected/TypeSerializerMainFragmentCreator.expected")));
    }

    //TODO unsupported parameter

}
