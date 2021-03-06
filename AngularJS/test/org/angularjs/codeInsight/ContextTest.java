package org.angularjs.codeInsight;

import com.intellij.lang.javascript.JSBundle;
import com.intellij.lang.javascript.JSTestUtils;
import com.intellij.lang.javascript.dialects.JSLanguageLevel;
import com.intellij.lang.javascript.inspections.*;
import com.intellij.lang.javascript.psi.JSFunction;
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction;
import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptFieldImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.angularjs.AngularTestUtil;

/**
 * @author Dennis.Ushakov
 */
public class ContextTest extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return AngularTestUtil.getBaseTestDataPath(getClass()) + "context";
  }

  public void testInlineTemplateCompletion2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, myFixture.getProject(),
                                        () -> myFixture.testCompletion("component.ts", "component.after.ts", "angular2.js"));
  }

  public void testInlineTemplateResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("component.after.ts", "angular2.js");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("=\"onComple<caret>tedButton()", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("component.after.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, JSFunction.class);
    });
  }

  public void testInlineTemplateMethodResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("templateMethod.ts", "angular2.js", "customer.ts", "customer2.ts");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("ca<caret>ll()", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("customer.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFunction.class);
    });
  }

  public void testNonInlineTemplateCompletion2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, myFixture.getProject(),
                                        () -> myFixture.testCompletion("template.completion.html", "template.html", "angular2.js", "template.completion.ts"));
  }

  public void testNonInlineTemplateResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("template.html", "angular2.js", "template.ts");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("myCu<caret>", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("template.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFieldImpl.class);
    });
  }

  public void testNonInlineTemplateUsage2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnusedLocalSymbolsInspection.class, JSUnusedGlobalSymbolsInspection.class);
      myFixture.configureByFiles("template.usage.ts", "template.usage.html", "angular2.js");
      myFixture.checkHighlighting();
    });
  }

  public void testNonInlineTemplateMethodResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("templateMethod.html", "angular2.js", "templateMethod.ts", "customer.ts", "customer2.ts");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("ca<caret>ll()", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("customer.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFunction.class);
    });
  }

  public void testNonInlineTemplateDefinitionResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("definition.html", "angular2.js", "definition.ts", "definition2.ts");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("tit<caret>le", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("definition.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFieldImpl.class);
    });
  }

  public void testInlineTemplateDefinitionResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("definition.ts", "angular2.js", "definition2.ts");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("tit<caret>le", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("definition.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFieldImpl.class);
    });
  }

  public void testNonInlineTemplatePropertyResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("definition2.html", "angular2.js", "definition2.ts");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("check<caret>ed", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("definition2.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFieldImpl.class);
    });
  }

  public void testInlineTemplatePropertyResolve2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.configureByFiles("definition2.ts", "angular2.js");
      int offsetBySignature = AngularTestUtil.findOffsetBySignature("check<caret>ed", myFixture.getFile());
      PsiReference ref = myFixture.getFile().findReferenceAt(offsetBySignature);
      assertNotNull(ref);
      PsiElement resolve = ref.resolve();
      assertNotNull(resolve);
      assertEquals("definition2.ts", resolve.getContainingFile().getName());
      assertInstanceOf(resolve, TypeScriptFieldImpl.class);
    });
  }

  public void testInlineTemplateCreateFunction2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunction.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunction.fixed.ts", true);
    });
  }

  public void testInlineTemplateCreateFunctionWithParam2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunctionWithParam.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunctionWithParam.fixed.ts", true);
    });
  }

  public void testInlineTemplateCreateFunctionEventEmitter2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunctionEventEmitter.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunctionEventEmitter.fixed.ts", true);
    });
  }

  public void testInlineTemplateCreateFunctionWithType2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunctionWithType.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunctionWithType.fixed.ts", true);
    });
  }

  public void testInlineTemplateCreateFunctionEventEmitterImplicit2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunctionEventEmitterImplicit.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunctionEventEmitterImplicit.fixed.ts", true);
    });
  }

  public void testInlineTemplateCreateField2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedVariableInspection.class);
      myFixture.getAllQuickFixes("createField.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Field 'todo'"));
      myFixture.checkResultByFile("createField.fixed.ts", true);
    });
  }

  public void testNonInlineTemplateCreateFunction2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunction.html", "createFunction.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunction.ts", "createFunction.fixed.ts", true);
    });
  }

  public void testNonInlineTemplateCreateField2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedVariableInspection.class);
      myFixture.getAllQuickFixes("createField.html", "createField.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Field 'todo'"));
      myFixture.checkResultByFile("createField.ts", "createField.fixed.ts", true);
    });
  }

  public void testNonInlineTemplateCreateFunctionDoubleClass2TypeScript() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSUnresolvedFunctionInspection.class);
      myFixture.getAllQuickFixes("createFunctionDoubleClass.html", "createFunctionDoubleClass.ts", "angular2.js");
      myFixture.launchAction(myFixture.findSingleIntention("Create Method 'fetchFromApi'"));
      myFixture.checkResultByFile("createFunctionDoubleClass.ts", "createFunctionDoubleClass.fixed.ts", true);
    });
  }

  public void testFixSignatureMismatchFromUsageInTemplate() {
    JSTestUtils.testWithinLanguageLevel(JSLanguageLevel.ES6, getProject(), () -> {
      myFixture.enableInspections(JSCheckFunctionSignaturesInspection.class);
      myFixture.getAllQuickFixes("changeMethodSignature.html", "changeMethodSignature.ts", "angular2.js");
      String fixTitle = JSBundle.message("change.method.signature.fix.text", "HeroDetailComponent.save()");
      myFixture.launchAction(myFixture.findSingleIntention(fixTitle));
      myFixture.checkResultByFile("changeMethodSignature.ts", "changeMethodSignature.fixed.ts", true);
    });
  }
}
