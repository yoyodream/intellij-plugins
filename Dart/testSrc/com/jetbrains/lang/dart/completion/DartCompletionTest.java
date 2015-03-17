package com.jetbrains.lang.dart.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.jetbrains.lang.dart.DartCodeInsightFixtureTestCase;
import com.jetbrains.lang.dart.util.CaretPositionInfo;
import com.jetbrains.lang.dart.util.DartTestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DartCompletionTest extends DartCodeInsightFixtureTestCase {

  @Override
  protected String getBasePath() {
    return "/completion";
  }

  private void doTest(final String text) {
    final PsiFile file = myFixture.addFileToProject("web/" + getTestName(true) + ".dart", text);
    myFixture.openFileInEditor(file.getVirtualFile());

    final List<CaretPositionInfo> caretPositions = DartTestUtils.extractPositionMarkers(getProject(), myFixture.getEditor().getDocument());
    final String fileText = file.getText();

    for (CaretPositionInfo caretPositionInfo : caretPositions) {
      myFixture.getEditor().getCaretModel().moveToOffset(caretPositionInfo.caretOffset);
      final LookupElement[] lookupElements = myFixture.completeBasic();
      final String fileTextWithCaret =
        fileText.substring(0, caretPositionInfo.caretOffset) + "<caret>" + fileText.substring(caretPositionInfo.caretOffset);
      checkLookupElements(lookupElements,
                          caretPositionInfo.completionEqualsList,
                          caretPositionInfo.completionIncludesList,
                          caretPositionInfo.completionExcludesList,
                          fileTextWithCaret);
      LookupManager.getInstance(getProject()).hideActiveLookup();
    }
  }

  private static void checkLookupElements(@NotNull final LookupElement[] lookupElements,
                                          @Nullable final List<String> equalsList,
                                          @Nullable final List<String> includesList,
                                          @Nullable final List<String> excludesList,
                                          @NotNull final String fileTextWithCaret) {
    final List<String> lookupStrings = new ArrayList<String>();
    for (LookupElement element : lookupElements) {
      lookupStrings.add(element.getLookupString());
    }

    if (equalsList != null) {
      assertSameElements(fileTextWithCaret, lookupStrings, equalsList);
    }

    if (includesList != null) {
      for (String s : includesList) {
        if (!lookupStrings.contains(s)) {
          fail("Missing [" + s + "] in completion list:\n" + StringUtil.join(lookupStrings, "\n") +
               "\n### Caret position:\n" + fileTextWithCaret);
        }
      }
    }

    if (excludesList != null) {
      for (String s : excludesList) {
        if (lookupStrings.contains(s)) {
          fail("Unexpected variant [" + s + "] in completion list:\n" + StringUtil.join(lookupStrings, "\n") +
               "\n### Caret position:\n" + fileTextWithCaret);
        }
      }
    }
  }

  public void testUriBasedDirectives() throws Exception {
    final String sdkLibs =
      "dart:async,dart:collection,dart:convert,dart:core,dart:html,dart:html_common,dart:indexed_db,dart:io,dart:isolate,dart:js," +
      "dart:math,dart:mirrors,dart:nativewrappers,dart:profiler,dart:svg,dart:typed_data,dart:web_audio,dart:web_gl,dart:web_sql";
    addStandardPackage("polymer");
    addStandardPackage("core_elements");
    myFixture.addFileToProject("pubspec.yaml", "");
    myFixture.addFileToProject("web/other1.dart", "");
    myFixture.addFileToProject("web/foo/other2.dart", "");
    myFixture.addFileToProject("web/other3.xml", "");
    doTest("import '''<caret completionEquals='" + sdkLibs + ",foo,other1.dart,package:'>''';\n" +
           "export r\"<caret completionEquals='" + sdkLibs + ",foo,other1.dart,package:'>\n" +
           "part '<caret completionEquals='foo,other1.dart,package:'>'\n" +
           "import 'dart:<caret completionEquals='" + sdkLibs + "'>';\n " +
           "import 'foo/<caret completionEquals='other2.dart'>z';\n" +
           "import 'package:<caret completionEquals='polymer,core_elements'>';\n" +
           "import 'package:polymer/<caret completionEquals='src,polymer.dart,transformer.dart'>");
  }

  public void testKeywords() throws Exception {
    doTest("Object <caret completionEquals=''>a;\n" +
           "var <caret completionEquals=''>a;\n" +
           "void function (Object O<caret completionEquals=''>){}\n");
  }

  public void testNoDuplication() throws Exception {
    myFixture.addFileToProject("web/part.dart", "part of lib; class FooBaz{}");
    doTest("library lib; part 'part.dart'; class FooBar{ const Foob<caret completionEquals='FooBar,FooBaz'> }");
  }

  public void testClassNameCompletion() throws Exception {
    myFixture.addFileToProject("pubspec.yaml", "name: ProjectName\n");
    myFixture.addFileToProject("lib/in_lib.dart", "library lib; export 'exported.dart'; part 'lib_part.dart'; class InLib{}");
    myFixture.addFileToProject("lib/lib_part.dart", "part of lib; class InLibPart{}; class InLibPartHidden{}");
    myFixture.addFileToProject("lib/exported.dart", "class InExported{}");
    myFixture.addFileToProject("lib/in_lib2.dart", "class InLib2{}");
    myFixture.addFileToProject("tool/in_tool.dart", "class InTool{}");
    myFixture.addFileToProject("packages/SomePackage/in_package1.dart", "class InPackage1{} class InPackage1NotShown{}");
    myFixture.addFileToProject("packages/SomePackage/in_package2.dart", "class InPackage2{}");
    myFixture.addFileToProject("web/web.dart", "library web;\n" +
                                               "import 'package:ProjectName/in_lib.dart' hide InLibPartHidden;\n" +
                                               "import 'package:SomePackage/in_package1.dart' show InPackage1;\n" +
                                               "part 'web_part.dart';\n" +
                                               "part '" + getTestName(true) + ".dart';\n" +
                                               "class InWeb{}");
    myFixture.addFileToProject("web/web_part.dart", "part of web; class InWebPart{}");
    doTest("part of web;\n" +
           "class InWebPart2{}\n" +
           "const <caret" +
           " completionIncludes='Object,String,int,bool,Iterable,Set,StateError,InLib,InLibPart,InExported,InPackage1,InWeb,InWebPart,InWebPart2'" +
           " completionExcludes='InLibPartHidden,InPackage1NotShown,InLib2,InTool,InPackage2,SetMixin,FixedLengthListMixin,Point,JsObject'>");

    final LookupElement[] lookupElements = myFixture.complete(CompletionType.BASIC, 2);
    final List<String> includes =
      Arrays.asList("Object", "String", "int", "bool", "Iterable", "Set", "StateError", "InLib", "InLibPart", "InExported", "InPackage1",
                    "InWeb", "InWebPart", "InWebPart2", "InLibPartHidden", "InPackage1NotShown", "InLib2", "InPackage2", "SetMixin",
                    "Point", "JsObject");
    final List<String> excludes = Arrays.asList("PI", "InTool", "FixedLengthListMixin"); // not a class; out of scope; in internal library
    checkLookupElements(lookupElements, null, includes, excludes, "### 2nd basic completion ###");
  }

  public void testWithImportPrefixes() throws Exception {
    myFixture.addFileToProject("web/other.dart", "import 'dart:core'; export 'other2.dart' show inOther2; var inOtherHidden, inOther;");
    myFixture.addFileToProject("web/other2.dart", "var inOther2Hidden, inOther2;");
    myFixture.addFileToProject("web/web_part.dart", "part of web; class InWebPart{}");
    doTest("import 'dart:core' as core;\n" +
           "import 'other.dart' hide inOtherHidden;\n" +
           "foo() {\n" +
           "  core.<caret completionIncludes='int,Object,String' completionExcludes='core,foo,inOtherHidden,inOther2Hidden,inOther,inOther2,JsObject'>x;\n" +
           "  <caret completionIncludes='core,foo,inOther,inOther2' completionExcludes='inOtherHidden,inOther2Hidden,int,Object,String,JsObject'>\n" +
           "}");

    final LookupElement[] lookupElements = myFixture.complete(CompletionType.BASIC, 2);
    final List<String> includes =
      Arrays.asList("Object", "String", "int", "bool", "Iterable", "Set", "StateError", "SetMixin", "Point", "JsObject",
                    "core", "foo", "inOther", "inOtherHidden", "inOther2", "inOther2Hidden");
    checkLookupElements(lookupElements, null, includes, null, "### 2nd basic completion ###");
  }
}
